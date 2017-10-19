package com.imadcn.framework.idworker.generator;

import com.imadcn.framework.idworker.algorithm.Snowflake;
import com.imadcn.framework.idworker.exception.RegException;
import com.imadcn.framework.idworker.storage.WorkerNodeRegister;

/**
 * Snowflake算法生成工具
 * @author yangchao
 * @since 2017-10-19
 */
public class SnowflakeGenerator implements IdGenerator {

	private Snowflake snowflake;
	private WorkerNodeRegister storage;
	
	public SnowflakeGenerator(WorkerNodeRegister storage) {
		this.storage = storage;
	}

	/**
	 * 初始化
	 */
	public void init() {
		long workerId = storage.register();
		if (workerId >= 0) {
			snowflake = Snowflake.create(workerId);
		} else {
			throw new RegException("failed to get worker id");
		}
	}
	
	@Override
	public long[] nextId(int size) {
		return snowflake.nextId(size);
	}

	@Override
	public long nextId() {
		return snowflake.nextId();
	}
	
}
