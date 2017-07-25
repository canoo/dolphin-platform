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
package com.canoo.impl.server.context;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.server.ClientSessionExecutor;
import com.google.common.util.concurrent.SettableFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

public class ClientSessionExecutorImpl implements ClientSessionExecutor {

    private final Executor runLaterExecutor;

    public ClientSessionExecutorImpl(final Executor runLaterExecutor) {
        this.runLaterExecutor = Assert.requireNonNull(runLaterExecutor, "runLaterExecutor");
    }

    @Override
    public Future<Void> runLaterInClientSession(final Runnable task) {
        Assert.requireNonNull(task, "task");
        return callLaterInClientSession(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                task.run();
                return null;
            }
        });
    }

    @Override
    public <T> Future<T> callLaterInClientSession(final Callable<T> task) {
        Assert.requireNonNull(task, "task");
        final SettableFuture<T> future = SettableFuture.<T>create();
        runLaterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    T result = task.call();
                    future.set(result);
                } catch (Exception e) {
                    future.setException(e);
                }
            }
        });
        return future;
    }
}
