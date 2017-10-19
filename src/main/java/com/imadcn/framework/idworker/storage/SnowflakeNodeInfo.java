package com.imadcn.framework.idworker.storage;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 机器节点信息
 * 
 * @author yangchao
 * @since 2017-10-18
 */
public class SnowflakeNodeInfo {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private long workerId;
	private String ip;
	private String hostName;
	private String pid;

	public SnowflakeNodeInfo(long workerId) {
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

	public long getWorkerId() {
		return workerId;
	}

	public String getIp() {
		return ip;
	}

	public String getHostName() {
		return hostName;
	}

	public String getPid() {
		return pid;
	}

	@Override
	public String toString() {
		return "WorkerNodeInfo [workerId=" + workerId + ", ip=" + ip + ", hostName=" + hostName + ", pid=" + pid + "]";
	}

}
