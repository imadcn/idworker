package com.imadcn.system.test.idworker;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.alibaba.fastjson.JSON;
import com.imadcn.framework.idworker.generator.IdGenerator;
import com.imadcn.system.test.spring.AbstractZookeeperJUnit4SpringContextTests;

@ContextConfiguration(locations = "classpath:spring/idworker-test.xml")
public class IdWorkerTest extends AbstractZookeeperJUnit4SpringContextTests {

	@Autowired
	private IdGenerator idGenerator;

	@Test
	public void testGetId() {
		try {
			Object object = idGenerator.nextId();
			print(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void print(Object object) {
		System.out.println(JSON.toJSONString(object));
	}

}
