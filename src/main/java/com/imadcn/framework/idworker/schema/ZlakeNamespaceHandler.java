package com.imadcn.framework.idworker.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class ZlakeNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("config", new ZlakeConfigBeanDefinitionParser());
	}

}
