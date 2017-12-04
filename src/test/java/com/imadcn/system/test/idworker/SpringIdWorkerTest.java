package com.imadcn.system.test.idworker;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.alibaba.fastjson.JSON;
import com.imadcn.framework.idworker.generator.IdGenerator;
import com.imadcn.system.test.spring.AbstractZookeeperJUnit4SpringContextTests;

@ContextConfiguration(locations = "classpath:META-INF/idworker-ctx.xml")
public final class SpringIdWorkerTest extends AbstractZookeeperJUnit4SpringContextTests {

	@Autowired
	private IdGenerator idGenerator;

	@Test
	public void testGetId() {
		try {
			Object object = idGenerator.nextId();
			print(object);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public void print(Object object) {
		String json = JSON.toJSONString(object);
		logger.info(json);
	}

}
