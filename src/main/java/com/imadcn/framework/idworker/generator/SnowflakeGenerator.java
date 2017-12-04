package com.imadcn.framework.idworker.generator;

import java.io.IOException;

import org.apache.curator.framework.state.ConnectionStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imadcn.framework.idworker.algorithm.Snowflake;
import com.imadcn.framework.idworker.exception.RegException;
import com.imadcn.framework.idworker.register.GeneratorConnector;
import com.imadcn.framework.idworker.register.zookeeper.ZookeeperConnectionStateListener;
import com.imadcn.framework.idworker.register.zookeeper.ZookeeperWorkerRegister;

/**
 * Snowflake算法生成工具
 * @author yangchao
 * @since 2017-10-19
 */
public class SnowflakeGenerator implements IdGenerator, GeneratorConnector {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * snowflake 算法
	 */
	private Snowflake snowflake;
	/**
	 * snowflake 注册
	 */
	private ZookeeperWorkerRegister register;
	/**
	 * 是否正在工作
	 */
	private volatile boolean initialized = false;

	private volatile boolean working = false;
	
	private volatile boolean connecting = false;
	
	private ConnectionStateListener listener;
	
	public SnowflakeGenerator(ZookeeperWorkerRegister register) {
		this.register = register;
	}
	
	@Override
	public synchronized void init() {
		if (!initialized) {
			listener = new ZookeeperConnectionStateListener(this);
			// 添加监听
			register.addConnectionLJistener(listener);
			// 注册workerId
			connect();
			// 添加shutdown hook
//			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						if (logger.isInfoEnabled()) {
//							logger.info("Run Shutdown Hook.");
//						}
//						suspend();
//						close();
//					} catch (Exception e) {
//						logger.error("", e);
//					}
//			}}));
			initialized = true;
		}
		
	}

	/**
	 * 初始化
	 */
	@Override
	public void connect() {
		if (!isConnecting()) {
			working = false;
			connecting = true;
			long workerId = register.register();
			if (workerId >= 0) {
				snowflake = Snowflake.create(workerId);
				working = true;
				connecting = false;
			} else {
				throw new RegException("failed to get worker id");
			}
		} else {
			logger.info("worker is CONNECTING, skip this time of register.");
		}
	}
	
	@Override
	public long[] nextId(int size) {
		if (isWorking()) {
			return snowflake.nextId(size);
		}
		throw new IllegalStateException("worker not working, reg center may shutdown");
	}

	@Override
	public long nextId() {
		if (isWorking()) {
			return snowflake.nextId();
		}
		throw new IllegalStateException("worker not working, reg center may shutdown");
	}

	@Override
	public void suspend() {
		this.working = false;
	}
	
	@Override
	public synchronized void close() throws IOException {
		register.logout();
	}

	@Override
	public boolean isWorking() {
		return this.working;
	}

	@Override
	public boolean isConnecting() {
		return this.connecting;
	}
	
}
