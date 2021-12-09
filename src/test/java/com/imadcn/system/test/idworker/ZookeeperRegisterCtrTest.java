/*
 * Copyright 2013-2019 imadcn Group.
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

import com.imadcn.framework.idworker.registry.zookeeper.ZookeeperRegistryCenter;
import com.imadcn.system.test.spring.AbstractZookeeperJUnit4SpringContextTests;

@ContextConfiguration(locations = "classpath:META-INF/idworker-reg-ctr.xml")
public final class ZookeeperRegisterCtrTest extends AbstractZookeeperJUnit4SpringContextTests {

    @Autowired
    private ZookeeperRegistryCenter regCtr;

    @Test
    public void testSequential() {
        String seq = regCtr.persistSequential("/idworker/sequential/t_seq_", "SEQUENCE");
        print(seq);
    }
}
