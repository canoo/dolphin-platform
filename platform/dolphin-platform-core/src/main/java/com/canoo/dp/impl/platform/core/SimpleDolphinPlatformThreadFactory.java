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
package com.canoo.dp.impl.platform.core;

import com.canoo.platform.core.PlatformThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleDolphinPlatformThreadFactory implements PlatformThreadFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleDolphinPlatformThreadFactory.class);

    private final AtomicInteger threadNumber = new AtomicInteger(0);

    private final Lock uncaughtExceptionHandlerLock = new ReentrantLock();

    private final ThreadGroup group;

    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    public SimpleDolphinPlatformThreadFactory() {
        this.uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable exception) {
                Assert.requireNonNull(thread, "thread");
                Assert.requireNonNull(exception, "exception");
                LOG.error("Unhandled error in Dolphin Platform background thread " + thread.getName(), exception);
            }
        };
        this.group = new ThreadGroup("Dolphin Platform executors");
    }

    @Override
    public Thread newThread(final Runnable task) {
        Assert.requireNonNull(task, "task");
        return AccessController.doPrivileged(new PrivilegedAction<Thread>() {
            @Override
            public Thread run() {
                final Thread backgroundThread = new Thread(group, task);
                backgroundThread.setName("Dolphin-Platform-Background-Thread-" + threadNumber.getAndIncrement());
                backgroundThread.setDaemon(false);
                uncaughtExceptionHandlerLock.lock();
                try {
                    backgroundThread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
                } finally {
                    uncaughtExceptionHandlerLock.unlock();
                }
                return backgroundThread;
            }
        });
    }

    public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        Assert.requireNonNull(uncaughtExceptionHandler, "uncaughtExceptionHandler");
        uncaughtExceptionHandlerLock.lock();
        try {
            this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        } finally {
            uncaughtExceptionHandlerLock.unlock();
        }
    }
}
