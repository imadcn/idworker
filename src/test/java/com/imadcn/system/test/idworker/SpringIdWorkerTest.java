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
		Object object = idGenerator.nextId();
		print(object);
	}
	
	@Test
	public void testBatchGetId() {
		Object object = idGenerator.nextId(20);
		print(object);
	}

	public void print(Object object) {
		String json = JSON.toJSONString(object);
		System.out.println(json);
		logger.info(json);
	}

}
