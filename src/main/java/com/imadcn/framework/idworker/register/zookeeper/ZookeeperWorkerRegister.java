package com.imadcn.framework.idworker.register.zookeeper;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.jboss.netty.handler.timeout.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.imadcn.framework.idworker.config.ApplicationConfiguration;
import com.imadcn.framework.idworker.exception.RegException;
import com.imadcn.framework.idworker.register.WorkerRegister;
import com.imadcn.framework.idworker.registry.CoordinatorRegistryCenter;
import com.imadcn.framework.idworker.util.HostUtils;

/**
 * 机器信息注册
 * 
 * @author yangchao
 * @since 1.0.0
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
	/**
	 * 注册中心工具
	 */
	private final CoordinatorRegistryCenter regCenter;
	/**
	 * 注册文件
	 */
	private String registryFile;
	/**
	 * zk节点信息
	 */
	private final NodePath nodePath;

	public ZookeeperWorkerRegister(CoordinatorRegistryCenter regCenter, ApplicationConfiguration applicationConfiguration) {
		this.regCenter = regCenter;
		this.nodePath = new NodePath(applicationConfiguration.getGroup());
		if (StringUtils.isEmpty(applicationConfiguration.getRegistryFile())) {
			this.registryFile = getDefaultFilePath(nodePath.getGroupName());
		} else {
			this.registryFile = applicationConfiguration.getRegistryFile();
		}
	}

	/**
	 * 向zookeeper注册workerId
	 * 
	 * @return workerId workerId
	 */
	@Override
	public long register() {
		InterProcessMutex lock = null;
		try {
			CuratorFramework client = (CuratorFramework) regCenter.getRawClient();
			lock = new InterProcessMutex(client, nodePath.getGroupPath());
			int numOfChildren = regCenter.getNumChildren(nodePath.getWorkerPath());
			if (numOfChildren < MAX_WORKER_NUM) {
				if (!lock.acquire(MAX_LOCK_WAIT_TIME_MS, TimeUnit.MILLISECONDS)) {
					String message = String.format("acquire lock failed after %s ms.", MAX_LOCK_WAIT_TIME_MS);
					throw new TimeoutException(message);
				}
				NodeInfo localNodeInfo = getLocalNodeInfo();
				List<String> children = regCenter.getChildrenKeys(nodePath.getWorkerPath());
				// 有本地缓存的节点信息，同时ZK也有这条数据
				if (localNodeInfo != null && children.contains(String.valueOf(localNodeInfo.getWorkerId()))) {
					String key = getNodePathKey(nodePath, localNodeInfo.getWorkerId());
					String zkNodeInfoJson = regCenter.get(key);
					NodeInfo zkNodeInfo = createNodeInfoFromJsonStr(zkNodeInfoJson);
					if (checkNodeInfo(localNodeInfo, zkNodeInfo)) {
						// 更新ZK节点信息，保存本地缓存，开启定时上报任务
						nodePath.setWorkerId(zkNodeInfo.getWorkerId());
						zkNodeInfo.setUpdateTime(new Date());
						updateZookeeperNodeInfo(key, zkNodeInfo);
						saveLocalNodeInfo(zkNodeInfo);
						executeUploadNodeInfoTask(key, zkNodeInfo);
						return zkNodeInfo.getWorkerId();
					}
				} 
				// 无本地信息或者缓存数据不匹配，开始向ZK申请节点机器ID
				for (int workerId = 0; workerId < MAX_WORKER_NUM; workerId++) {
					String workerIdStr = String.valueOf(workerId);
					if (!children.contains(workerIdStr)) { // 申请成功
						NodeInfo applyNodeInfo = createNodeInfo(nodePath.getGroupName(), workerId);
						nodePath.setWorkerId(applyNodeInfo.getWorkerId());
						// 保存ZK节点信息，保存本地缓存，开启定时上报任务
						saveZookeeperNodeInfo(nodePath.getWorkerIdPath(), applyNodeInfo);
						saveLocalNodeInfo(applyNodeInfo);
						executeUploadNodeInfoTask(nodePath.getWorkerIdPath(), applyNodeInfo);
						return applyNodeInfo.getWorkerId();
					}
				}
			}
			throw new RegException("max worker num reached. register failed");
		} catch (RegException e) {
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw new IllegalStateException(e.getMessage(), e);
		} finally {
			try {
				if (lock != null) {
					lock.release();
				}
			} catch (Exception ignored) {
				logger.error("", ignored);
			}
		}
	}
	
	/**
	 * 添加连接监听
	 * @param listener zk状态监听listener
	 */
	@Deprecated
	public void addConnectionListener(ConnectionStateListener listener) {
		CuratorFramework client = (CuratorFramework) regCenter.getRawClient();
		client.getConnectionStateListenable().addListener(listener);
	}

	/**
	 * 关闭注册
	 */
	@Override
	public synchronized void logout() {
		CuratorFramework client = (CuratorFramework) regCenter.getRawClient();
		if (client == null) {
			return;
		}
		if (client.getState() == CuratorFrameworkState.STARTED) {
			// 移除注册节点（最大程度的自动释放资源）
			regCenter.remove(nodePath.getWorkerIdPath()); 
			// 关闭连接
			regCenter.close();
		}
	}

	/**
	 * 检查节点信息
	 * @param localNodeInfo 本地缓存节点信息
	 * @param zkNodeInfo zookeeper节点信息
	 * @return
	 */
	private boolean checkNodeInfo(NodeInfo localNodeInfo, NodeInfo zkNodeInfo) {
		try {
			// NodeId、IP、HostName、GroupName 相等（本地缓存==ZK数据）
			if (!zkNodeInfo.getNodeId().equals(localNodeInfo.getNodeId())) {
				return false;
			}
			if (!zkNodeInfo.getIp().equals(localNodeInfo.getIp())) {
				return false;
			}
			if (!zkNodeInfo.getHostName().equals(localNodeInfo.getHostName())) {
				return false;
			}
			if (!zkNodeInfo.getGroupName().equals(localNodeInfo.getGroupName())) {
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.error("check node info error, {}", e);
			return false;
		}
		
	}
	
	/**
	 * 更新节点信息Task
	 * @param key zk path
	 * @param nodeInfo 节点信息
	 */
	private void executeUploadNodeInfoTask(final String key, final NodeInfo nodeInfo) {
		Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r, "upload node info task thread");
                thread.setDaemon(true);
                return thread;
			}
		}).scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				updateZookeeperNodeInfo(key, nodeInfo);
			}
		}, 3L, 3L, TimeUnit.SECONDS);
	}
	
	/**
	 * 获取节点ZK Path KEy
	 * @param nodePath 节点路径信息
	 * @param workerId 节点机器ID
	 * @return
	 */
	private String getNodePathKey(NodePath nodePath, Integer workerId) {
		StringBuilder builder = new StringBuilder();
		builder.append(nodePath.getWorkerPath()).append("/");
		builder.append(workerId);
		return builder.toString();
	}
	
	/**
	 * 保存ZK节点信息
	 * @param key
	 * @param nodeInfo
	 */
	private void saveZookeeperNodeInfo(String key, NodeInfo nodeInfo) {
		regCenter.persist(key, jsonizeNodeInfo(nodeInfo));
	}
	
	/**
	 * 刷新ZK节点信息（修改updateTime）
	 * @param key
	 * @param nodeInfo
	 */
	private void updateZookeeperNodeInfo(String key, NodeInfo nodeInfo) {
		try {
			nodeInfo.setUpdateTime(new Date());
			regCenter.persist(key, jsonizeNodeInfo(nodeInfo));
		} catch (Exception e) {
			logger.debug("update zookeeper node info error, {}", e);
		}
		
	}
	
	/**
	 * 缓存机器节点信息至本地
	 * @param nodeInfo 机器节点信息
	 */
	private void saveLocalNodeInfo(NodeInfo nodeInfo) {
		try {
			File nodeInfoFile = new File(registryFile);
			String nodeInfoJson = jsonizeNodeInfo(nodeInfo);
			FileUtils.writeStringToFile(nodeInfoFile, nodeInfoJson, StandardCharsets.UTF_8);
		} catch (IOException e) {
			logger.error("save node info cache error, {}", e);
		}
	}
	
	/**
	 * 读取本地缓存机器节点
	 * @return 机器节点信息
	 */
	private NodeInfo getLocalNodeInfo() {
		try {
			File nodeInfoFile = new File(registryFile);
			if (nodeInfoFile.exists()) {
				String nodeInfoJson = FileUtils.readFileToString(nodeInfoFile, StandardCharsets.UTF_8);
				NodeInfo nodeInfo = createNodeInfoFromJsonStr(nodeInfoJson);
				return nodeInfo;
			}
		} catch (Exception e) {
			logger.error("read node info cache error, {}", e);
		}
		return null;
	}
	
	/**
	 * 初始化节点信息
	 * @param groupName 分组名
	 * @param workerId 机器号
	 * @return 节点信息
	 * @throws UnknownHostException
	 */
	private NodeInfo createNodeInfo(String groupName, Integer workerId) throws UnknownHostException {
		NodeInfo nodeInfo = new NodeInfo();
		nodeInfo.setNodeId(genNodeId());
		nodeInfo.setGroupName(groupName);
		nodeInfo.setWorkerId(workerId);
		nodeInfo.setIp(HostUtils.getLocalIP());
		nodeInfo.setHostName(HostUtils.getLocalHostName());
		nodeInfo.setCreateTime(new Date());
		nodeInfo.setUpdateTime(new Date());
		return nodeInfo;
	}
	
	/**
	 * 通过节点信息JSON字符串反序列化节点信息
	 * @param jsonStr 节点信息JSON字符串
	 * @return 节点信息
	 */
	private NodeInfo createNodeInfoFromJsonStr(String jsonStr) {
		NodeInfo nodeInfo = JSON.parseObject(jsonStr, NodeInfo.class);
		return nodeInfo;
	}
	
	/**
	 * 节点信息转json字符串
	 * @param nodeInfo 节点信息
	 * @return json字符串
	 */
	private String jsonizeNodeInfo(NodeInfo nodeInfo) {
		String dateFormat = "yyyy-MM-dd HH:mm:ss";
		return JSON.toJSONStringWithDateFormat(nodeInfo, dateFormat, SerializerFeature.WriteDateUseDateFormat);
	}
	
	/**
	 * 获取本地节点缓存文件路径
	 * @param groupName 分组名
	 * @return 文件路径
	 */
	private String getDefaultFilePath(String groupName) {
		StringBuilder builder = new StringBuilder();
		builder.append(".").append(File.separator).append("tmp");
		builder.append(File.separator).append("idworker");
		builder.append(File.separator).append(groupName).append(".cache");
		return builder.toString();
	}
	
	/**
	 * 获取节点唯一ID （基于UUID）
	 * @return 节点唯一ID
	 */
	private String genNodeId() {
		return UUID.randomUUID().toString().replace("-", "").toLowerCase();
	}
}
