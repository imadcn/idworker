package com.imadcn.framework.idworker.spring.common;

/**
 * idworker:zookeeper 配置TAG
 * 
 * @author imadcn
 * @since 1.0.0
 */
public final class ZookeeperBeanDefinitionTag {

    private ZookeeperBeanDefinitionTag() {
    }

    public static final String SERVER_LISTS = "server-lists";

    public static final String NAMESPACE = "namespace";

    public static final String BASE_SLEEP_TIME_MS = "base-sleep-time-milliseconds";

    public static final String MAX_SLEEP_TIME_MS = "max-sleep-time-milliseconds";

    public static final String MAX_RETRIES = "max-retries";

    public static final String SESSION_TIMEOUT_MS = "session-timeout-milliseconds";

    public static final String CONNECTION_TIMEOUT_MS = "connection-timeout-milliseconds";

    public static final String DIGEST = "digest";
}
