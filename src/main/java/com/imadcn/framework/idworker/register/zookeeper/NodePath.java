package com.imadcn.framework.idworker.register.zookeeper;

/**
 * 机器节点Path
 * @author yangchao
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
		result = prime * result + (int) (sessionId ^ (sessionId >>> 32));
		result = prime * result + (int) (workerId ^ (workerId >>> 32));
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
		NodePath other = (NodePath) obj;
		if (groupName == null) {
			if (other.groupName != null)
				return false;
		} else if (!groupName.equals(other.groupName))
			return false;
		if (sessionId != other.sessionId)
			return false;
		if (workerId != other.workerId)
			return false;
		return true;
	}
	
}
