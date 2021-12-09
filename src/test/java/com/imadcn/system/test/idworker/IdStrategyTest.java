/*
 * Copyright 2013-2017 imadcn Group.
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
package com.imadcn.system.test.idworker;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.imadcn.framework.idworker.generator.CompressUUIDGenerator;
import com.imadcn.framework.idworker.generator.SnowflakeGenerator;
import com.imadcn.system.test.spring.AbstractZookeeperJUnit4SpringContextTests;

@ContextConfiguration(locations = "classpath:META-INF/idworker-ctx-strategy.xml")
public final class IdStrategyTest extends AbstractZookeeperJUnit4SpringContextTests {

    @Autowired
    private CompressUUIDGenerator compressUUIDGenerator;
    @Autowired
    private SnowflakeGenerator snowflakeGenerator;

    @Test
    public void testCompressId() {
        print(compressUUIDGenerator.nextStringId());
        print(compressUUIDGenerator.nextFixedStringId());
    }

    @Test
    public void testSnowflakeId() {
        print(snowflakeGenerator.nextStringId());
        print(snowflakeGenerator.nextFixedStringId());
    }

    @Test
    public void testUnsupportedMethod1() {
        try {
            print(compressUUIDGenerator.nextId());
        } catch (Exception e) {
            print(e);
        }
    }

    @Test
    public void testUnsupportedMethod2() {
        try {
            print(compressUUIDGenerator.nextId(2));
        } catch (Exception e) {
            print(e);
        }
    }
}
