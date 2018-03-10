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
package com.canoo.dp.impl.client;

import com.canoo.dp.impl.client.legacy.ClientModelStore;
import com.canoo.dp.impl.client.legacy.DefaultModelSynchronizer;
import com.canoo.dp.impl.client.legacy.ModelSynchronizer;
import com.canoo.dp.impl.client.legacy.communication.AbstractClientConnector;
import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.remoting.BeanManagerImpl;
import com.canoo.dp.impl.remoting.BeanRepository;
import com.canoo.dp.impl.remoting.BeanRepositoryImpl;
import com.canoo.dp.impl.remoting.ClassRepository;
import com.canoo.dp.impl.remoting.ClassRepositoryImpl;
import com.canoo.dp.impl.remoting.Converters;
import com.canoo.dp.impl.remoting.EventDispatcher;
import com.canoo.dp.impl.remoting.PresentationModelBuilderFactory;
import com.canoo.dp.impl.remoting.codec.OptimizedJsonCodec;
import com.canoo.dp.impl.remoting.collections.ListMapperImpl;
import com.canoo.dp.impl.remoting.commands.CreateContextCommand;
import com.canoo.dp.impl.remoting.commands.DestroyContextCommand;
import com.canoo.dp.impl.remoting.legacy.communication.Command;
import com.canoo.platform.client.ClientConfiguration;
import com.canoo.platform.client.session.ClientSessionStore;
import com.canoo.platform.core.functional.Subscription;
import com.canoo.platform.core.http.HttpClient;
import com.canoo.platform.remoting.BeanManager;
import com.canoo.platform.remoting.DolphinRemotingException;
import com.canoo.platform.remoting.client.ClientContext;
import com.canoo.platform.remoting.client.ClientInitializationException;
import com.canoo.platform.remoting.client.ControllerInitalizationException;
import com.canoo.platform.remoting.client.ControllerProxy;
import com.canoo.platform.remoting.client.RemotingExceptionHandler;
import org.apiguardian.api.API;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ClientContextImpl implements ClientContext {

    private final URI endpoint;

    private final ClientSessionStore clientSessionStore;

    private final AbstractClientConnector clientConnector;

    private final ClientModelStore modelStore;

    private final Executor backgroundExecutor;

    @Deprecated
    private  final BeanManager clientBeanManager;

    private final ControllerProxyFactory controllerProxyFactory;

    private final DolphinCommandHandler dolphinCommandHandler;

    private final List<RemotingExceptionHandler> remotingExceptionHandlers = new CopyOnWriteArrayList<>();

    public ClientContextImpl(final ClientConfiguration clientConfiguration, final URI endpoint, final HttpClient httpClient, final ClientSessionStore clientSessionStore) {
        Assert.requireNonNull(clientConfiguration, "clientConfiguration");
        this.clientSessionStore = Assert.requireNonNull(clientSessionStore, "clientSessionStore");
        this.endpoint = Assert.requireNonNull(endpoint, "endpoint");
        this.backgroundExecutor = clientConfiguration.getBackgroundExecutor();
        final ModelSynchronizer defaultModelSynchronizer = new DefaultModelSynchronizer(new Supplier<AbstractClientConnector>() {
            @Override
            public AbstractClientConnector get() {
                return clientConnector;
            }
        });

        this.modelStore = new ClientModelStore(defaultModelSynchronizer);

        final RemotingExceptionHandler connectorExceptionHandler = new RemotingExceptionHandler() {
            @Override
            public void handle(final DolphinRemotingException e) {
                remotingExceptionHandlers.forEach(h -> h.handle(e));
            }
        };

        this.clientConnector = createConnector(clientConfiguration, modelStore, httpClient, connectorExceptionHandler);
        Assert.requireNonNull(clientConnector, "clientConnector");

        final EventDispatcher dispatcher = new ClientEventDispatcher(modelStore);
        final BeanRepository beanRepository = new BeanRepositoryImpl(modelStore, dispatcher);
        final Converters converters = new Converters(beanRepository);
        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(modelStore);
        final ClassRepository classRepository = new ClassRepositoryImpl(modelStore, converters, builderFactory);

        this.dolphinCommandHandler = new DolphinCommandHandler(clientConnector);
        this.controllerProxyFactory = new ControllerProxyFactory(dolphinCommandHandler, clientConnector, modelStore, beanRepository, dispatcher, converters);
        this.clientBeanManager = new BeanManagerImpl(beanRepository, new ClientBeanBuilderImpl(classRepository, beanRepository, new ListMapperImpl(modelStore, classRepository, beanRepository, builderFactory, dispatcher), builderFactory, dispatcher));
    }

    protected AbstractClientConnector createConnector(final ClientConfiguration configuration, final ClientModelStore modelStore, final HttpClient httpClient, final RemotingExceptionHandler connectorExceptionHandler) {
        return new DolphinPlatformHttpClientConnector(endpoint, configuration, modelStore, OptimizedJsonCodec.getInstance(), connectorExceptionHandler, httpClient);
    }

    protected DolphinCommandHandler getDolphinCommandHandler() {
        return dolphinCommandHandler;
    }

    @Override
    public synchronized <T> CompletableFuture<ControllerProxy<T>> createController(final String name) {
        Assert.requireNonBlank(name, "name");

        if(controllerProxyFactory == null) {
            throw new IllegalStateException("connect was not called!");
        }

        return controllerProxyFactory.<T>create(name).handle((proxy, throwable) -> {
            if (throwable != null) {
                throw new ControllerInitalizationException(throwable);
            }
            return proxy;
        });
    }

    @Override
    public synchronized BeanManager getBeanManager() {
        return clientBeanManager;
    }

    @Override
    public synchronized CompletableFuture<Void> disconnect() {
        final CompletableFuture<Void> result = new CompletableFuture<>();
        backgroundExecutor.execute(() -> {
            try {
                final Command command = new DestroyContextCommand();
                dolphinCommandHandler.invokeDolphinCommand(command).handle((v, throwable) -> {
                    clientConnector.disconnect();
                    clientSessionStore.resetSession(endpoint);
                    if (throwable != null) {
                        final Exception e = new DolphinRemotingException("Can't disconnect", throwable);
                        result.completeExceptionally(e);
                    } else {
                        result.complete(v);
                    }
                    return v;
                }).get();
            } catch (Exception e) {
                if(!result.isCompletedExceptionally()) {
                    result.completeExceptionally(e);
                }
            }
        });
        return result;
    }

    @Override
    public CompletableFuture<Void> connect() {
        final CompletableFuture<Void> result = new CompletableFuture<>();
        clientConnector.connect();
        backgroundExecutor.execute(() -> {
            final Command command = new CreateContextCommand();
            dolphinCommandHandler.invokeDolphinCommand(command).handle((v, throwable) -> {
                if (throwable != null) {
                    final Exception e = new ClientInitializationException("Can't call init action!", throwable);
                    result.completeExceptionally(e);
                } else {
                    result.complete(v);
                }
                return v;
            }).thenAccept(v -> clientConnector.startLongPolling());
        });
        return result;
    }

    @Override
    public String getClientId() {
        return clientSessionStore.getClientIdentifierForUrl(endpoint);
    }

    @Override
    public Subscription addRemotingExceptionHandler(final RemotingExceptionHandler exceptionHandler) {
        Assert.requireNonNull(exceptionHandler, "exceptionHandler");
        remotingExceptionHandlers.add(exceptionHandler);
        return () -> remotingExceptionHandlers.remove(exceptionHandler);
    }

    public boolean isConnected() {
        return clientConnector.isConnected();
    }

    public Subscription addConnectionListener(final Consumer<Boolean> listener) {
        return clientConnector.addConnectionListener(listener);
    }
}
