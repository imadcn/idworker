package com.imadcn.framework.idworker.register;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imadcn.framework.idworker.register.zookeeper.NodeInfo;
import com.imadcn.framework.idworker.register.zookeeper.NodePath;
import com.imadcn.framework.idworker.registry.CoordinatorRegistryCenter;
import com.imadcn.framework.idworker.serialize.json.JsonSerializer;
import com.imadcn.framework.idworker.util.HostUtils;

/**
 * Worker注册
 * 
 * @author imadcn
 * @since 1.6.0
 */
public abstract class AbstractWorkerRegister implements WorkerRegister {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 最大机器数
     */
    public static final long MAX_WORKER_NUM = 1024;
    /**
     * 加锁最大等待时间
     */
    public static final int MAX_LOCK_WAIT_TIME_MS = 30 * 1000;
    /**
     * 注册中心工具
     */
    private CoordinatorRegistryCenter regCenter;
    /**
     * 注册文件
     */
    private String registryFile;
    /**
     * zk节点信息
     */
    private NodePath nodePath;
    /**
     * zk节点是否持久化存储
     */
    private boolean durable;
    /**
     * Json序列化
     */
    private JsonSerializer<NodeInfo> jsonSerializer;

    /**
     * 是否使用本地缓存（如果不依赖本地缓存，那么每次都会申请一个新的workerId）
     */
    private boolean cachable;

    /**
     * 检查节点信息
     * 
     * @param localNodeInfo 本地缓存节点信息
     * @param zkNodeInfo zookeeper节点信息
     * @return 节点信息相同返回true，否则返回false
     */
    protected boolean checkNodeInfo(NodeInfo localNodeInfo, NodeInfo zkNodeInfo) {
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
     * 缓存机器节点信息至本地
     * 
     * @param nodeInfo 机器节点信息
     * @throws Exception 系统异常
     */
    protected void saveLocalNodeInfo(NodeInfo nodeInfo) throws Exception {
        try {
            File nodeInfoFile = new File(getRegistryFile());
            String nodeInfoJson = jsonizeNodeInfo(nodeInfo);
            FileUtils.writeStringToFile(nodeInfoFile, nodeInfoJson, StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("save node info cache error, {}", e);
        }
    }

    /**
     * 读取本地缓存机器节点
     * 
     * @return 机器节点信息
     */
    protected NodeInfo getLocalNodeInfo() {
        try {
            File nodeInfoFile = new File(getRegistryFile());
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
     * 
     * @param groupName 分组名
     * @param workerId 机器号
     * @return 节点信息
     * @throws UnknownHostException 未知HOST异常
     */
    protected NodeInfo createNodeInfo(String groupName, Integer workerId) throws UnknownHostException {
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
     * 
     * @param jsonStr 节点信息JSON字符串
     * @return 节点信息
     * @throws Exception 系统异常
     */
    protected NodeInfo createNodeInfoFromJsonStr(String jsonStr) throws Exception {
        return getJsonSerializer().parseObject(jsonStr, NodeInfo.class);
    }

    /**
     * 节点信息转json字符串
     * 
     * @param nodeInfo 节点信息
     * @return json字符串
     * @throws Exception 系统异常
     */
    protected String jsonizeNodeInfo(NodeInfo nodeInfo) throws Exception {
        return getJsonSerializer().toJsonString(nodeInfo);
    }

    /**
     * 获取本地节点缓存文件路径
     * 
     * @param groupName 分组名
     * @return 文件路径
     */
    protected String getDefaultFilePath(String groupName) {
        StringBuilder builder = new StringBuilder();
        builder.append(".").append(File.separator).append("tmp");
        builder.append(File.separator).append("idworker");
        builder.append(File.separator).append(groupName).append(".cache");
        return builder.toString();
    }

    /**
     * 获取节点唯一ID （基于UUID）
     * 
     * @return 节点唯一ID
     */
    protected String genNodeId() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    /**
     * 获取节点ZK Path Key
     * 
     * @param nodePath 节点路径信息
     * @param workerId 节点机器ID
     * @return 节点PATH的KEY
     */
    protected String getNodePathKey(NodePath nodePath, Integer workerId) {
        StringBuilder builder = new StringBuilder();
        builder.append(nodePath.getWorkerPath()).append("/");
        builder.append(workerId);
        return builder.toString();
    }

    public CoordinatorRegistryCenter getRegCenter() {
        return regCenter;
    }

    public void setRegCenter(final CoordinatorRegistryCenter regCenter) {
        this.regCenter = regCenter;
    }

    public String getRegistryFile() {
        return registryFile;
    }

    public void setRegistryFile(String registryFile) {
        this.registryFile = registryFile;
    }

    public boolean isDurable() {
        return durable;
    }

    public void setDurable(boolean durable) {
        this.durable = durable;
    }

    public JsonSerializer<NodeInfo> getJsonSerializer() {
        return jsonSerializer;
    }

    public void setJsonSerializer(JsonSerializer<NodeInfo> jsonSerializer) {
        this.jsonSerializer = jsonSerializer;
    }

    public boolean isCachable() {
        return cachable;
    }

    public void setNodePath(final NodePath nodePath) {
        this.nodePath = nodePath;
    }

    public void setCachable(boolean cachable) {
        this.cachable = cachable;
    }

    public NodePath getNodePath() {
        return nodePath;
    }
}
