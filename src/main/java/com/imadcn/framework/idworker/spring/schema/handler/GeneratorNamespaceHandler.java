package com.imadcn.framework.idworker.spring.schema.handler;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.imadcn.framework.idworker.common.ConfigConstants;
import com.imadcn.framework.idworker.spring.schema.parser.GeneratorBeanDefinitionParser;

/**
 * IdworkerNamespaceHandler
 * 
 * @author imadcn
 * @since 1.0.0
 */
public class GeneratorNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser(ConfigConstants.SNOWFLAKE,
            new GeneratorBeanDefinitionParser(ConfigConstants.SNOWFLAKE));
        registerBeanDefinitionParser(ConfigConstants.COMPRESS_UUID,
            new GeneratorBeanDefinitionParser(ConfigConstants.COMPRESS_UUID));
    }

}
