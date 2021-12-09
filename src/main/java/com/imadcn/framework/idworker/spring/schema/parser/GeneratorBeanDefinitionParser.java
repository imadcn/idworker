package com.imadcn.framework.idworker.spring.schema.parser;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.imadcn.framework.idworker.common.ConfigConstants;
import com.imadcn.framework.idworker.generator.CompressUUIDGenerator;
import com.imadcn.framework.idworker.generator.SnowflakeGenerator;

/**
 * generator:xxx 标签解析
 * 
 * @author imadcn
 * @since 1.2.0
 */
public class GeneratorBeanDefinitionParser extends BaseBeanDefinitionParser {

    private String generatorType;

    /**
     * generator:xxx 标签解析
     * 
     * @param generatorType 解析类型
     */
    public GeneratorBeanDefinitionParser(String generatorType) {
        this.generatorType = generatorType;
    }

    @Override
    protected AbstractBeanDefinition parseInternal(final Element element, final ParserContext parserContext) {
        Class<?> generatorClass = null;
        if (ConfigConstants.SNOWFLAKE.equals(generatorType)) {
            generatorClass = SnowflakeGenerator.class;
        } else if (ConfigConstants.COMPRESS_UUID.equals(generatorType)) {
            generatorClass = CompressUUIDGenerator.class;
        } else {
            throw new IllegalArgumentException("unknown registryType");
        }
        BeanDefinitionBuilder result = BeanDefinitionBuilder.rootBeanDefinition(generatorClass);
        // snowflake 生成策略
        if (generatorClass.isAssignableFrom(SnowflakeGenerator.class)) {
            result.addConstructorArgValue(
                GeneratorRegisteryBuilder.buildWorkerNodeRegisterBeanDefinition(element, parserContext));
            // 去掉低并发模式配置解析
            // result.addPropertyValue(PropertyConstants.LOW_CONCURRENCY,
            // getAttributeValue(element,
            // GeneratorBeanDefinitionTag.LOW_CONCURRENCY));
            result.setInitMethodName("init");
        }
        return result.getBeanDefinition();
    }
}
