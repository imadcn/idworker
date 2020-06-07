package com.imadcn.framework.idworker.generator;

/**
 * Id生成
 * 
 * @author imadcn
 * @since 1.0.0
 */
public interface IdGenerator {

    /**
     * 批量获取ID
     * 
     * @param size 获取大小，最多10万个
     * @return ID
     */
    long[] nextId(int size);

    /**
     * 获取ID
     * 
     * @return ID
     */
    long nextId();

    /**
     * 字符串格式的ID
     * 
     * @since 1.2.0
     * @return ID
     */
    String nextStringId();

    /**
     * 固定19位长度，字符串格式的ID
     * 
     * @since 1.2.0
     * @return ID
     */
    String nextFixedStringId();

}
