package com.imadcn.framework.idworker.generator;

import java.io.Closeable;

/**
 * Id生成
 * @author yangchao
 * @since 2017-10-19
 */
public interface IdGenerator extends Closeable {
	
	/**
	 * 初始化数据
	 */
	void init();
	
	/**
	 * 注册初始化
	 */
	void register();
	
	/**
	 * 批量获取ID
	 * @param size 获取大小，最多100万个
	 * @return ID
	 */
	long[] nextId(int size);
	
	/**
	 * 获取ID
	 * @return ID
	 */
	long nextId();
	
	/**
	 * 挂起ID生产
	 */
	void suspend();
	
	/**
	 * 判断是否正在正常运行
	 * @return 是返回<b> true </b>,否则返回<b> false </b>
	 */
	boolean isWorking();
	
	/**
	 * 是否正在连接
	 * @return 是返回<b> true </b>,否则返回<b> false </b>
	 */
	boolean isConnecting();
}
