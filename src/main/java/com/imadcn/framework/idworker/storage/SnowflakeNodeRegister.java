package com.imadcn.framework.idworker.storage;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.jboss.netty.handler.timeout.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.imadcn.framework.idworker.config.ApplicationConfiguration;
import com.imadcn.framework.idworker.exception.RegException;
import com.imadcn.framework.idworker.registry.CoordinatorRegistryCenter;

/**
 * 机器信息注册
 * 
 * @author yangchao
 * @since 2017-10-18
 */
public class SnowflakeNodeRegister {

	private final Logger logger = LoggerFactory.getLogger(getClass());

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
	private final SnowflakeNodePath nodePath;

	public SnowflakeNodeRegister(CoordinatorRegistryCenter regCenter, ApplicationConfiguration applicationConfiguration) {
		this.regCenter = regCenter;
		this.nodePath = new SnowflakeNodePath(applicationConfiguration.getGroup());
	}

	/**
	 * 向zookeeper注册workerId
	 * 
	 * @return workerId
	 */
	public long register() {
		long registerWorkerId = -1L;
		CuratorFramework client = (CuratorFramework) regCenter.getRawClient();
		InterProcessMutex lock = new InterProcessMutex(client, nodePath.getGroupPath());
		try {
			int numOfChildren = regCenter.getNumChildren(nodePath.getWorkerPath());
			if (numOfChildren < MAX_WORKER_NUM) {
				if (!lock.acquire(MAX_LOCK_WAIT_TIME_MS, TimeUnit.MILLISECONDS)) {
					String message = String.format("acquire lock failed after %s ms.", MAX_LOCK_WAIT_TIME_MS);
					throw new TimeoutException(message);
				}
				List<String> children = regCenter.getChildrenKeys(nodePath.getWorkerPath());
				for (int workerId = 0; workerId < MAX_WORKER_NUM; workerId++) {
					String workderIdStr = String.valueOf(workerId);
					if (!children.contains(workderIdStr)) {
						String key = nodePath.getWorkerPath() + "/" + workerId;
						String value = JSON.toJSONString(new SnowflakeNodeInfo(workerId));
						logger.info("snowflake worker register with : {}", value);
						regCenter.persistEphemeral(key, value);
						registerWorkerId = workerId;
						break;
					}
				}
			} else {
				throw new RegException("max worker num reached. register failed");
			}
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
		return registerWorkerId;
	}
}
