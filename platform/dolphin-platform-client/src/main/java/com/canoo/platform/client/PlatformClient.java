/*
 * Copyright 2015-2018 Canoo Engineering AG.
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
package com.canoo.platform.client;

import com.canoo.dp.impl.platform.client.config.ConfigurationFileLoader;
import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.platform.core.ansi.PlatformLogo;
import com.canoo.dp.impl.platform.core.context.ContextManagerImpl;
import com.canoo.platform.client.spi.ServiceProvider;
import com.canoo.platform.core.DolphinRuntimeException;
import org.apiguardian.api.API;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.canoo.dp.impl.platform.client.ClientConstants.UI_CONTEXT;
import static com.canoo.dp.impl.platform.core.PlatformConstants.APPLICATION_CONTEXT;
import static com.canoo.dp.impl.platform.core.PlatformConstants.APPLICATION_NAME_DEFAULT;
import static com.canoo.dp.impl.platform.core.PlatformConstants.APPLICATION_NAME_PROPERTY;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(since = "0.19.0", status = EXPERIMENTAL)
public class PlatformClient {

    private static final String DEFAULT_LOCATION = "application.properties";

    private static PlatformClient INSTANCE;

    private final Map<Class<?>, ServiceProvider> providers = new ConcurrentHashMap<>();

    private final Map<Class<?>, Object> services = new ConcurrentHashMap<>();

    private final ClientConfiguration clientConfiguration;

    private AtomicBoolean isToolkitSet =  new AtomicBoolean(false);

    private PlatformClient() {
        this.clientConfiguration = ConfigurationFileLoader.loadConfiguration(DEFAULT_LOCATION);
        Assert.requireNonNull(clientConfiguration, "clientConfiguration");
        PlatformLogo.printLogo(clientConfiguration);


        ContextManagerImpl.getInstance().addGlobalContext(APPLICATION_CONTEXT, clientConfiguration.getProperty(APPLICATION_NAME_PROPERTY, APPLICATION_NAME_DEFAULT));

        final ServiceLoader<ServiceProvider> loader = ServiceLoader.load(ServiceProvider.class);
        final Iterator<ServiceProvider> iterator = loader.iterator();
        while (iterator.hasNext()) {
            ServiceProvider provider = iterator.next();
            if(provider.isActive(clientConfiguration)) {
                Class serviceClass = provider.getServiceType();
                Assert.requireNonNull(serviceClass, "serviceClass");
                if (providers.containsKey(serviceClass)) {
                    throw new DolphinRuntimeException("Can not register more than 1 implementation for service type " + serviceClass);
                }
                providers.put(serviceClass, provider);
            }
        }
        initImpl(new HeadlessToolkit());
    }

    private void initImpl(Toolkit toolkit) {
        services.clear();
        Assert.requireNonNull(toolkit, "toolkit");
        clientConfiguration.setUiExecutor(toolkit.getUiExecutor());
        isToolkitSet.set(true);
        ContextManagerImpl.getInstance().addGlobalContext(UI_CONTEXT, toolkit.getName());
    }

    private Set<Class<?>> implGetAllServiceTypes() {
        return Collections.unmodifiableSet(providers.keySet());
    }

    private <S> boolean hasServiceImpl(final Class<S> serviceClass) {
        Assert.requireNonNull(serviceClass, "serviceClass");
        if(!isToolkitSet.get()){
            throw new DolphinRuntimeException("Toolkit is not set!");
        }
        return providers.containsKey(serviceClass);
    }

    private synchronized <S> S getServiceImpl(final Class<S> serviceClass) {
        Assert.requireNonNull(serviceClass, "serviceClass");
        if(!isToolkitSet.get()){
            throw new DolphinRuntimeException("Toolkit is not set!");
        }
        if(services.containsKey(serviceClass)) {
            final S service = (S) services.get(serviceClass);
            return service;
        }
        final ServiceProvider<S> serviceProvider = providers.get(serviceClass);
        Assert.requireNonNull(serviceProvider, "serviceProvider");
        final S service = serviceProvider.getService(clientConfiguration);
        Assert.requireNonNull(service, "service");
        services.put(serviceClass, service);
        return service;
    }

    public static void init(Toolkit toolkit) {
        getInstance().initImpl(toolkit);
    }

    public static ClientConfiguration getClientConfiguration() {
        return getInstance().clientConfiguration;
    }

    public static <S> boolean hasService(final Class<S> serviceClass) {
        return getInstance().hasServiceImpl(serviceClass);
    }

    public static <S> S getService(final Class<S> serviceClass) {
        return getInstance().getServiceImpl(serviceClass);
    }

    public static Set<Class<?>> getAllServiceTypes() {
        return getInstance().implGetAllServiceTypes();
    }

    private static synchronized PlatformClient getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new PlatformClient();
        }
        return INSTANCE;
    }
}
