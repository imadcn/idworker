package com.imadcn.framework.idworker.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @Author Anson <br>
 * Created on 2020/11/20 16:25<br>
 * 名称：JSON.java <br>
 * 描述：JSON工具类<br>
 */
@Component
public class JSON {
    /**
     * 引入Jackson的ObjectMapper对象
     */
    private final ObjectMapper objectMapper;

    /**
     * 构造JSON对象
     * @param objectMapper Jackson的ObjectMapper对象
     */
    public JSON(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 将对象转换为JSON字符串
     * @param obj 类对象
     * @param <T> 类类型
     * @return 序列化字符串
     */
    public <T> String objToStr(T obj) {
        if (null == obj) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将JSON字符串转换成对象
     * @param str 序列化字符串
     * @param clazz 类对象
     * @param <T> 类类型
     * @return 类对象
     */
    public <T> T strToObj(String str, Class<T> clazz) {
        if (StringUtils.isEmpty(str) || null == clazz) {
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T) str : objectMapper.readValue(str, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将JSON字符串转换成对象
     * @param str 序列化字符串
     * @param typeReference 类型的引用
     * @param <T> 类类型
     * @return 类对象
     */
/*    public <T> T strToObj(String str, TypeReference<T> typeReference) {
        if (StringUtils.isEmpty(str) || null == typeReference) {
            return null;
        }
        try {
            return (T) (typeReference.getType().equals(String.class) ?
                    str : objectMapper.readValue(str, typeReference));
        } catch (Exception e) {
            return null;
        }
    }*/
}
