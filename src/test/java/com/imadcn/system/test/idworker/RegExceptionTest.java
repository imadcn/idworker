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

import com.imadcn.framework.idworker.exception.RegException;

public class RegExceptionTest {

    @Test
    public void test() {
        try {
            throw new RegException(new RuntimeException("test 1"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            throw new RegException("error message %s-%s", "s1", "s2");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
