package com.imadcn.framework.idworker.register.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imadcn.framework.idworker.register.GeneratorConnector;

/**
 * Zk连接状态监听
 * @author yangc
 * @since 1.1.0
 */
public class ZookeeperConnectionStateListener implements ConnectionStateListener {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private GeneratorConnector connector;
	
	public ZookeeperConnectionStateListener(GeneratorConnector connector) {
		this.connector = connector;
	}

	@Override
	public void stateChanged(CuratorFramework client, ConnectionState newState) {
		switch (newState) {
		case LOST:
			logger.warn("zookeeper connection session lost, try to register new worker id.");
			doReconnecting();
			break;
		case SUSPENDED:
			logger.warn("zookeeper suspended, try to register new worker id.");
			doReconnecting();
			break;
		default:
			break;
		}
	}
	
	protected void doReconnecting() {
		connector.suspend();
		connector.connect();
	}
}
