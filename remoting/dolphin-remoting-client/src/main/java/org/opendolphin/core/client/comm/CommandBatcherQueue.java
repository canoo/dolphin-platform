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
package org.opendolphin.core.client.comm;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CommandBatcherQueue implements DataflowQueue<List<CommandAndHandler>> {

    private final List<List<CommandAndHandler>> internalQueue = new LinkedList<>();

    private final Lock queueLock = new ReentrantLock();

    private final Condition emptyCondition = queueLock.newCondition();

    @Override
    public List<CommandAndHandler> getVal() throws InterruptedException {
        queueLock.lock();
        try {
            if (internalQueue.isEmpty()) {
                emptyCondition.await();
            }
            if (internalQueue.isEmpty()) {
                return null;
            }
            return internalQueue.remove(0);
        } finally {
            queueLock.unlock();
        }
    }

    @Override
    public void add(List<CommandAndHandler> value) {
        queueLock.lock();
        try {
            internalQueue.add(value);
            emptyCondition.signal();
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
};
