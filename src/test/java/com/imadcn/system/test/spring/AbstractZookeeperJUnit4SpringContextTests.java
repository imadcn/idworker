package com.imadcn.system.test.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestExecutionListeners;

@TestExecutionListeners(EmbedZookeeperTestExecutionListener.class)
public abstract class AbstractZookeeperJUnit4SpringContextTests {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

}
