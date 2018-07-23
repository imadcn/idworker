package com.imadcn.framework.idworker.register.zookeeper;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 机器节点信息
 * 
 * @author yangchao
 * @since 1.0.0
 */
public class NodeInfo {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private Long sessionId;
	private Integer workerId;
	private String ip;
	private String hostName;
	private String pid;
	private Date updateTime;
	private Date createTime;

	public NodeInfo() {
		this(null, null);
	}

	public NodeInfo(Long sessionId, Integer workerId) {
		this.sessionId = sessionId;
		this.workerId = workerId;
		init();
	}

	private void init() {
		try {
			this.ip = InetAddress.getLocalHost().getHostAddress();
			this.hostName = InetAddress.getLocalHost().getHostName();
			this.pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
		} catch (Exception e) {
			logger.error("", e);
		}
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

	@Override
	public String toString() {
		return "NodeInfo [sessionId=" + sessionId + ", workerId=" + workerId + ", ip=" + ip + ", hostName=" + hostName + ", pid=" + pid + ", updateTime=" + updateTime + ", createTime=" + createTime + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pid == null) ? 0 : pid.hashCode());
		result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
		result = prime * result + ((workerId == null) ? 0 : workerId.hashCode());
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
		if (pid == null) {
			if (other.pid != null)
				return false;
		} else if (!pid.equals(other.pid))
			return false;
		if (sessionId == null) {
			if (other.sessionId != null)
				return false;
		} else if (!sessionId.equals(other.sessionId))
			return false;
		if (workerId == null) {
			if (other.workerId != null)
				return false;
		} else if (!workerId.equals(other.workerId))
			return false;
		return true;
	}

}
