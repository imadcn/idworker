package com.imadcn.system.test.idworker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.imadcn.framework.idworker.generator.IdGenerator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath*:spring-*.xml")
public class IdWorkerTest {
	
	@Autowired
	private IdGenerator idGenerator;
	
	@Test
	public void testGetId() {
		while (true) {
			try {
				Thread.sleep(5_000);
				Object object = idGenerator.nextId();
				print(object);
			} catch (Exception e) {
			}
		}
	}
	
	public static void print(Object object) {
		System.out.println(JSON.toJSONString(object));
	}

}
