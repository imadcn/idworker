/*
 * Copyright 2013-2018 imadcn Group.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.imadcn.framework.idworker.common;

import com.imadcn.framework.idworker.algorithm.CompressUUID;
import com.imadcn.framework.idworker.algorithm.Snowflake;

/**
 * ID生成策略
 * 
 * @author imadcn
 * @since 1.2.0
 */
public enum GeneratorStrategy {
    /**
     * snowflake算法 {@link Snowflake}
     */
    SNOWFLAKE("snowflake"),
    /**
     * 64进制UUID算法 {@link CompressUUID}
     */
    COMPRESS_UUID("compress_uuid"),;

    /**
     * 策略Code
     */
    private String code;

    private GeneratorStrategy(String code) {
        this.code = code;
    }

    /**
     * 获取策略CODE
     * 
     * @return 策略CODE
     */
    public String getCode() {
        return code;
    }

    /**
     * 根据code获取生成策略enum
     * 
     * @param code 策略Code
     * @return 生成策略
     */
    public static GeneratorStrategy getByCode(String code) {
        if (code != null) {
            GeneratorStrategy[] strategyValues = values();
            if (strategyValues != null) {
                for (GeneratorStrategy strategy : strategyValues) {
                    if (code.equals(strategy.getCode())) {
                        return strategy;
                    }
                }
            }
        }
        return null;
    }
}
