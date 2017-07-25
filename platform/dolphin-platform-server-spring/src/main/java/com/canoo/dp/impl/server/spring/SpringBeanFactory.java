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
import com.canoo.platform.server.spring.ClientScoped;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.lang.reflect.Proxy;

/**
 * Provides all Dolphin Platform Beans and Scopes for CDI
 */
@Configuration
public class SpringBeanFactory {

    @Bean(name = "remotingContext")
    @ClientScoped
    protected RemotingContext createRemotingContext(DolphinEventBus eventBus) {
        Assert.requireNonNull(eventBus, "eventBus");

        final DolphinContextProvider contextProvider = PlatformBootstrap.getServerCoreComponents().getInstance(DolphinContextProvider.class);
        Assert.requireNonNull(contextProvider, "contextProvider");

        final DolphinContext context = contextProvider.getCurrentDolphinContext();
        Assert.requireNonNull(context, "context");

        return new RemotingContextImpl(context, eventBus);
    }

    /**
     * Method to create a spring managed {@link com.canoo.dolphin.impl.BeanManagerImpl} instance in client scope.
     *
     * @return the instance
     */
    @Bean(name = "beanManager")
    @ClientScoped
    protected BeanManager createManager(RemotingContext remotingContext) {
        Assert.requireNonNull(remotingContext, "remotingContext");
        return remotingContext.getBeanManager();
    }

    @Bean(name = "clientSession")
    @ClientScoped
    protected ClientSession createClientSession() {
        final ClientSessionProvider provider = PlatformBootstrap.getServerCoreComponents().getInstance(ClientSessionProvider.class);
        Assert.requireNonNull(provider, "provider");
        return provider.getCurrentClientSession();
    }

    /**
     * Method to create a spring managed {@link DolphinEventBus} instance in singleton scope.
     *
     * @return the instance
     */
    @Bean(name = "dolphinEventBus")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected DolphinEventBus createEventBus() {
        return (DolphinEventBus) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{DolphinEventBus.class}, new LazyEventBusInvocationHandler());
    }

    @Bean(name = "propertyBinder")
    @ClientScoped
    protected PropertyBinder createPropertyBinder(RemotingContext remotingContext) {
        Assert.requireNonNull(remotingContext, "remotingContext");
        return remotingContext.getBinder();
    }

    @Bean(name = "customScopeConfigurer")
    public static CustomScopeConfigurer createClientScope() {
        final CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        configurer.addScope(ClientScope.CLIENT_SCOPE, new ClientScope());
        return configurer;
    }
}
