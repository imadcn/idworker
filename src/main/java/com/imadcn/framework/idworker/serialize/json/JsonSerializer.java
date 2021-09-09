package com.imadcn.framework.idworker.serialize.json;

import com.imadcn.framework.idworker.serialize.Serializer;

/**
 * json serializer
 * @author imadcn
 * @since 1.6.0
 */
public abstract class JsonSerializer<T> implements Serializer {
    
    /**
     * 默认时间装换格式：yyyy-MM-dd HH:mm:ss
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * to json string
     * @param object
     * @return
     */
    public abstract String toJsonString(Object object) throws Exception;
    
    /**
     * to java object
     * @param json
     * @param clazz
     * @return
     */
    public abstract T parseObject(String json, Class<T> clazz) throws Exception;
        
}
