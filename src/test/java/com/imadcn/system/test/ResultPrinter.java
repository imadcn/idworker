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
package com.imadcn.system.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

public class ResultPrinter {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected void print(Object object) {
        String json = JSON.toJSONString(object);
        System.out.println(json);
        logger.info(json);
    }

    protected void rawPrint(Object object) {
        System.out.println(object);
        logger.info("", object);
    }
}
