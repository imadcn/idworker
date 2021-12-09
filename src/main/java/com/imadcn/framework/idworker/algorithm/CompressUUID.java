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
package com.imadcn.framework.idworker.algorithm;

import java.util.UUID;

/**
 * 64进制的UUID，取值范围 [a-zA-Z0-9_-]
 * 
 * @author imadcn
 * @since 1.2.0
 */
public class CompressUUID {

    private static final String[] CHARS = new String[] {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
        "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8",
        "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
        "V", "W", "X", "Y", "Z", "_", "-"};

    /**
     * 不带"-"符号的UUID
     * 
     * @return 不带"-"符号的UUID
     */
    public static String noneDashUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 22位长度的 UUID
     * 
     * @return 22位长度的 UUID
     */
    public static String uuid22() {
        StringBuffer r = new StringBuffer();
        String uuid = new StringBuilder().append("0").append(noneDashUuid()).toString();
        int index = 0;
        int[] buff = new int[3];
        int l = uuid.length();
        for (int i = 0; i < l; i++) {
            index = i % 3;
            buff[index] = Integer.parseInt("" + uuid.charAt(i), 16);
            if (index == 2) {
                r.append(CHARS[buff[0] << 2 | buff[1] >>> 2]);
                r.append(CHARS[(buff[1] & 3) << 4 | buff[2]]);
            }
        }
        return r.toString();
    }
}
