/*
 * Copyright 2015-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.implementation.dolphin.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DolphinRuntimeException extends RuntimeException {

    private static final Logger LOG = LoggerFactory.getLogger(DolphinRuntimeException.class);

    private final transient Thread thread;

    public DolphinRuntimeException(String message, Throwable cause) {
        this(Thread.currentThread(), message, cause);
    }

    public DolphinRuntimeException(Thread thread, String message, Throwable cause) {
        super(message, cause);
        if(thread != null) {
            this.thread = thread;
        } else {
            LOG.error("Can not specify thread for Dolphin Platform runtime error!");
            this.thread = Thread.currentThread();
        }
    }

    public Thread getThread() {
        return thread;
    }
}
