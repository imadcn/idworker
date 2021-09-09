package com.imadcn.system.test.idworker;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.imadcn.framework.idworker.generator.SnowflakeGenerator;
import com.imadcn.system.test.spring.AbstractZookeeperJUnit4SpringContextTests;

@ContextConfiguration(locations = "classpath:META-INF/idworker-ctx-serialize-fastjson.xml")
public final class SerializeFastJsonModeTest extends AbstractZookeeperJUnit4SpringContextTests {

    @Autowired
    private SnowflakeGenerator idGenerator;

    @Test
    public void testGetId() {
        Object object = idGenerator.nextId();
        print(object);
    }

    @Test
    public void testBatchGetId() {
        Object object = idGenerator.nextId(20);
        print(object);
    }
}
