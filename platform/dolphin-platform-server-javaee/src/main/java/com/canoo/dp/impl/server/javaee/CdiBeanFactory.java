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
package com.canoo.dp.impl.server.javaee;

import com.canoo.dolphin.BeanManager;
import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.server.bootstrap.PlatformBootstrap;
import com.canoo.dp.impl.server.client.ClientSessionProvider;
import com.canoo.impl.server.context.DolphinContext;
import com.canoo.impl.server.context.DolphinContextProvider;
import com.canoo.impl.server.context.RemotingContextImpl;
import com.canoo.impl.server.event.LazyEventBusInvocationHandler;
import com.canoo.platform.server.RemotingContext;
import com.canoo.platform.server.binding.PropertyBinder;
import com.canoo.platform.server.client.ClientSession;
import com.canoo.platform.server.event.DolphinEventBus;
import com.canoo.platform.server.javaee.ClientScoped;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.lang.reflect.Proxy;

/**
 * Factory that provides all needed Dolphin Platform extensions as CDI beans.
 *
 * @author Hendrik Ebbers
 */
@ApplicationScoped
public class CdiBeanFactory {

    @Produces
    @ClientScoped
    public BeanManager createManager(RemotingContext remotingContext) {
        Assert.requireNonNull(remotingContext, "remotingContext");
        return remotingContext.getBeanManager();
    }

    @Produces
    @ClientScoped
    public RemotingContext createRemotingContext(DolphinEventBus eventBus) {
        Assert.requireNonNull(eventBus, "eventBus");

        final DolphinContextProvider contextProvider = PlatformBootstrap.getServerCoreComponents().getInstance(DolphinContextProvider.class);
        Assert.requireNonNull(contextProvider, "contextProvider");

        final DolphinContext context =contextProvider.getCurrentDolphinContext();
        Assert.requireNonNull(context, "context");

        return new RemotingContextImpl(context, eventBus);
    }

    @Produces
    @ClientScoped
    public ClientSession createDolphinSession() {
        final ClientSessionProvider provider = PlatformBootstrap.getServerCoreComponents().getInstance(ClientSessionProvider.class);
        Assert.requireNonNull(provider, "provider");
        return provider.getCurrentClientSession();
    }

    @Produces
    @ApplicationScoped
    public DolphinEventBus createEventBus() {
        return (DolphinEventBus) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{DolphinEventBus.class}, new LazyEventBusInvocationHandler());
    }

    @Produces
    @ClientScoped
    public PropertyBinder createPropertyBinder(RemotingContext remotingContext) {
        Assert.requireNonNull(remotingContext, "remotingContext");
        return remotingContext.getBinder();
    }
}
