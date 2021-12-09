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
package com.imadcn.framework.idworker.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Host Utils
 * 
 * @author imadcn
 * @since 1.3.0
 */
public class HostUtils {

    /**
     * 获取本地IP
     * 
     * @return 本地IP
     * @throws UnknownHostException UnknownHostException
     */
    public static String getLocalIP() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    /**
     * 获取本地主机名
     * 
     * @return 本地主机名
     * @throws UnknownHostException UnknownHostException
     */
    public static String getLocalHostName() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }
}
