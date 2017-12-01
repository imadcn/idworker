package com.imadcn.framework.idworker.register;

import java.io.Closeable;

/**
 * Worker注册
 * @author yangc
 * @since 2017-12-01
 */
public interface WorkerRegister extends Closeable {

	/**
	 * 注册workerId
	 */
	long register();
}
