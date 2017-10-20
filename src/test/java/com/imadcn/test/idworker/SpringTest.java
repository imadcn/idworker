package com.imadcn.test.idwork;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSON;
import com.imadcn.framework.idworker.generator.IdGenerator;

public class SpringTest {
	
	private static ClassPathXmlApplicationContext context;
	private static String configPath = "classpath*:spring-idworker.xml";
	
	public SpringTest() {
		context = new ClassPathXmlApplicationContext(new String[] {configPath});
		context.start();
	}
	
	public void testGen() {
		IdGenerator generator = context.getBean(IdGenerator.class);
		print(generator.nextId());
		print(generator.nextId());
		print(generator.nextId());
		print(generator.nextId());
		print(generator.nextId());
		print(generator.nextId());
		print(generator.nextId());
		print(generator.nextId());
		print(generator.nextId());
	} 
	
	void print(Object object) {
		System.err.println(JSON.toJSONString(object));
	}
	
	public static void main(String[] args) {
		SpringTest test = new SpringTest();
		test.testGen();
	}

}
