package com.imadcn.framework.idworker.register.zookeeper;

import java.io.Serializable;
import java.util.Date;

/**
 * 机器节点信息
 * 
 * @author imadcn
 * @since 1.0.0
 */
public class NodeInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nodeId;
    private String groupName;
    private Integer workerId;
    private String ip;
    private String hostName;
    private Date updateTime;
    private Date createTime;
    @Deprecated
    private String pid;
    @Deprecated
    private Long sessionId;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Integer workerId) {
        this.workerId = workerId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Deprecated
    public String getPid() {
        return pid;
    }

    @Deprecated
    public void setPid(String pid) {
        this.pid = pid;
    }

    @Deprecated
    public Long getSessionId() {
        return sessionId;
    }

    @Deprecated
    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "NodeInfo [nodeId=" + nodeId + ", groupName=" + groupName + ", workerId=" + workerId + ", ip=" + ip
            + ", hostName=" + hostName + ", updateTime=" + updateTime + ", createTime=" + createTime + "]";
    }

}
