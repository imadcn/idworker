package com.imadcn.framework.idworker.spring.schema;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.imadcn.framework.idworker.config.ZookeeperConfiguration;
import com.imadcn.framework.idworker.registry.zookeeper.ZookeeperRegistryCenter;
import com.imadcn.framework.idworker.spring.common.ZookeeperBeanDefinitionTag;

/**
 * idworker:zookeeper 标签解析
 * @author yangchao
 * @since 2017-10-19
 */
public class ZookeeperBeanDefinitionParser extends BaseBeanDefinitionParser {

	@Override
    protected AbstractBeanDefinition parseInternal(final Element element, final ParserContext parserContext) {
        BeanDefinitionBuilder result = BeanDefinitionBuilder.rootBeanDefinition(ZookeeperRegistryCenter.class);
        result.addConstructorArgValue(buildZookeeperConfigurationBeanDefinition(element, parserContext));
        result.setInitMethodName("init");
        return result.getBeanDefinition();
    }
    
    private AbstractBeanDefinition buildZookeeperConfigurationBeanDefinition(final Element element, final ParserContext parserContext) {
        BeanDefinitionBuilder result = BeanDefinitionBuilder.rootBeanDefinition(ZookeeperConfiguration.class);
        addPropertyValueIfNotEmpty(ZookeeperBeanDefinitionTag.SERVER_LISTS, "serverLists", element, result);
        addPropertyValueIfNotEmpty(ZookeeperBeanDefinitionTag.NAMESPACE, "namespace", element, result);
        addPropertyValueIfNotEmpty(ZookeeperBeanDefinitionTag.BASE_SLEEP_TIME_MS, "baseSleepTimeMilliseconds", element, result);
        addPropertyValueIfNotEmpty(ZookeeperBeanDefinitionTag.MAX_SLEEP_TIME_MS, "maxSleepTimeMilliseconds", element, result);
        addPropertyValueIfNotEmpty(ZookeeperBeanDefinitionTag.MAX_RETRIES, "maxRetries", element, result);
        addPropertyValueIfNotEmpty(ZookeeperBeanDefinitionTag.SESSION_TIMEOUT_MS, "sessionTimeoutMilliseconds", element, result);
        addPropertyValueIfNotEmpty(ZookeeperBeanDefinitionTag.CONNECTION_TIMEOUT_MS, "connectionTimeoutMilliseconds", element, result);
        addPropertyValueIfNotEmpty(ZookeeperBeanDefinitionTag.DIGEST, "digest", element, result);
        return result.getBeanDefinition();
    }

}
