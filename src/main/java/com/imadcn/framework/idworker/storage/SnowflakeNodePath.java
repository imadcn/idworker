package com.imadcn.framework.idworker.storage;

/**
 * 机器节点Path
 * @author yangchao
 * @since 2017-10-18
 */
public class SnowflakeNodePath {
	
	private static final String WORKER_NODE = "worker";
	
	private String groupName;
	
	private long workderId; // 机器编号
	
	public SnowflakeNodePath(String groupName) {
		this.groupName = groupName;
	}

	public SnowflakeNodePath(long workderId) {
		this.workderId = workderId;
	}
		
	public String getGroupPath() {
		return String.format("/%s", groupName);
	} 
	
	public String getWorkerPath() {
		return String.format("/%s/%s", groupName, WORKER_NODE);
	} 
	
	public String getWorkerIdPath() {
		return String.format("/%s/%s/%s", groupName, WORKER_NODE, workderId);
	}

}
