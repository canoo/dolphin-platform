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
package com.canoo.dolphin.test.impl;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.client.ClientConfiguration;
import com.canoo.dolphin.client.ClientContext;
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
import com.canoo.dolphin.impl.BeanRepositoryImpl;
import com.canoo.dolphin.impl.ClassRepositoryImpl;
import com.canoo.dolphin.impl.Converters;
import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.impl.PresentationModelBuilderFactory;
import com.canoo.dolphin.impl.ReflectionHelper;
import com.canoo.dolphin.impl.collections.ListMapperImpl;
import com.canoo.dolphin.internal.BeanBuilder;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.ClassRepository;
import com.canoo.dolphin.internal.EventDispatcher;
import com.canoo.dolphin.internal.collections.ListMapper;
import com.canoo.dolphin.server.BackgroundRunner;
import com.canoo.dolphin.server.DolphinSession;
import com.canoo.dolphin.server.binding.PropertyBinder;
import com.canoo.dolphin.server.binding.impl.PropertyBinderImpl;
import com.canoo.dolphin.server.bootstrap.DolphinPlatformBootstrap;
import com.canoo.dolphin.server.config.ConfigurationFileLoader;
import com.canoo.dolphin.server.context.DolphinContext;
import com.canoo.dolphin.server.context.DolphinContextProvider;
import com.canoo.dolphin.server.context.DolphinContextUtils;
import com.canoo.dolphin.server.context.DolphinSessionProvider;
import com.canoo.dolphin.server.controller.ControllerRepository;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.impl.DefaultDolphinEventBus;
import com.canoo.dolphin.server.impl.ClasspathScanner;
import com.canoo.dolphin.server.spring.ClientScope;
import com.canoo.dolphin.test.ControllerTestException;
import com.canoo.common.Assert;
import com.canoo.remoting.client.ClientDolphin;
import com.canoo.remoting.client.communication.UiThreadHandler;
import com.canoo.remoting.server.ServerModelStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Configuration
public class DolphinPlatformSpringTestBootstrap {

    private static final Logger LOG = LoggerFactory.getLogger(DolphinPlatformSpringTestBootstrap.class);

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
        final ClientContext clientContext = new ClientContextImpl(clientConfiguration, clientDolphin, controllerProxyFactory, dolphinCommandHandler, platformBeanRepository, clientBeanManager, new ForwardableCallback());

        //Currently the event bus can not used in tests. See https://github.com/canoo/dolphin-platform/issues/196
        config.getClientExecutor().submit(new Callable<Void>() {
            @Override
            public Void call() {
                DolphinContextUtils.setContextForCurrentThread(dolphinContext);
                clientDolphin.startPushListening(PlatformConstants.POLL_EVENT_BUS_COMMAND_NAME, PlatformConstants.RELEASE_EVENT_BUS_COMMAND_NAME);
                return null;
            }

        }).get();
        return clientContext;
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

        DolphinTestContext dolphinContext = new DolphinTestContext(ConfigurationFileLoader.loadConfiguration(), dolphinContextProviderMock, containerManager, controllerRepository, config);
        dolphinContextProviderMock.setCurrentContext(dolphinContext);

        DolphinTestClientConnector inMemoryClientConnector = new DolphinTestClientConnector(config.getClientDolphin(), dolphinContext);

        inMemoryClientConnector.setStrictMode(false);
        inMemoryClientConnector.setUiThreadHandler(new UiThreadHandler() {

            @Override
            public void executeInsideUiThread(Runnable runnable) {
                config.getClientExecutor().execute(runnable);
            }
        });
        config.getClientDolphin().setClientConnector(inMemoryClientConnector);

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
        return context.getDolphinSession();
    }


    /**
     * Method to create a spring managed {@link DolphinEventBus} instance in singleton scope.
     *
     * @return the instance
     */
    @Bean(name = "dolphinEventBus")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected DolphinEventBus createEventBus(final DolphinTestContext context) {
        return new DefaultDolphinEventBus(new DolphinContextProvider() {
            @Override
            public DolphinContext getCurrentContext() {
                return context;
            }

            @Override
            public DolphinSession getCurrentDolphinSession() {
                return context.getDolphinSession();
            }
        }, DolphinPlatformBootstrap.getSessionLifecycleHandler());
    }

    @Bean(name = "backgroundRunner")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected BackgroundRunner createBackgroundRunner() {
        return new BackgroundRunner() {

            @Override
            public Future<Void> runLaterInClientSession(final String clientSessionId, final Runnable task) {
                return DolphinContextUtils.runLaterInClientSession(clientSessionId, task);
            }
        };
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
                return context.getDolphinSession();
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
            return getCurrentContext().getDolphinSession();
        }
    }

}
