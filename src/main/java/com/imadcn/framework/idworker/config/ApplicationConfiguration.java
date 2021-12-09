package com.imadcn.framework.idworker.config;

/**
 * Application 配置
 * 
 * @author imadcn
 * @since 1.0.0
 */
public class ApplicationConfiguration {

    /**
     * 分组名字，默认default
     */
    private String group = "default";
    /**
     * 生成策略，默认snowflake
     * 
     * @since 1.2.0
     */
    private String strategy = "snowflake";
    /**
     * 低并发模式（snowflake策略生效）
     * 
     * @since 1.2.5
     */
    @Deprecated
    private boolean lowConcurrency = false;
    /**
     * zk节点信息本地缓存文件路径
     * 
     * @since 1.3.0
     */
    private String registryFile;
    /**
     * zk节点是否持久化存储
     * 
     * @since 1.4.0
     */
    private boolean durable;

    /**
     * 序列化工具
     * 
     * @since 1.6.0
     */
    private String serialize;

    /**
     * 是否使用本地缓存（如果不依赖本地缓存，那么每次都会申请一个新的workerId）
     * <p>
     * 需要注意的是，如果不依赖本地缓存，且开启了节点持久化存储。会在一定次数以后耗尽可用节点信息。
     * 
     * @since 1.6.0
     */
    private boolean cachable = true;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    @Deprecated
    public boolean isLowConcurrency() {
        return lowConcurrency;
    }

    @Deprecated
    public void setLowConcurrency(boolean lowConcurrency) {
        this.lowConcurrency = lowConcurrency;
    }

    public String getRegistryFile() {
        return registryFile;
    }

    public void setRegistryFile(String registryFile) {
        this.registryFile = registryFile;
    }

    public boolean isDurable() {
        return durable;
    }

    public void setDurable(boolean durable) {
        this.durable = durable;
    }

    public String getSerialize() {
        return serialize;
    }

    public void setSerialize(String serialize) {
        this.serialize = serialize;
    }

    public boolean isCachable() {
        return cachable;
    }

    public void setCachable(boolean cachable) {
        this.cachable = cachable;
    }

}
