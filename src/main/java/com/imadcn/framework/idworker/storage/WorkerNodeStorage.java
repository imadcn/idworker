package com.imadcn.framework.idworker.storage;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.jboss.netty.handler.timeout.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.imadcn.framework.idworker.registry.CoordinatorRegistryCenter;

/**
 * 机器信息存储
 * @author yangchao
 * @since 2017-10-18
 */
public class WorkerNodeStorage {
	
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
//	private final String groupName;
	private final WorkerNodePath nodePath;

	public WorkerNodeStorage(CoordinatorRegistryCenter regCenter, String groupName) {
		this.regCenter = regCenter;
//		this.groupName = groupName;
		this.nodePath = new WorkerNodePath(groupName);
	}
	
	public long register() {
		long registerWorkerId = 0L;
		int numOfChildren = regCenter.getNumChildren(nodePath.getWorkerPath());
		if (numOfChildren < MAX_WORKER_NUM) {
			CuratorFramework client = (CuratorFramework) regCenter.getRawClient();
			InterProcessMutex lock = new InterProcessMutex(client, nodePath.getGroupPath());
			try {
				if (!lock.acquire(MAX_LOCK_WAIT_TIME_MS, TimeUnit.MILLISECONDS)) {
					String message = String.format("acquire lock failed after %s ms.", MAX_LOCK_WAIT_TIME_MS);
					throw new TimeoutException(message);
				}
				List<String> children = regCenter.getChildrenKeys(nodePath.getWorkerPath());
				for (int workerId = 0; workerId < MAX_WORKER_NUM; workerId++) {
					String workderIdStr = String.valueOf(workerId);
					if (!children.contains(workderIdStr)) {
						String key = nodePath.getWorkerPath() + "/" + workerId;
						String value = JSON.toJSONString(new WorkerNodeInfo(workerId));
						logger.info("snowflake worker register with : {}", value);
						regCenter.persistEphemeral(key, value);
						registerWorkerId = workerId;
						break;
					}
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
		} else {
			throw new RuntimeException("max worker num reached. register failed");
		}
		return registerWorkerId;
	}
}
