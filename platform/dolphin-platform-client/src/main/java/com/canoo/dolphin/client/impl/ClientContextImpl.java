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
package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.client.ClientBeanManager;
import com.canoo.dolphin.client.ClientConfiguration;
import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.ClientInitializationException;
import com.canoo.dolphin.client.ControllerInitalizationException;
import com.canoo.dolphin.client.ControllerProxy;
import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.common.Assert;
import com.canoo.common.Callback;
import com.canoo.dolphin.util.DolphinRemotingException;
import com.canoo.remoting.client.ClientDolphin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

public class ClientContextImpl implements ClientContext {

    private final ClientDolphin clientDolphin;

    private final ClientBeanManagerImpl clientBeanManager;

    private final ClientPlatformBeanRepository platformBeanRepository;

    private State state = State.CREATED;

    private final ControllerProxyFactory controllerProxyFactory;

    private final DolphinCommandHandler dolphinCommandHandler;

    private final ClientConfiguration clientConfiguration;

    private ForwardableCallback<DolphinRemotingException> remotingErrorHandler;

    public ClientContextImpl(ClientConfiguration clientConfiguration, ClientDolphin clientDolphin, ControllerProxyFactory controllerProxyFactory, DolphinCommandHandler dolphinCommandHandler, ClientPlatformBeanRepository platformBeanRepository, ClientBeanManagerImpl clientBeanManager, ForwardableCallback<DolphinRemotingException> remotingErrorHandler) throws ExecutionException, InterruptedException {
        this.clientDolphin = Assert.requireNonNull(clientDolphin, "clientDolphin");
        this.controllerProxyFactory = Assert.requireNonNull(controllerProxyFactory, "controllerProxyFactory");
        this.dolphinCommandHandler = Assert.requireNonNull(dolphinCommandHandler, "dolphinCommandHandler");
        this.platformBeanRepository = Assert.requireNonNull(platformBeanRepository, "platformBeanRepository");
        this.clientBeanManager = Assert.requireNonNull(clientBeanManager, "clientBeanManager");
        this.remotingErrorHandler = Assert.requireNonNull(remotingErrorHandler, "remotingErrorHandler");
        this.clientConfiguration = Assert.requireNonNull(clientConfiguration, "clientConfiguration");
        try {
            dolphinCommandHandler.invokeDolphinCommand(PlatformConstants.INIT_CONTEXT_COMMAND_NAME).handle(new BiFunction<Void, Throwable, Object>() {
                @Override
                public Object apply(Void aVoid, Throwable throwable) {
                    if (throwable != null) {
                        state = State.DESTROYED;
                        throw new ClientInitializationException("Can't call init action!", throwable);
                    } else {
                        state = State.INITIALIZED;
                    }
                    return null;
                }
            }).get(clientConfiguration.getConnectionTimeout(), TimeUnit.MILLISECONDS);

            //Set the ID
            //TODO: Workaround with Cast. Should be refactored in Remoting layer



        } catch (Exception e) {
            throw new ClientInitializationException("Can not connect to server!", e);
        }
    }

    @Override
    public synchronized <T> CompletableFuture<ControllerProxy<T>> createController(String name) {
        Assert.requireNonBlank(name, "name");
        checkForInitializedState();

        return controllerProxyFactory.<T>create(name).handle(new BiFunction<ControllerProxy<T>, Throwable, ControllerProxy<T>>() {
            @Override
            public ControllerProxy<T> apply(ControllerProxy<T> controllerProxy, Throwable throwable) {
                if (throwable != null) {
                    throw new ControllerInitalizationException(throwable);
                }
                return controllerProxy;
            }
        });
    }

    @Override
    public synchronized ClientBeanManager getBeanManager() {
        checkForInitializedState();
        return clientBeanManager;
    }

    @Override
    public synchronized CompletableFuture<Void> disconnect() {
        checkForInitializedState();
        state = State.DESTROYING;
        clientDolphin.stopPushListening();
        final CompletableFuture<Void> result = new CompletableFuture<>();

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                state = State.DESTROYED;
                dolphinCommandHandler.invokeDolphinCommand(PlatformConstants.DESTROY_CONTEXT_COMMAND_NAME).handle(new BiFunction<Void, Throwable, Object>() {
                    @Override
                    public Object apply(Void aVoid, Throwable throwable) {
                        if (throwable != null) {
                            result.completeExceptionally(new DolphinRemotingException("Can't disconnect", throwable));
                        } else {
                            result.complete(null);
                        }
                        return null;
                    }
                });
                //TODO: Stop communication in client connector
            }
        });

        return result;
    }

    @Override
    public Subscription onRemotingError(final Callback<DolphinRemotingException> callback) {
        Assert.requireNonNull(callback, "callback");
        return remotingErrorHandler.register(new Callback<DolphinRemotingException>() {
            @Override
            public void call(final DolphinRemotingException e) {
                clientConfiguration.getUiThreadHandler().executeInsideUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.call(e);
                    }
                });
            }
        });
    }

    private void checkForInitializedState() {
        switch (state) {
            case CREATED:
                throw new IllegalStateException("The client is initialized!");
            case DESTROYED:
                throw new IllegalStateException("The client is disconnected!");
            case DESTROYING:
                throw new IllegalStateException("The client is disconnecting!");
        }
    }
}
