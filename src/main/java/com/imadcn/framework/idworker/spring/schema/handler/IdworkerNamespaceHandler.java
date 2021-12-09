package com.imadcn.framework.idworker.spring.schema.handler;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.imadcn.framework.idworker.common.ConfigConstants;
import com.imadcn.framework.idworker.spring.schema.parser.RegistryBeanDefinitionParser;

/**
 * IdworkerNamespaceHandler
 * 
 * @author imadcn
 * @since 1.0.0
 */
public class IdworkerNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser(ConfigConstants.REGISTRY,
            new RegistryBeanDefinitionParser(ConfigConstants.REGISTRY));
        registerBeanDefinitionParser(ConfigConstants.GENERATOR,
            new RegistryBeanDefinitionParser(ConfigConstants.GENERATOR));
    }

}
