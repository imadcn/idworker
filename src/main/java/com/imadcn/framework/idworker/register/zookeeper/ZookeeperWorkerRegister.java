package com.imadcn.framework.idworker.register.zookeeper;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.jboss.netty.handler.timeout.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.imadcn.framework.idworker.config.ApplicationConfiguration;
import com.imadcn.framework.idworker.exception.RegException;
import com.imadcn.framework.idworker.register.WorkerRegister;
import com.imadcn.framework.idworker.registry.CoordinatorRegistryCenter;

/**
 * 机器信息注册
 * 
 * @author yangchao
 * @since 2017-10-18
 */
public class ZookeeperWorkerRegister implements WorkerRegister {

	private static final Logger logger = LoggerFactory.getLogger(ZookeeperWorkerRegister.class);

	/**
	 * 最大机器数
	 */
	private static final long MAX_WORKER_NUM = 1024;
	/**
	 * 加锁最大等待时间
	 */
	private static final int MAX_LOCK_WAIT_TIME_MS = 30 * 1000;

	private final CoordinatorRegistryCenter regCenter;
	/**
	 * zk节点信息
	 */
	private final NodePath nodePath;

	public ZookeeperWorkerRegister(CoordinatorRegistryCenter regCenter, ApplicationConfiguration applicationConfiguration) {
		this.regCenter = regCenter;
		this.nodePath = new NodePath(applicationConfiguration.getGroup());
	}

	/**
	 * 向zookeeper注册workerId
	 * 
	 * @return workerId workerId
	 */
	@Override
	public long register() {
		CuratorFramework client = (CuratorFramework) regCenter.getRawClient();
		InterProcessMutex lock = new InterProcessMutex(client, nodePath.getGroupPath());
		try {
			int numOfChildren = regCenter.getNumChildren(nodePath.getWorkerPath());
			if (numOfChildren < MAX_WORKER_NUM) {
				if (!lock.acquire(MAX_LOCK_WAIT_TIME_MS, TimeUnit.MILLISECONDS)) {
					String message = String.format("acquire lock failed after %s ms.", MAX_LOCK_WAIT_TIME_MS);
					throw new TimeoutException(message);
				}
				long sessionId = client.getZookeeperClient().getZooKeeper().getSessionId();
				logger.debug("current session id is : {}", sessionId);
				List<String> children = regCenter.getChildrenKeys(nodePath.getWorkerPath());
				for (int workerId = 0; workerId < MAX_WORKER_NUM; workerId++) {
					String workderIdStr = String.valueOf(workerId);
					String key = nodePath.getWorkerPath() + "/" + workerId;
					NodeInfo currentNode = new NodeInfo(sessionId, workerId);
					currentNode.setCreateTime(new Date());
					currentNode.setUpdateTime(new Date());
					if (!children.contains(workderIdStr)) {
						String value = jsonizeNodeInfo(currentNode);
						logger.info("snowflake worker register with : {}", value);
						regCenter.persistEphemeral(key, value);
						// 将workerId保存进nodePath 
						nodePath.setWorkerId(workerId);
						return workerId;
					} else {
						String value = regCenter.get(key);
						NodeInfo cacheNode = JSON.parseObject(value, NodeInfo.class);
						if (currentNode.equals(cacheNode)) {
							logger.info("use cached nodeinfo, {}", value);
							cacheNode.setUpdateTime(new Date());
							regCenter.persistEphemeral(key, jsonizeNodeInfo(cacheNode));
							return cacheNode.getWorkerId();
						}
					}
				}
			}
			throw new RegException("max worker num reached. register failed");
		} catch (Exception e) {
			logger.error("", e);
			throw new IllegalStateException(e.getMessage(), e);
		} finally {
			try {
				lock.release();
			} catch (Exception ignored) {
				logger.error("", ignored);
			}
		}
	}
	
	/**
	 * 添加连接监听
	 * @param listener zk状态监听listener
	 */
	public void addConnectionLJistener(ConnectionStateListener listener) {
		CuratorFramework client = (CuratorFramework) regCenter.getRawClient();
		client.getConnectionStateListenable().addListener(listener);
	}
	
	/**
	 * 关闭注册
	 */
	@Override
	public synchronized void logout() {
		// 移除注册节点
		regCenter.remove(nodePath.getWorkerIdPath());
		// 关闭连接
		regCenter.close();
	}
	
	private String jsonizeNodeInfo(NodeInfo nodeInfo) {
		String dateFormat = "yyyy-MM-dd HH:mm:ss";
		return JSON.toJSONStringWithDateFormat(nodeInfo, dateFormat, SerializerFeature.WriteDateUseDateFormat);
	}
}
