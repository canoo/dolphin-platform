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
package com.canoo.platform.remoting.client;

import com.canoo.dp.impl.client.ClientContextFactoryImpl;
import com.canoo.platform.remoting.BeanManager;
import org.apiguardian.api.API;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
     * Creates a {@link ControllerProxy} instance for the controller with the given name.
     * By doing so a new instance of the matching controller class will be created on the server.
     * The {@link ControllerProxy} can be used to communicate with the controller instance on the
     * server. The method don't block. To get the created {@link ControllerProxy} instance {@link CompletableFuture#get()}
     * must be called on the return value.
     * @param name the unique name of the controller type
     * @param <T> the type of the model that is bound to the controller and view
     * @return a {@link CompletableFuture} that defines the creation of the controller.
     */
    <T> CompletableFuture<ControllerProxy<T>> createController(String name);

    /**
     * Returns the {@link BeanManager} that is bound to the client context
     * @return the bean manager
     */
    @Deprecated
    BeanManager getBeanManager();

    /**
     * Disconnects the client context. The method don't block. To verify that the connection has been closed
     * {@link CompletableFuture#get()} must be called on the return value.
     * @return a {@link CompletableFuture} that defines the disconnect task.
     */
    CompletableFuture<Void> disconnect();

    CompletableFuture<Void> connect();

    String getClientId();

    Optional<String> clientId();
}
