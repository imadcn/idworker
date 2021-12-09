package com.imadcn.framework.idworker.serialize.json;

import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * jackson serializer
 * 
 * @author imadcn
 * @since 1.6.0
 */
public class JacksonSerializer<T> extends JsonSerializer<T> {

    private ObjectMapper objectMapper;

    public JacksonSerializer() {
        objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat(DEFAULT_DATE_FORMAT));
    }

    @Override
    public String toJsonString(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }

    @Override
    public T parseObject(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(json, clazz);
    }
}
