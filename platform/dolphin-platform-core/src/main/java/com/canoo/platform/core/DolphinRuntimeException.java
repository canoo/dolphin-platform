/*
 * Copyright 2015-2017 Canoo Engineering AG.
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
package com.canoo.platform.core;

import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * General runtime exception for the Dolphin Platform
 *
 * @author Hendrik Ebbers
 */
@API(since = "0.x", status = MAINTAINED)
public class DolphinRuntimeException extends RuntimeException {

    private static final Logger LOG = LoggerFactory.getLogger(DolphinRuntimeException.class);

    private final transient Thread thread;

    public DolphinRuntimeException(final String s) {
        super(s);
        this.thread = Thread.currentThread();
    }

    /**
     * Constructor
     *
     * @param message message of the exception
     * @param cause   cause of the exception
     */
    public DolphinRuntimeException(final String message, final Throwable cause) {
        this(Thread.currentThread(), message, cause);
    }

    /**
     * Constructor
     *
     * @param thread  Thread in that the exception happend
     * @param message message of the exception
     * @param cause   cause of the exception
     */
    public DolphinRuntimeException(final Thread thread, final String message, final Throwable cause) {
        super(message, cause);
        if (thread != null) {
            this.thread = thread;
        } else {
            LOG.error("Can not specify thread for Dolphin Platform runtime error!");
            this.thread = Thread.currentThread();
        }
    }

    /**
     * Returns the thread in that this exception was created
     *
     * @return the thread in that this exception was created
     */
    public Thread getThread() {
        return thread;
    }
}
