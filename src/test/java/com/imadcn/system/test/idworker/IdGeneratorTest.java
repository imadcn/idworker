package com.imadcn.system.test.idworker;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.imadcn.framework.idworker.generator.IdGenerator;
import com.imadcn.system.test.spring.AbstractZookeeperJUnit4SpringContextTests;

@ContextConfiguration(locations = "classpath:META-INF/idworker-ctx.xml")
public final class IdGeneratorTest extends AbstractZookeeperJUnit4SpringContextTests {

    @Autowired
    private IdGenerator idGenerator;

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

    @Test
    public void testWithInvalidParam() {
        try {
            Object object = idGenerator.nextId(0);
            print(object);
        } catch (Exception e) {
            print(e.getMessage());
        }

        try {
            Object object = idGenerator.nextId(-1);
            print(object);
        } catch (Exception e) {
            print(e.getMessage());
        }

        try {
            Object object = idGenerator.nextId(100_001);
            print(object);
        } catch (Exception e) {
            print(e.getMessage());
        }

    }
}
