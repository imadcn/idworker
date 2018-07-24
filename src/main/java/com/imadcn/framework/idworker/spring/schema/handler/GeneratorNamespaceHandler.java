package com.imadcn.framework.idworker.spring.schema.handler;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.imadcn.framework.idworker.spring.schema.parser.GeneratorBeanDefinitionParser;

/**
 * IdworkerNamespaceHandler
 * @author yangchao
 * @since 1.0.0
 */
public class GeneratorNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("snowflake", new GeneratorBeanDefinitionParser("snowflake"));
		registerBeanDefinitionParser("compress-uuid", new GeneratorBeanDefinitionParser("compress-uuid"));
	}

}
