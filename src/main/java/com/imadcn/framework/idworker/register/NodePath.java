package com.imadcn.framework.idworker.register;

/**
 * 机器节点Path
 * @author yangchao
 * @since 2017-10-18
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
	
}
