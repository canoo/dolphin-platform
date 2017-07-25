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
package com.canoo.dp.impl.server.client;

import com.canoo.platform.server.client.ClientSession;
import com.canoo.dp.impl.platform.core.Assert;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of {@link ClientSession} that uses a map internally to store all attributes
 */
public class HttpClientSessionImpl implements ClientSession {

    private final Map<String, Object> store;

    private final String httpSessionId;

    private final String dolphinSessionId;

    public HttpClientSessionImpl() {
        this(UUID.randomUUID().toString());
    }

    public HttpClientSessionImpl(final String httpSessionId) {
        this.httpSessionId = Assert.requireNonBlank(httpSessionId, "httpSessionId");
        this.dolphinSessionId = UUID.randomUUID().toString();
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

}
