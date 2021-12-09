package com.imadcn.framework.idworker.exception;

import org.apache.zookeeper.KeeperException.ConnectionLossException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 注册中心异常处理类.
 * 
 * @author imadcn
 * @since 1.0.0
 */
public final class RegExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(RegExceptionHandler.class);

    /**
     * 处理异常.
     * 
     * <p>
     * 处理掉中断和连接失效异常并继续抛注册中心.
     * </p>
     * 
     * @param cause 待处理异常.
     */
    public static void handleException(final Exception cause) {
        if (null == cause) {
            return;
        }
        if (isIgnoredException(cause) || null != cause.getCause() && isIgnoredException(cause.getCause())) {
            logger.debug("Elastic job: ignored exception for: {}", cause.getMessage());
        } else if (cause instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        } else {
            throw new RegException(cause);
        }
    }

    private static boolean isIgnoredException(final Throwable cause) {
        return cause instanceof ConnectionLossException || cause instanceof NoNodeException
            || cause instanceof NodeExistsException;
    }
}
