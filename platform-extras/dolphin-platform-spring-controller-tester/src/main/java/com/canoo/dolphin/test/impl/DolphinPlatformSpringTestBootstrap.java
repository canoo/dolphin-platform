/*
 * Copyright 2015-2016 Canoo Engineering AG.
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
package com.canoo.dolphin.test.impl;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.client.ClientBeanManager;
import com.canoo.dolphin.client.ClientConfiguration;
import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.ControllerProxy;
import com.canoo.dolphin.client.impl.ClientBeanBuilderImpl;
import com.canoo.dolphin.client.impl.ClientBeanManagerImpl;
import com.canoo.dolphin.client.impl.ClientContextImpl;
import com.canoo.dolphin.client.impl.ClientEventDispatcher;
import com.canoo.dolphin.client.impl.ClientPlatformBeanRepository;
import com.canoo.dolphin.client.impl.ClientPresentationModelBuilderFactory;
import com.canoo.dolphin.client.impl.ControllerProxyFactory;
import com.canoo.dolphin.client.impl.ControllerProxyFactoryImpl;
import com.canoo.dolphin.client.impl.DolphinCommandHandler;
import com.canoo.dolphin.client.impl.ForwardableCallback;
import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.impl.BeanRepositoryImpl;
import com.canoo.dolphin.impl.ClassRepositoryImpl;
import com.canoo.dolphin.impl.Converters;
import com.canoo.dolphin.impl.PresentationModelBuilderFactory;
import com.canoo.dolphin.impl.ReflectionHelper;
import com.canoo.dolphin.impl.collections.ListMapperImpl;
import com.canoo.dolphin.internal.BeanBuilder;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.ClassRepository;
import com.canoo.dolphin.internal.EventDispatcher;
import com.canoo.dolphin.internal.collections.ListMapper;
import com.canoo.dolphin.server.DolphinSession;
import com.canoo.dolphin.server.binding.PropertyBinder;
import com.canoo.dolphin.server.binding.impl.PropertyBinderImpl;
import com.canoo.dolphin.server.context.DolphinContext;
import com.canoo.dolphin.server.context.DolphinContextProvider;
import com.canoo.dolphin.server.context.DolphinSessionProvider;
import com.canoo.dolphin.server.controller.ControllerRepository;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.impl.DolphinEventBusImpl;
import com.canoo.dolphin.server.impl.ClasspathScanner;
import com.canoo.dolphin.server.spring.ClientScope;
import com.canoo.dolphin.test.ControllerTestException;
import com.canoo.dolphin.util.Assert;
import com.canoo.dolphin.util.Callback;
import com.canoo.dolphin.util.DolphinRemotingException;
import org.apache.http.client.HttpClient;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.comm.UiThreadHandler;
import org.opendolphin.core.server.ServerModelStore;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Configuration
public class DolphinPlatformSpringTestBootstrap {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ClientContext createClientContext(final DolphinTestContext dolphinContext, final TestInMemoryConfiguration config) throws ExecutionException, InterruptedException, MalformedURLException {
        Assert.requireNonNull(dolphinContext, "dolphinContext");
        final ClientDolphin clientDolphin = dolphinContext.getClientDolphin();

        final URL dummyURL = new URL("http://dummyURL");
        final ClientConfiguration clientConfiguration = new ClientConfiguration(dummyURL, new UiThreadHandler() {

            @Override
            public void executeInsideUiThread(Runnable runnable) {
                config.getClientExecutor().execute(runnable);
            }
        });
        clientConfiguration.setConnectionTimeout(Long.MAX_VALUE);
        final DolphinCommandHandler dolphinCommandHandler = new DolphinCommandHandler(clientDolphin);
        final EventDispatcher dispatcher = new ClientEventDispatcher(clientDolphin);
        final BeanRepository beanRepository = new BeanRepositoryImpl(clientDolphin, dispatcher);
        final Converters converters = new Converters(beanRepository);
        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(clientDolphin);
        final ClassRepository classRepository = new ClassRepositoryImpl(clientDolphin, converters, builderFactory);
        final ListMapper listMapper = new ListMapperImpl(clientDolphin, classRepository, beanRepository, builderFactory, dispatcher);
        final BeanBuilder beanBuilder = new ClientBeanBuilderImpl(classRepository, beanRepository, listMapper, builderFactory, dispatcher);
        final ClientPlatformBeanRepository platformBeanRepository = new ClientPlatformBeanRepository(clientDolphin, beanRepository, dispatcher, converters);
        final ClientBeanManagerImpl clientBeanManager = new ClientBeanManagerImpl(beanRepository, beanBuilder, clientDolphin);
        final ControllerProxyFactory controllerProxyFactory = new ControllerProxyFactoryImpl(platformBeanRepository, dolphinCommandHandler, clientDolphin);
        clientConfiguration.setHttpClient(new HttpClientMock());
        final ClientContext clientContext = new ClientContextImpl(clientConfiguration, clientDolphin, controllerProxyFactory, dolphinCommandHandler, platformBeanRepository, clientBeanManager, new ForwardableCallback());

        config.getClientExecutor().submit(new Runnable() {
            @Override
            public void run() {
                //Currently the event bus can not used in tests. See https://github.com/canoo/dolphin-platform/issues/196
                //clientDolphin.startPushListening(PlatformConstants.POLL_EVENT_BUS_COMMAND_NAME, PlatformConstants.RELEASE_EVENT_BUS_COMMAND_NAME);
            }

        }).get();

        return new ClientContextForTests() {
            @Override
            public void sync() {
                final CompletableFuture<Void> future = new CompletableFuture<>();
                clientDolphin.sync(new Runnable() {
                    @Override
                    public void run() {
                        future.complete(null);
                    }
                });
                try {
                    future.get();
                } catch (Exception e) {
                    throw new RuntimeException("Error in sync call", e);
                }
            }

            @Override
            public <T> CompletableFuture<ControllerProxy<T>> createController(final String name) {
                return clientContext.createController(name);
            }

            @Override
            public ClientBeanManager getBeanManager() {
                return clientContext.getBeanManager();
            }

            @Override
            public CompletableFuture<Void> disconnect() {
                return clientContext.disconnect();
            }

            @Override
            public Subscription onRemotingError(final Callback<DolphinRemotingException> callback) {
                return clientContext.onRemotingError(callback);
            }

            @Override
            public HttpClient getHttpClient() {
                return clientContext.getHttpClient();
            }
        };
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public DolphinTestContext createServerContext(final TestInMemoryConfiguration config,
                                                  final WebApplicationContext context) throws ExecutionException, InterruptedException {
        Assert.requireNonNull(config, "config");
        Assert.requireNonNull(context, "context");
        ControllerRepository controllerRepository = new ControllerRepository(new ClasspathScanner());
        TestSpringContainerManager containerManager = new TestSpringContainerManager(context);
        containerManager.init(context.getServletContext());
        DolphinContextProviderMock dolphinContextProviderMock = new DolphinContextProviderMock();
        DolphinEventBusImpl dolphinEventBus = new DolphinEventBusImpl(dolphinContextProviderMock);
        DolphinTestContext dolphinContext = new DolphinTestContext(containerManager, controllerRepository, config, dolphinEventBus);
        dolphinContextProviderMock.setCurrentContext(dolphinContext);
        return dolphinContext;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public TestInMemoryConfiguration createInMemoryConfig() throws ExecutionException, InterruptedException {
        TestInMemoryConfiguration config = new TestInMemoryConfiguration();
        config.getServerDolphin().registerDefaultActions();
        ServerModelStore store = config.getServerDolphin().getServerModelStore();
        try {
            ReflectionHelper.setPrivileged(ServerModelStore.class.getDeclaredField("currentResponse"), store, new ArrayList<>());
        } catch (NoSuchFieldException e) {
            throw new ControllerTestException(e);
        }
        return config;
    }

    /**
     * Method to create a spring managed {@link com.canoo.dolphin.impl.BeanManagerImpl} instance in client scope.
     *
     * @return the instance
     */
    @Bean(name = "beanManager")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected BeanManager createManager(final DolphinTestContext context) {
        Assert.requireNonNull(context, "context");
        return context.getBeanManager();
    }


    @Bean(name = "dolphinSession")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected DolphinSession createDolphinSession(final DolphinTestContext context) {
        Assert.requireNonNull(context, "context");
        return context.getCurrentDolphinSession();
    }


    /**
     * Method to create a spring managed {@link DolphinEventBus} instance in singleton scope.
     *
     * @return the instance
     */
    @Bean(name = "dolphinEventBus")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected DolphinEventBus createEventBus(final DolphinTestContext context) {
        Assert.requireNonNull(context, "context");
        return context.getDolphinEventBus();
    }

    @Bean(name = "propertyBinder")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected PropertyBinder createPropertyBinder() {
        return new PropertyBinderImpl();
    }

    @Bean(name = "customScopeConfigurer")
    public static CustomScopeConfigurer createClientScope(final DolphinTestContext context) {
        Assert.requireNonNull(context, "context");
        CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        configurer.addScope(ClientScope.CLIENT_SCOPE, new ClientScope(new DolphinSessionProvider() {
            @Override
            public DolphinSession getCurrentDolphinSession() {
                return context.getCurrentDolphinSession();
            }
        }));
        return configurer;
    }

    private class DolphinContextProviderMock implements DolphinContextProvider {

        DolphinContext currentContext;

        public void setCurrentContext(DolphinContext currentContext) {
            this.currentContext = currentContext;
        }

        @Override
        public DolphinContext getCurrentContext() {
            return currentContext;
        }

        @Override
        public DolphinSession getCurrentDolphinSession() {
            return getCurrentContext().getCurrentDolphinSession();
        }
    }

}
