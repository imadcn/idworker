package com.imadcn.framework.idworker.register.zookeeper;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.util.StringUtils;

import com.imadcn.framework.idworker.common.SerializeStrategy;
import com.imadcn.framework.idworker.config.ApplicationConfiguration;
import com.imadcn.framework.idworker.exception.ConfigException;
import com.imadcn.framework.idworker.exception.RegException;
import com.imadcn.framework.idworker.register.AbstractWorkerRegister;
import com.imadcn.framework.idworker.registry.CoordinatorRegistryCenter;
import com.imadcn.framework.idworker.serialize.json.FastJsonSerializer;
import com.imadcn.framework.idworker.serialize.json.JacksonSerializer;

/**
 * 机器信息注册
 * 
 * @author imadcn
 * @since 1.0.0
 */
public class ZookeeperWorkerRegister extends AbstractWorkerRegister {

    public ZookeeperWorkerRegister(CoordinatorRegistryCenter regCenter,
        ApplicationConfiguration applicationConfiguration) {
        setRegCenter(regCenter);
        setNodePath(new NodePath(applicationConfiguration.getGroup()));
        setDurable(applicationConfiguration.isDurable());
        setCachable(applicationConfiguration.isCachable());

        if (!isCachable() && isDurable()) {
            logger.warn("「durable」&& 「NONE cachable」 may become a waste");
        }

        if (SerializeStrategy.SERIALIZE_JSON_FASTJSON.equals(applicationConfiguration.getSerialize())) {
            setJsonSerializer(new FastJsonSerializer<>());
        } else if (SerializeStrategy.SERIALIZE_JSON_JACKSON.equals(applicationConfiguration.getSerialize())) {
            setJsonSerializer(new JacksonSerializer<>());
        } else {
            throw new ConfigException("unsupported serialize strategy: %s, use: [fastjson / jackson]",
                applicationConfiguration.getSerialize());
        }
        if (StringUtils.isEmpty(applicationConfiguration.getRegistryFile())) {
            setRegistryFile(getDefaultFilePath(getNodePath().getGroupName()));
        } else {
            setRegistryFile(applicationConfiguration.getRegistryFile());
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
            CuratorFramework client = (CuratorFramework) getRegCenter().getRawClient();
            lock = new InterProcessMutex(client, getNodePath().getGroupPath());
            int numOfChildren = getRegCenter().getNumChildren(getNodePath().getWorkerPath());
            if (numOfChildren < MAX_WORKER_NUM) {
                if (!lock.acquire(MAX_LOCK_WAIT_TIME_MS, TimeUnit.MILLISECONDS)) {
                    String message = String.format("acquire lock failed after %s ms.", MAX_LOCK_WAIT_TIME_MS);
                    throw new TimeoutException(message);
                }
                NodeInfo localNodeInfo = getLocalNodeInfo();
                List<String> children = getRegCenter().getChildrenKeys(getNodePath().getWorkerPath());
                /*
                 * 有本地缓存的节点信息，同时ZK也有这条数据 2021.12 新增判断是否依赖本地缓存，如果不依赖本地缓存，则每次都会申请新的id
                 */
                if (isCachable() && localNodeInfo != null
                    && children.contains(String.valueOf(localNodeInfo.getWorkerId()))) {
                    String key = getNodePathKey(getNodePath(), localNodeInfo.getWorkerId());
                    String zkNodeInfoJson = getRegCenter().get(key);
                    NodeInfo zkNodeInfo = createNodeInfoFromJsonStr(zkNodeInfoJson);
                    if (checkNodeInfo(localNodeInfo, zkNodeInfo)) {
                        // 更新ZK节点信息，保存本地缓存，开启定时上报任务
                        getNodePath().setWorkerId(zkNodeInfo.getWorkerId());
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
                        NodeInfo applyNodeInfo = createNodeInfo(getNodePath().getGroupName(), workerId);
                        getNodePath().setWorkerId(applyNodeInfo.getWorkerId());
                        // 保存ZK节点信息，保存本地缓存，开启定时上报任务
                        saveZookeeperNodeInfo(getNodePath().getWorkerIdPath(), applyNodeInfo);
                        saveLocalNodeInfo(applyNodeInfo);
                        executeUploadNodeInfoTask(getNodePath().getWorkerIdPath(), applyNodeInfo);
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
     * 关闭注册
     */
    @Override
    public synchronized void logout() {
        CuratorFramework client = (CuratorFramework) getRegCenter().getRawClient();
        if (client != null && client.getState() == CuratorFrameworkState.STARTED) {
            // 移除注册节点（最大程度的自动释放资源）
            getRegCenter().remove(getNodePath().getWorkerIdPath());
            // 关闭连接
            getRegCenter().close();
        }
    }

    /**
     * 更新节点信息Task
     * 
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
     * 保存ZK节点信息
     * 
     * @param key
     * @param nodeInfo
     */
    private void saveZookeeperNodeInfo(String key, NodeInfo nodeInfo) throws Exception {
        if (isDurable()) {
            getRegCenter().persist(key, jsonizeNodeInfo(nodeInfo));
        } else {
            getRegCenter().persistEphemeral(key, jsonizeNodeInfo(nodeInfo));
        }
    }

    /**
     * 刷新ZK节点信息（修改updateTime）
     * 
     * @param key
     * @param nodeInfo
     */
    private void updateZookeeperNodeInfo(String key, NodeInfo nodeInfo) {
        try {
            nodeInfo.setUpdateTime(new Date());
            if (isDurable()) {
                getRegCenter().persist(key, jsonizeNodeInfo(nodeInfo));
            } else {
                getRegCenter().persistEphemeral(key, jsonizeNodeInfo(nodeInfo));
            }
        } catch (Exception e) {
            logger.debug("update zookeeper node info error, {}", e);
        }
    }
}
