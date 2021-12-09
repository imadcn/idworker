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
package com.imadcn.framework.idworker.generator;

import com.imadcn.framework.idworker.algorithm.CompressUUID;

/**
 * 64进制的UUID
 * 
 * @author imadcn
 * @since 1.2.0
 */
public class CompressUUIDGenerator implements IdGenerator {

    @Override
    public long[] nextId(int size) {
        throw new UnsupportedOperationException(
            "unsupported operation[public long[] nextId(int size)] in CompressUUIDGenerator");
    }

    @Override
    public long nextId() {
        throw new UnsupportedOperationException("unsupported operation[public long nextId()] in CompressUUIDGenerator");
    }

    @Override
    public String nextStringId() {
        return CompressUUID.uuid22();
    }

    @Override
    public String nextFixedStringId() {
        return nextStringId();
    }

}
