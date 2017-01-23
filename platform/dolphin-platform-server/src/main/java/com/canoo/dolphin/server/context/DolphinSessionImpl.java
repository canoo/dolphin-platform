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
package com.canoo.dolphin.server.context;

import com.canoo.dolphin.server.DolphinSession;
import com.canoo.common.Assert;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * Default implementation of {@link DolphinSession} that uses a map internally to store all attributes
 */
public class DolphinSessionImpl implements DolphinSession {

    private final Map<String, Object> store;

    private final String dolphinSessionId;

    private final Executor runLaterExecutor;

    public DolphinSessionImpl(final String dolphinSessionId, final Executor runLaterExecutor) {
        this.dolphinSessionId = Assert.requireNonBlank(dolphinSessionId, "dolphinSessionId");
        this.runLaterExecutor = Assert.requireNonNull(runLaterExecutor, "runLaterExecutor");
        this.store = new ConcurrentHashMap<>();
    }

    @Override
    public void setAttribute(String name, Object value) {
        store.put(name, value);
    }

    @Override
    public Object getAttribute(String name) {
        return store.get(name);
    }

    @Override
    public void removeAttribute(String name) {
        store.remove(name);
    }

    @Override
    public Set<String> getAttributeNames() {
        return Collections.unmodifiableSet(store.keySet());
    }

    @Override
    public void invalidate() {
        store.clear();
    }

    @Override
    public String getId() {
        return dolphinSessionId;
    }

    @Override
    public void runLater(Runnable runnable) {
        runLaterExecutor.execute(runnable);
    }
}
