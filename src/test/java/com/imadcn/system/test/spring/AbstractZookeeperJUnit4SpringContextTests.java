package com.imadcn.system.test.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.alibaba.fastjson.JSON;

@TestExecutionListeners(EmbedZookeeperTestExecutionListener.class)
public abstract class AbstractZookeeperJUnit4SpringContextTests extends AbstractJUnit4SpringContextTests {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected void print(Object object) {
        String json = JSON.toJSONString(object);
        System.out.println(json);
        logger.info(json);
    }
}
