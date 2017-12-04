package com.imadcn.framework.idworker.generator;

/**
 * Id生成
 * @author yangchao
 * @since 2017-10-19
 */
public interface IdGenerator {
	
	/**
	 * 批量获取ID
	 * @param size 获取大小，最多10万个
	 * @return ID
	 */
	long[] nextId(int size);
	
	/**
	 * 获取ID
	 * @return ID
	 */
	long nextId();
	
}
