package com.imadcn.framework.idworker.register.zookeeper;

import java.io.Serializable;
import java.util.Date;

/**
 * 机器节点信息
 * 
 * @author yangchao
 * @since 1.0.0
 */
public class NodeInfo implements Serializable {

	private static final long serialVersionUID = 1L;

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

	public NodeInfo() {
	}

	@Deprecated
	public NodeInfo(Long sessionId, Integer workerId) {
		this.sessionId = sessionId;
		this.workerId = workerId;
	}
	
	public NodeInfo(String ip, String hostName, String groupName) {
		this.ip = ip;
		this.hostName = hostName;
		this.groupName = groupName;
	}

	public NodeInfo(String ip, String hostName, String groupName, Integer workerId) {
		this.ip = ip;
		this.hostName = hostName;
		this.groupName = groupName;
		this.workerId = workerId;
	}

	public Long getSessionId() {
		return sessionId;
	}

	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
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

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
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
	
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Override
	public String toString() {
		return "NodeInfo [sessionId=" + sessionId + ", workerId=" + workerId + ", ip=" + ip + ", hostName=" + hostName + ", pid=" + pid + ", updateTime=" + updateTime + ", createTime=" + createTime + ", groupName=" + groupName + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
		result = prime * result + ((hostName == null) ? 0 : hostName.hashCode());
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeInfo other = (NodeInfo) obj;
		if (groupName == null) {
			if (other.groupName != null)
				return false;
		} else if (!groupName.equals(other.groupName))
			return false;
		if (hostName == null) {
			if (other.hostName != null)
				return false;
		} else if (!hostName.equals(other.hostName))
			return false;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		return true;
	}
}
