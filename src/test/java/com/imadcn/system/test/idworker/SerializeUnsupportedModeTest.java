/*
 * Copyright 2013-2021 imadcn Group.
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

import com.imadcn.framework.idworker.config.ApplicationConfiguration;
import com.imadcn.framework.idworker.config.ZookeeperConfiguration;
import com.imadcn.framework.idworker.generator.SnowflakeGenerator;
import com.imadcn.framework.idworker.register.zookeeper.ZookeeperWorkerRegister;
import com.imadcn.framework.idworker.registry.zookeeper.ZookeeperRegistryCenter;
import com.imadcn.system.test.ResultPrinter;

public class SerializeUnsupportedModeTest extends ResultPrinter {

    @Test
    public void testUnsupported() {
        try {
            ZookeeperConfiguration configuration = new ZookeeperConfiguration();
            configuration.setServerLists("127.0.0.1:2181");
            configuration.setNamespace("manual_idworker");

            ZookeeperRegistryCenter registryCenter = new ZookeeperRegistryCenter(configuration);

            ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration();
            applicationConfiguration.setSerialize("unsupported");
            applicationConfiguration.setGroup("manual_group");

            ZookeeperWorkerRegister workerRegister
                = new ZookeeperWorkerRegister(registryCenter, applicationConfiguration);

            SnowflakeGenerator generator = new SnowflakeGenerator(workerRegister);

            generator.init();
            System.out.println(generator.nextId());
            generator.close();

            generator.init();
            System.out.println(generator.nextId());
            generator.close();

        } catch (Throwable e) {
            logger.error("", e);
        }
    }
}
