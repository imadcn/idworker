package com.imadcn.framework.idworker.generator;

import java.io.IOException;

import org.apache.curator.framework.state.ConnectionStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imadcn.framework.idworker.algorithm.Snowflake;
import com.imadcn.framework.idworker.exception.RegException;
import com.imadcn.framework.idworker.register.zookeeper.ZookeeperConnectionStateListener;
import com.imadcn.framework.idworker.register.zookeeper.ZookeeperWorkerRegister;

/**
 * Snowflake算法生成工具
 * @author yangchao
 * @since 2017-10-19
 */
public class SnowflakeGenerator implements IdGenerator {
	
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
	private volatile boolean working = false;
	
	private volatile boolean hookAdded = false;
	
	private ConnectionStateListener listener;
	
	public SnowflakeGenerator(ZookeeperWorkerRegister register) {
		this.register = register;
	}
	
	@Override
	public void init() {
		listener = new ZookeeperConnectionStateListener(this);
		// 添加监听
		register.addConnectionLJistener(listener);
		// 注册workerId
		register();
		// 添加shutdown hook
		if (!hookAdded) {
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						if (logger.isInfoEnabled()) {
							logger.info("Run Shutdown Hook.");
						}
						suspend();
						close();
					} catch (Exception e) {
						logger.error("", e);
					}
			}}));
			hookAdded = true;
		}
	}

	/**
	 * 初始化
	 */
	@Override
	public void register() {
		long workerId = register.register();
		if (workerId >= 0) {
			snowflake = Snowflake.create(workerId);
			working = true;
		} else {
			throw new RegException("failed to get worker id");
		}
	}
	
	@Override
	public long[] nextId(int size) {
		if (working) {
			return snowflake.nextId(size);
		}
		throw new IllegalStateException("worker not working, reg center may shutdown");
	}

	@Override
	public long nextId() {
		if (working) {
			return snowflake.nextId();
		}
		throw new IllegalStateException("worker not working, reg center may shutdown");
	}

	@Override
	public void suspend() {
		this.working = false;
	}

	@Override
	public void recover() {
		this.working = true;
	}

	@Override
	public void close() throws IOException {
		register.logout();
	}

	@Override
	public boolean isWorking() {
		return working;
	}
}
