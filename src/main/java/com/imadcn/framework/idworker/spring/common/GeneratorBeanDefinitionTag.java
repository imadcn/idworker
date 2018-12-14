package com.imadcn.framework.idworker.spring.common;

/**
 * idworker:application 配置TAF
 * 
 * @author yangchao
 * @since 1.0.0
 */
public final class GeneratorBeanDefinitionTag extends BaseBeanDefinitionTag {

	private GeneratorBeanDefinitionTag() {
	}

	public static final String GROUOP = "group";

	public static final String REGISTRY_CENTER_REF = "registry-center-ref";

	/**
	 * 生成策略
	 * 
	 * @since 1.2.0
	 */
	public static final String STRATEGY = "strategy";

}
