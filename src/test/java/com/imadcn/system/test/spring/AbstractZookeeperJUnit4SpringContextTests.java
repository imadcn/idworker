package com.imadcn.system.test.spring;

import org.springframework.test.context.TestExecutionListeners;

@TestExecutionListeners(EmbedZookeeperTestExecutionListener.class)
public abstract class AbstractZookeeperJUnit4SpringContextTests {

}
