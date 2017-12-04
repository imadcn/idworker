package com.imadcn.framework.idworker.register;

/**
 * ID生成注册Connector
 * @author yangc
 * @since 2017-12-04
 */
public interface GeneratorConnector {
	
	/**
	 * 初始化数据
	 */
	void init();
	
	/**
	 * 连接
	 */
	void connect();
	
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
