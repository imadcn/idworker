package com.imadcn.framework.idworker.spring.common;

/**
 * idworker:application 配置TAF
 * 
 * @author imadcn
 * @since 1.0.0
 */
public final class GeneratorBeanDefinitionTag extends BaseBeanDefinitionTag {

    private GeneratorBeanDefinitionTag() {
    }

    public static final String GROUOP = "group";

    public static final String REGISTRY_CENTER_REF = "registry-center-ref";

    /**
     * 生成策略
     * 
     * @since 1.2.0
     */
    public static final String STRATEGY = "strategy";

    /**
     * 低并发模式
     * 
     * @since 1.2.5
     */
    @Deprecated
    public static final String LOW_CONCURRENCY = "low-concurrency";

    /**
     * 注册信息本地缓存文件地址
     * 
     * @since 1.3.0
     */
    public static final String REGISTRY_FILE = "registry-file";

    /**
     * ZK节点是否持久化存储
     * 
     * @since 1.4.0
     */
    public static final String DURABLE = "durable";

    /**
     * 序列化方式
     * 
     * @since 1.6.0
     */
    public static final String SERIALIZE = "serialize";

    /**
     * 是否使用本地缓存（如果不依赖本地缓存，那么每次都会申请一个新的workerId）
     * 
     * @since 1.6.0
     */
    public static final String CACHABLE = "cachable";
}
