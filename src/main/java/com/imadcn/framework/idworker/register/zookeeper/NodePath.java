package com.imadcn.framework.idworker.register.zookeeper;

/**
 * 机器节点Path
 * 
 * @author imadcn
 * @since 1.0.0
 */
public class NodePath {

    private static final String WORKER_NODE = "worker";

    /**
     * workerId 分组
     */
    private String groupName;

    /**
     * 机器编号
     */
    private long workerId;

    private long sessionId = -1L;

    public NodePath(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupPath() {
        return String.format("/%s", groupName);
    }

    public String getWorkerPath() {
        return String.format("/%s/%s", groupName, WORKER_NODE);
    }

    public String getWorkerIdPath() {
        return String.format("/%s/%s/%s", groupName, WORKER_NODE, workerId);
    }

    public String getGroupName() {
        return groupName;
    }

    public long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(long workerId) {
        this.workerId = workerId;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

}
