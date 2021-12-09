package com.imadcn.framework.idworker.algorithm;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Snowflake的结构如下(每部分用-分开): <br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 <br>
 * <b> · </b>1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0 <br>
 * <b> · </b>41位时间戳(毫秒级)，注意，41位时间戳不是存储当前时间的时间戳，而是存储时间戳的差值（当前时间戳 -
 * 开始时间戳)得到的值），这里的的开始时间戳，一般是我们的id生成器开始使用的时间，由我们程序来指定的（如下下面程序epoch属性）。41位的时间戳，可以使用69年 <br>
 * <b> · </b>10位的数据机器位，可以部署在1024个节点，包括5位datacenterId和5位workerId <br>
 * <b> · </b>12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间戳)产生4096个ID序号 加起来刚好64位，为一个Long型。
 * <p>
 * SnowFlake的优点是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)，并且效率较高，经测试，SnowFlake每秒能够产生26万ID左右。
 * <p>
 * 注意这里进行了小改动: <br>
 * <b> · </b>Snowflake是5位的datacenter加5位的机器id; 这里变成使用10位的机器id (b) <br>
 * <b> · </b>对系统时间的依赖性非常强，需关闭ntp的时间同步功能。当检测到ntp时间调整后，将会拒绝分配id
 * 
 * @author imadcn
 * @since 1.0.0
 */
public class Snowflake {

    private static final Logger logger = LoggerFactory.getLogger(Snowflake.class);

    /**
     * 机器ID
     */
    private final long workerId;
    /**
     * 时间起始标记点，作为基准，一般取系统的最近时间，默认2017-01-01
     */
    private final long epoch = 1483200000000L;
    /**
     * 机器id所占的位数（源设计为5位，这里取消dataCenterId，采用10位，既1024台）
     */
    private final long workerIdBits = 10L;
    /**
     * 机器ID最大值: 1023 (从0开始)
     */
    private final long maxWorkerId = -1L ^ -1L << workerIdBits;
    /**
     * 序列在id中占的位数
     */
    private final long sequenceBits = 12L;
    /**
     * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)，12位
     */
    private final long sequenceMask = -1L ^ -1L << sequenceBits;
    /**
     * 机器ID向左移12位
     */
    private final long workerIdShift = sequenceBits;
    /**
     * 时间戳向左移22位(5+5+12)
     */
    private final long timestampLeftShift = sequenceBits + workerIdBits;
    /**
     * 并发控制，毫秒内序列(0~4095)
     */
    private long sequence = 0L;
    /**
     * 上次生成ID的时间戳
     */
    private long lastTimestamp = -1L;
    /**
     * 100,000
     */
    private final int HUNDRED_K = 100_000;
    /**
     * sequence随机种子（兼容低并发下，sequence均为0的情况）
     */
    private static final Random RANDOM = new Random();

    /**
     * @param workerId 机器Id
     */
    private Snowflake(long workerId) {
        if (workerId > maxWorkerId || workerId < 0) {
            String message = String.format("worker Id can't be greater than %d or less than 0", maxWorkerId);
            throw new IllegalArgumentException(message);
        }
        this.workerId = workerId;
    }

    /**
     * Snowflake Builder
     * 
     * @param workerId 机器Id
     * @return Snowflake Instance
     */
    public static Snowflake create(long workerId) {
        return new Snowflake(workerId);
    }

    /**
     * Snowflake Builder
     * 
     * @param workerId 机器Id
     * @param lowConcurrency 是否低并发模式
     * @return Snowflake Instance
     */
    @Deprecated
    public static Snowflake create(long workerId, boolean lowConcurrency) {
        return create(workerId);
    }

    /**
     * 批量获取ID
     * 
     * @param size 获取大小，最多10万个
     * @return SnowflakeId
     */
    public long[] nextId(int size) {
        if (size <= 0 || size > HUNDRED_K) {
            String message = String.format("Size can't be greater than %d or less than 0", HUNDRED_K);
            throw new IllegalArgumentException(message);
        }
        long[] ids = new long[size];
        for (int i = 0; i < size; i++) {
            ids[i] = nextId();
        }
        return ids;
    }

    /**
     * 获得ID
     * 
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = timeGen();
        // 如果上一个timestamp与新产生的相等，则sequence加一(0-4095循环);
        if (lastTimestamp == timestamp) {
            // 对新的timestamp，sequence从0开始
            sequence = sequence + 1 & sequenceMask;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                sequence = RANDOM.nextInt(100);
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 时间戳改变，毫秒内序列重置
            sequence = RANDOM.nextInt(100);
        }
        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            String message = String.format("Clock moved backwards. Refusing to generate id for %d milliseconds.",
                (lastTimestamp - timestamp));
            logger.error(message);
            throw new RuntimeException(message);
        }
        lastTimestamp = timestamp;
        // 移位并通过或运算拼到一起组成64位的ID
        return timestamp - epoch << timestampLeftShift | workerId << workerIdShift | sequence;
    }

    /**
     * 等待下一个毫秒的到来, 保证返回的毫秒数在参数lastTimestamp之后
     * 
     * @param lastTimestamp 上次生成ID的时间戳
     * @return 下一个毫秒
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 获得系统当前毫秒数
     * 
     * @return 获得系统当前毫秒数
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }

}
