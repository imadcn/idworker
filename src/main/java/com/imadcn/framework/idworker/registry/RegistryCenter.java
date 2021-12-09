package com.imadcn.framework.idworker.registry;

/**
 * 注册中心
 * 
 * @author imadcn
 * @since 1.0.0
 */
public interface RegistryCenter {

    /**
     * 初始化注册中心.
     */
    void init();

    /**
     * 关闭注册中心.
     */
    void close();

    /**
     * 获取注册数据.
     * 
     * @param key 键
     * @return 值
     */
    String get(String key);

    /**
     * 获取数据是否存在.
     * 
     * @param key 键
     * @return 数据是否存在
     */
    boolean isExisted(String key);

    /**
     * 持久化注册数据.
     * 
     * @param key 键
     * @param value 值
     */
    void persist(String key, String value);

    /**
     * 更新注册数据.
     * 
     * @param key 键
     * @param value 值
     */
    void update(String key, String value);

    /**
     * 删除注册数据.
     * 
     * @param key 键
     */
    void remove(String key);

    /**
     * 获取注册中心当前时间.
     * 
     * @param key 用于获取时间的键
     * @return 注册中心当前时间
     */
    long getRegistryCenterTime(String key);

    /**
     * 直接获取操作注册中心的原生客户端. 如：Zookeeper或Redis等原生客户端.
     * 
     * @return 注册中心的原生客户端
     */
    Object getRawClient();
}
