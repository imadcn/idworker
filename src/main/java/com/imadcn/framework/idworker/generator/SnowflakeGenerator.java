package com.imadcn.framework.idworker.generator;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imadcn.framework.idworker.algorithm.Snowflake;
import com.imadcn.framework.idworker.exception.RegException;
import com.imadcn.framework.idworker.register.GeneratorConnector;
import com.imadcn.framework.idworker.register.zookeeper.ZookeeperWorkerRegister;

/**
 * Snowflake算法生成工具
 * 
 * @author imadcn
 * @since 1.0.0
 */
public class SnowflakeGenerator implements IdGenerator, GeneratorConnector {

    static final String FIXED_STRING_FORMAT = "%019d";

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * snowflake 算法
     */
    private Snowflake snowflake;
    /**
     * snowflake 注册
     */
    private ZookeeperWorkerRegister register;
    /**
     * 是否正在工作
     */
    private volatile boolean initialized = false;

    private volatile boolean working = false;

    private volatile boolean connecting = false;

    public SnowflakeGenerator(ZookeeperWorkerRegister register) {
        this.register = register;
    }

    @Override
    public synchronized void init() {
        if (!initialized) {
            // 持久化节点+本地缓存，不再使用状态监听
            // listener = new ZookeeperConnectionStateListener(this);
            // // 添加监听
            // register.addConnectionListener(listener);
            // 连接与注册workerId
            connect();
            initialized = true;
        }
    }

    /**
     * 初始化
     */
    @Override
    public void connect() {
        if (!isConnecting()) {
            working = false;
            connecting = true;
            long workerId = register.register();
            if (workerId >= 0) {
                snowflake = Snowflake.create(workerId);
                working = true;
                connecting = false;
            } else {
                throw new RegException("failed to get worker id");
            }
        } else {
            logger.info("worker is connecting, skip this time of register.");
        }
    }

    @Override
    public long[] nextId(int size) {
        if (isWorking()) {
            return snowflake.nextId(size);
        }
        throw new IllegalStateException("worker isn't working, reg center may shutdown");
    }

    @Override
    public long nextId() {
        if (isWorking()) {
            return snowflake.nextId();
        }
        throw new IllegalStateException("worker isn't working, reg center may shutdown");
    }

    @Override
    public String nextStringId() {
        return String.valueOf(nextId());
    }

    @Override
    public String nextFixedStringId() {
        return String.format(FIXED_STRING_FORMAT, nextId());
    }

    @Override
    public void suspend() {
        this.working = false;
    }

    @Override
    public synchronized void close() throws IOException {
        // 关闭，先重置状态(避免ZK删除 workerId，其他机器抢注，会导致workerID 重新生成的BUG)
        reset();
        register.logout();
    }

    @Override
    public boolean isWorking() {
        return this.working;
    }

    @Override
    public boolean isConnecting() {
        return this.connecting;
    }

    /**
     * 重置连接状态
     */
    protected void reset() {
        initialized = false;
        working = false;
        connecting = false;
    }

    /**
     * 低并发模式
     * 
     * @param lowConcurrency 低并发模式 ? true : false
     */
    @Deprecated
    public void setLowConcurrency(boolean lowConcurrency) {
    }
}
