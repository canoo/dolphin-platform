/*
 * Copyright 2015-2018 Canoo Engineering AG.
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
package com.canoo.dp.impl.client.legacy.communication;

import org.apiguardian.api.API;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.apiguardian.api.API.Status.DEPRECATED;

@API(since = "0.x", status = DEPRECATED)
public class CommandBatcherQueue implements DataflowQueue<List<CommandAndHandler>> {

    private final List<List<CommandAndHandler>> internalQueue = new LinkedList<>();

    private final Lock queueLock = new ReentrantLock();

    @Override
    public List<CommandAndHandler> getVal() throws InterruptedException {
        queueLock.lock();
        try {
            if (internalQueue.isEmpty()) {
                return Collections.emptyList();
            }
            return internalQueue.remove(0);
        } finally {
            queueLock.unlock();
        }
    }

    @Override
    public void add(final List<CommandAndHandler> value) {
        queueLock.lock();
        try {
            internalQueue.add(value);
        } finally {
            queueLock.unlock();
        }
    }

    @Override
    public int length() {
        queueLock.lock();
        try {
            return internalQueue.size();
        } finally {
            queueLock.unlock();
        }
    }

    @Override
    public void clear() {
        queueLock.lock();
        try {
            internalQueue.clear();
        } finally {
            queueLock.unlock();
        }
    }
};
