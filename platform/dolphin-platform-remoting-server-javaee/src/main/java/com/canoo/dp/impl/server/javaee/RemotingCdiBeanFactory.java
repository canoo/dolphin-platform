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

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.server.bootstrap.PlatformBootstrap;
import com.canoo.dp.impl.server.context.DolphinContextProvider;
import com.canoo.dp.impl.server.context.RemotingContextImpl;
import com.canoo.dp.impl.server.event.LazyEventBusInvocationHandler;
import com.canoo.platform.core.DolphinRuntimeException;
import com.canoo.platform.remoting.BeanManager;
import com.canoo.platform.remoting.server.RemotingContext;
import com.canoo.platform.remoting.server.binding.PropertyBinder;
import com.canoo.platform.remoting.server.event.RemotingEventBus;
import com.canoo.platform.server.javaee.ClientScoped;
import org.apiguardian.api.API;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.lang.reflect.Proxy;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * Factory that provides all needed Dolphin Platform extensions as CDI beans.
 *
 * @author Hendrik Ebbers
 */
@ApplicationScoped
@API(since = "0.x", status = INTERNAL)
public class RemotingCdiBeanFactory {

    @Produces
    @ClientScoped
    public BeanManager createManager(RemotingContext remotingContext) {
        Assert.requireNonNull(remotingContext, "remotingContext");
        return remotingContext.getBeanManager();
    }

    @Produces
    @ClientScoped
    public RemotingContext createRemotingContext(RemotingEventBus eventBus) {
        return PlatformBootstrap.getServerCoreComponents().
                getInstance(DolphinContextProvider.class).
                map(p -> p.getCurrentDolphinContext()).
                map(c -> new RemotingContextImpl(c, eventBus)).
                orElseThrow(() -> new DolphinRuntimeException(RemotingContext.class + " can not be provided!"));
    }

    @Produces
    @ApplicationScoped
    public RemotingEventBus createEventBus() {
        return (RemotingEventBus) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{RemotingEventBus.class}, new LazyEventBusInvocationHandler());
    }

    @Produces
    @ClientScoped
    public PropertyBinder createPropertyBinder(RemotingContext remotingContext) {
        Assert.requireNonNull(remotingContext, "remotingContext");
        return remotingContext.getBinder();
    }
}
