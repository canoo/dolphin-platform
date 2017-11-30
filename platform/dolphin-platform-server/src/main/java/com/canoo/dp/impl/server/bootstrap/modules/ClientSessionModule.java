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
package com.canoo.dp.impl.server.bootstrap.modules;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.server.client.ClientSessionFilter;
import com.canoo.dp.impl.server.client.ClientSessionLifecycleHandler;
import com.canoo.dp.impl.server.client.ClientSessionLifecycleHandlerImpl;
import com.canoo.dp.impl.server.client.ClientSessionManager;
import com.canoo.dp.impl.server.client.ClientSessionMutextHolder;
import com.canoo.dp.impl.server.client.ClientSessionProvider;
import com.canoo.dp.impl.server.client.HttpSessionCleanerListener;
import com.canoo.dp.impl.server.config.DefaultModuleConfig;
import com.canoo.platform.core.DolphinRuntimeException;
import com.canoo.platform.core.PlatformConfiguration;
import com.canoo.platform.server.ServerListener;
import com.canoo.platform.server.client.ClientSession;
import com.canoo.platform.server.client.ClientSessionListener;
import com.canoo.platform.server.spi.AbstractBaseModule;
import com.canoo.platform.server.spi.ModuleDefinition;
import com.canoo.platform.server.spi.ModuleInitializationException;
import com.canoo.platform.server.spi.ServerCoreComponents;
import com.canoo.platform.server.spi.components.ClasspathScanner;
import com.canoo.platform.server.spi.components.ManagedBeanFactory;
import org.apiguardian.api.API;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
@ModuleDefinition(ClientSessionModule.CLIENT_SESSION_MODULE)
public class ClientSessionModule extends AbstractBaseModule {

    public static final String CLIENT_SESSION_MODULE = "ClientSessionModule";

    public static final String CLIENT_SESSION_MODULE_ACTIVE = "clientSessionActive";

    public static final String DOLPHIN_CLIENT_ID_FILTER_NAME = "clientIdFilter";

    @Override
    protected String getActivePropertyName() {
        return CLIENT_SESSION_MODULE_ACTIVE;
    }

    @Override
    public void initialize(final ServerCoreComponents coreComponents) throws ModuleInitializationException {
        Assert.requireNonNull(coreComponents, "coreComponents");

        final ServletContext servletContext = coreComponents.getInstance(ServletContext.class).
                orElseThrow(() -> new DolphinRuntimeException("Can not start " + ClientSessionModule.class.getName()));
        final PlatformConfiguration configuration = coreComponents.getConfiguration();
        final ClasspathScanner classpathScanner = coreComponents.getInstance(ClasspathScanner.class).
                orElseThrow(() -> new DolphinRuntimeException("Can not start " + ClientSessionModule.class.getName()));
        final ManagedBeanFactory beanFactory = coreComponents.getInstance(ManagedBeanFactory.class).
                orElseThrow(() -> new DolphinRuntimeException("Can not start " + ClientSessionModule.class.getName()));

        final ClientSessionLifecycleHandlerImpl lifecycleHandler = new ClientSessionLifecycleHandlerImpl();
        coreComponents.provideInstance(ClientSessionLifecycleHandler.class, lifecycleHandler);
        coreComponents.provideInstance(ClientSessionProvider.class, new ClientSessionProvider() {
            @Override
            public ClientSession getCurrentClientSession() {
                return lifecycleHandler.getCurrentDolphinSession();
            }
        });


        final ClientSessionManager clientSessionManager = new ClientSessionManager(configuration, lifecycleHandler);

        final List<String> endpointList = DefaultModuleConfig.getIdFilterUrlMappings(configuration);
        final String[] endpoints = endpointList.toArray(new String[endpointList.size()]);
        final ClientSessionFilter filter = new ClientSessionFilter(clientSessionManager);
        final FilterRegistration.Dynamic createdFilter = servletContext.addFilter(DOLPHIN_CLIENT_ID_FILTER_NAME, filter);
        createdFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, endpoints);

        final HttpSessionCleanerListener sessionCleaner = new HttpSessionCleanerListener(clientSessionManager);
        servletContext.addListener(sessionCleaner);

        final Set<Class<?>> listeners = classpathScanner.getTypesAnnotatedWith(ServerListener.class);
        for (final Class<?> listenerClass : listeners) {
            if (ClientSessionListener.class.isAssignableFrom(listenerClass)) {
                final ClientSessionListener listener = (ClientSessionListener) beanFactory.createDependentInstance(listenerClass);

                lifecycleHandler.addSessionDestroyedListener(new Consumer<ClientSession>() {
                    @Override
                    public void accept(ClientSession clientSession) {
                        listener.sessionDestroyed(clientSession);
                    }
                });
                lifecycleHandler.addSessionCreatedListener(new Consumer<ClientSession>() {
                    @Override
                    public void accept(ClientSession clientSession) {
                        listener.sessionCreated(clientSession);
                    }
                });
            }
        }

        final ClientSessionMutextHolder mutextHolder = new ClientSessionMutextHolder();
        lifecycleHandler.addSessionDestroyedListener(new Consumer<ClientSession>() {
            @Override
            public void accept(ClientSession clientSession) {
                mutextHolder.sessionDestroyed(clientSession);
            }
        });
        lifecycleHandler.addSessionCreatedListener(new Consumer<ClientSession>() {
            @Override
            public void accept(ClientSession clientSession) {
                mutextHolder.sessionCreated(clientSession);
            }
        });

    }
}
