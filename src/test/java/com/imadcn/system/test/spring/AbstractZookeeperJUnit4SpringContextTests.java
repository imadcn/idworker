package com.imadcn.system.test.spring;

import com.imadcn.framework.idworker.jackson.JSON;
import com.imadcn.framework.idworker.jackson.JacksonConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@TestExecutionListeners(EmbedZookeeperTestExecutionListener.class)
public abstract class AbstractZookeeperJUnit4SpringContextTests extends AbstractJUnit4SpringContextTests {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected void print(Object object) {
        String json = new JSON(new JacksonConfig().objectMapper()).objToStr(object);
        System.out.println(json);
        logger.info(json);
    }
}
