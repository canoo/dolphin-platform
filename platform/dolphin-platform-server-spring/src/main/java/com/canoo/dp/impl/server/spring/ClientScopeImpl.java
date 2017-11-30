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
package com.canoo.dp.impl.server.spring;


import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.server.bootstrap.PlatformBootstrap;
import com.canoo.dp.impl.server.client.ClientSessionProvider;
import com.canoo.platform.core.DolphinRuntimeException;
import com.canoo.platform.server.client.ClientSession;
import org.apiguardian.api.API;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * Implementation of the {@link com.canoo.platform.server.spring.ClientScope} scope
 */
@API(since = "0.x", status = INTERNAL)
public class ClientScopeImpl implements Scope {

    public final static String CLIENT_SCOPE = "client";

    private final static String CLIENT_STORE_ATTRIBUTE = "DolphinPlatformSpringClientScopeStore";

    @Override
    public Object get(final String name, final ObjectFactory<?> objectFactory) {
        Assert.requireNonBlank(name, "name");
        Assert.requireNonNull(objectFactory, "objectFactory");
        Map<String, Object> localStore = getLocalStore();
        if (!localStore.containsKey(name)) {
            localStore.put(name, objectFactory.getObject());
        }
        return localStore.get(name);
    }

    @Override
    public Object remove(final String name) {
        return getLocalStore().remove(name);
    }

    @Override
    public void registerDestructionCallback(final String name, final Runnable callback) {

    }

    @Override
    public Object resolveContextualObject(final String key) {
        return null;
    }

    private Map<String, Object> getLocalStore() {
        ClientSession session = getClientSession();
        if(session == null) {
            throw new IllegalStateException("No dolphin request found! Looks like you try to use the " + ClientScopeImpl.class.getSimpleName() + " ouside of the dolphin context!");
        }
        Map<String, Object> localStore = session.getAttribute(CLIENT_STORE_ATTRIBUTE);
        if(localStore == null) {
            localStore = Collections.synchronizedMap(new HashMap<String, Object>());
            session.setAttribute(CLIENT_STORE_ATTRIBUTE, localStore);
        }
        return localStore;
    }

    @Override
    public String getConversationId() {
        return getClientSession().getId();
    }

    protected ClientSession getClientSession() {
        final ClientSessionProvider clientSessionProvider = PlatformBootstrap.getServerCoreComponents().getInstance(ClientSessionProvider.class).orElseThrow(() -> new DolphinRuntimeException(ClientSessionProvider.class.getName() + " not found!"));
        return clientSessionProvider.getCurrentClientSession();
    }
}
