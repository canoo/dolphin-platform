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
package com.canoo.platform.remoting.client;

import com.canoo.dp.impl.client.ClientContextFactoryImpl;
import com.canoo.platform.core.functional.Subscription;
import com.canoo.platform.remoting.BeanManager;
import org.apiguardian.api.API;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * The client context defines a connection to the Dolphin Platform endpoint on the server.
 * For each client instance there should be one {@link ClientContext} instance that can be
 * created by using the {@link ClientContextFactoryImpl}.
 * The client context is needed to create {@link ControllerProxy} instances.
 */
@API(since = "0.x", status = MAINTAINED)
public interface ClientContext extends ControllerFactory {

    /**
     * Returns the {@link BeanManager} that is bound to the client context
     * @return the bean manager
     */
    @Deprecated
    BeanManager getBeanManager();

    /**
     * Disconnects the client context. The method doesn't block. To verify that the connection has been closed
     * {@link CompletableFuture#get()} can be called.
     * @return a {@link CompletableFuture} that defines the disconnect task.
     */
    CompletableFuture<Void> disconnect();

    /**
     * Connects the client context. The method doesn't block. To verify that the connection has been
     * successful connected {@link CompletableFuture#get()} can be called.
     * @return a {@link CompletableFuture} that defines the connection task.
     */
    CompletableFuture<Void> connect();

    String getClientId();

    Subscription addRemotingExceptionHandler(RemotingExceptionHandler exceptionHandler);

    boolean isConnected();

    Subscription addConnectionListener(final Consumer<Boolean> listener);
}
