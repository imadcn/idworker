package com.imadcn.framework.idworker.spring.schema.parser;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.w3c.dom.Element;

/**
 * Spring Bean Definition Parser
 * 
 * @author imadcn
 * @since 1.0.0
 */
public abstract class BaseBeanDefinitionParser extends AbstractBeanDefinitionParser {

    /**
     * 设置Bean Id
     * 
     * @param result result
     * @param beanId beanId
     */
    // protected void setBeanId(BeanDefinitionBuilder result, String beanId) {
    // result.addPropertyValue("id", beanId);
    // }

    /**
     * 设置Bean Id
     * 
     * @param result result
     * @param clazz clazz
     * @param parserContext parserContext
     * @param element element
     */
    // protected void setBeanId(BeanDefinitionBuilder result, Class<?> clazz,
    // ParserContext parserContext, Element element) {
    // String beanId = getBeanId(clazz, parserContext, element);
    // setBeanId(result, beanId);
    // }

    /**
     * 获取BeanId
     * 
     * @param clazz clazz
     * @param parserContext parserContext
     * @param element element
     * @return BeanId
     */
    // protected String getBeanId(Class<?> clazz, ParserContext parserContext,
    // Element element) {
    // String id = element.getAttribute("id");
    // if (id == null || id.isEmpty()) {
    // String generatedBeanName = element.getAttribute("name");
    // if (generatedBeanName == null || generatedBeanName.isEmpty()) {
    // generatedBeanName = clazz.getName();
    // }
    // id = generatedBeanName;
    // int counter = 2;
    // while (parserContext.getRegistry().containsBeanDefinition(id)) {
    // id = generatedBeanName + (counter++);
    // }
    // }
    // if (id != null && !id.isEmpty()) {
    // if (parserContext.getRegistry().containsBeanDefinition(id)) {
    // throw new IllegalStateException("Duplicate spring bean id " + id);
    // }
    // }
    // return id;
    // }

    /**
     * 设置Properties
     * 
     * @param attributeName attributeName
     * @param propertyName propertyName
     * @param element element
     * @param factory factory
     */
    protected static void addPropertyValueIfNotEmpty(final String attributeName, final String propertyName,
        final Element element, final BeanDefinitionBuilder factory) {
        String attributeValue = element.getAttribute(attributeName);
        if (attributeValue != null && !attributeValue.isEmpty()) {
            factory.addPropertyValue(propertyName, attributeValue);
        }
    }

    /**
     * 获取attribute值
     * 
     * @param element element
     * @param attributeName attributeName
     * @since 1.2.0
     * @return attribute值
     */
    protected static String getAttributeValue(final Element element, final String attributeName) {
        String attributeValue = element.getAttribute(attributeName);
        return attributeValue;
    }
}
