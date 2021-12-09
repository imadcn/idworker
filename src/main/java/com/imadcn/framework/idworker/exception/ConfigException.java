package com.imadcn.framework.idworker.exception;

/**
 * configuration error exception
 * 
 * @author imadcn
 * @since 1.6.0
 */
public class ConfigException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ConfigException(final String errorMessage, final Object... args) {
        super(String.format(errorMessage, args));
    }

    public ConfigException(final Exception cause) {
        super(cause);
    }
}
