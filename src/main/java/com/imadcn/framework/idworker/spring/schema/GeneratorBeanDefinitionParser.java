package com.imadcn.framework.idworker.spring.schema;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.imadcn.framework.idworker.config.ApplicationConfiguration;
import com.imadcn.framework.idworker.register.zookeeper.ZookeeperWorkerRegister;
import com.imadcn.framework.idworker.spring.common.GeneratorBeanDefinitionTag;

/**
 * idworker:application 标签解析
 * @author yangchao
 * @since 2017-10-19
 */
public class GeneratorBeanDefinitionParser extends BaseBeanDefinitionParser {
	
	private Class<?> generatorClass;

	public GeneratorBeanDefinitionParser(Class<?> generatorClass) {
		this.generatorClass = generatorClass;
	}

	@Override
    protected AbstractBeanDefinition parseInternal(final Element element, final ParserContext parserContext) {
        BeanDefinitionBuilder result = BeanDefinitionBuilder.rootBeanDefinition(generatorClass);
        result.addConstructorArgValue(buildWorkerNodeRegisterBeanDefinition(element, parserContext));
        result.setInitMethodName("init");
        return result.getBeanDefinition();
    }
	
	private AbstractBeanDefinition buildWorkerNodeRegisterBeanDefinition(final Element element, final ParserContext parserContext) {
		 BeanDefinitionBuilder result = BeanDefinitionBuilder.rootBeanDefinition(ZookeeperWorkerRegister.class);
		 result.addConstructorArgReference(element.getAttribute(GeneratorBeanDefinitionTag.REGISTRY_CENTER_REF));
		 result.addConstructorArgValue(buildApplicationConfigurationBeanDefinition(element, parserContext));
		 return result.getBeanDefinition();
	} 
    
    private AbstractBeanDefinition buildApplicationConfigurationBeanDefinition(final Element element, final ParserContext parserContext) {
        BeanDefinitionBuilder configuration = BeanDefinitionBuilder.rootBeanDefinition(ApplicationConfiguration.class);
        addPropertyValueIfNotEmpty(GeneratorBeanDefinitionTag.GROUOP, "group", element, configuration);
        return configuration.getBeanDefinition();
    }
    
}
