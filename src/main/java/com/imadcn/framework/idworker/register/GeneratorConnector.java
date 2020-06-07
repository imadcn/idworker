package com.imadcn.framework.idworker.register;

import java.io.Closeable;

/**
 * ID生成注册Connector
 * 
 * @author imadcn
 * @since 1.1.0
 */
public interface GeneratorConnector extends Closeable {

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
     * 是否正在正常运行
     * 
     * @return 是返回<b> true </b>,否则返回<b> false </b>
     */
    boolean isWorking();

    /**
     * 是否正在连接
     * 
     * @return 是返回<b> true </b>,否则返回<b> false </b>
     */
    boolean isConnecting();
}
