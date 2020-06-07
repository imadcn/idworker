package com.imadcn.system.test.idworker;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.imadcn.framework.idworker.generator.SnowflakeGenerator;
import com.imadcn.system.test.spring.AbstractZookeeperJUnit4SpringContextTests;

@ContextConfiguration(locations = "classpath:META-INF/idworker-ctx-manual.xml")
public final class ManualCloseTest extends AbstractZookeeperJUnit4SpringContextTests {

    @Autowired
    private SnowflakeGenerator idGenerator;

    @Test
    public void testClose() throws Exception {
        Object object = idGenerator.nextId();
        print(object);
        idGenerator.close();
    }

}
