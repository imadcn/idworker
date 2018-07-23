package com.imadcn.framework.idworker.spring.schema.parser;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.imadcn.framework.idworker.common.GeneratorStrategy;
import com.imadcn.framework.idworker.config.ApplicationConfiguration;
import com.imadcn.framework.idworker.generator.CompressUUIDGenerator;
import com.imadcn.framework.idworker.generator.SnowflakeGenerator;
import com.imadcn.framework.idworker.register.zookeeper.ZookeeperWorkerRegister;
import com.imadcn.framework.idworker.spring.common.GeneratorBeanDefinitionTag;

/**
 * idworker:application 标签解析
 * @author yangchao
 * @since 1.0.0
 */
public class GeneratorBeanDefinitionParser extends BaseBeanDefinitionParser {
	
	@Override
    protected AbstractBeanDefinition parseInternal(final Element element, final ParserContext parserContext) {
		Class<?> generatorClass = getGeneratorClass(element);
        BeanDefinitionBuilder result = BeanDefinitionBuilder.rootBeanDefinition(generatorClass);
        // snowflake 生成策略
        if (generatorClass.isAssignableFrom(SnowflakeGenerator.class)) {
        	result.addConstructorArgValue(buildWorkerNodeRegisterBeanDefinition(element, parserContext));
        	result.setInitMethodName("init");
        }
        return result.getBeanDefinition();
    }
	
	/**
	 * snowflake策略：zookeeper依赖配置
	 * @param element
	 * @param parserContext
	 * @return
	 */
	private AbstractBeanDefinition buildWorkerNodeRegisterBeanDefinition(final Element element, final ParserContext parserContext) {
		 BeanDefinitionBuilder result = BeanDefinitionBuilder.rootBeanDefinition(ZookeeperWorkerRegister.class);
		 result.addConstructorArgReference(element.getAttribute(GeneratorBeanDefinitionTag.REGISTRY_CENTER_REF));
		 result.addConstructorArgValue(buildApplicationConfigurationBeanDefinition(element, parserContext));
		 return result.getBeanDefinition();
	} 
    
    /**
     * snowflake策略：app config 参数
     * @param element
     * @param parserContext
     * @return
     */
    private AbstractBeanDefinition buildApplicationConfigurationBeanDefinition(final Element element, final ParserContext parserContext) {
        BeanDefinitionBuilder configuration = BeanDefinitionBuilder.rootBeanDefinition(ApplicationConfiguration.class);
        addPropertyValueIfNotEmpty(GeneratorBeanDefinitionTag.GROUOP, "group", element, configuration);
        addPropertyValueIfNotEmpty(GeneratorBeanDefinitionTag.STRATEGY, "strategy", element, configuration);
        return configuration.getBeanDefinition();
    }
    
    /**
     * 获取ID生成策略 Class
     * @param strategyCode
     * @return
     */
    private Class<?> getGeneratorClass(final Element element) {
    	String strategyCode = getAttributeValue(element, "strategy");
    	GeneratorStrategy strategy = GeneratorStrategy.valueOf(strategyCode);
    	if (strategy == null) {
    		throw new IllegalArgumentException("unsupported generator strategy.");
    	}
    	switch (strategy) {
    	case SNOWFLAKE:
    		return SnowflakeGenerator.class;
    	case COMPRESS_UUID:
    		return CompressUUIDGenerator.class;
    	default:
    		return SnowflakeGenerator.class;
    	}
    }
}
