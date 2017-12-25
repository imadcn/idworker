package com.imadcn.system.test.idworker;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.alibaba.fastjson.JSON;
import com.imadcn.framework.idworker.generator.SnowflakeGenerator;
import com.imadcn.system.test.spring.AbstractZookeeperJUnit4SpringContextTests;

@ContextConfiguration(locations = "classpath:META-INF/idworker-ctx.xml")
public final class SpringIdWorkerTest extends AbstractZookeeperJUnit4SpringContextTests {

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
	
	@Test
	public void testWithInvalidParam() {
		try {
			Object object = idGenerator.nextId(0);
			print(object);
		} catch (Exception e) {
			print(e);
		}
		
		try {
			Object object = idGenerator.nextId(-1);
			print(object);
		} catch (Exception e) {
			print(e);
		}
		
		
		try {
			Object object = idGenerator.nextId(100_001);
			print(object);
		} catch (Exception e) {
			print(e);
		}
		
	}
	
	@Test
	public void testClose() throws Exception {
		Object object = idGenerator.nextId();
		print(object);
		idGenerator.close();
	}

	public void print(Object object) {
		String json = JSON.toJSONString(object);
		System.out.println(json);
		logger.info(json);
	}

}
