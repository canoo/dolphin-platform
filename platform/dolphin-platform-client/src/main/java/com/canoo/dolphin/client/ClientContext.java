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
package com.canoo.dolphin.client;

import com.canoo.dolphin.event.Subscription;
import com.canoo.common.Callback;
import com.canoo.dolphin.util.DolphinRemotingException;

import java.util.concurrent.CompletableFuture;

/**
 * The client context defines a connection to the Dolphin Platform endpoint on the server.
 * For each client instance there should be one {@link ClientContext} instance that can be
 * created by using the {@link ClientContextFactory}.
 * The client context is needed to create {@link ControllerProxy} instances.
 */
public interface ClientContext {

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
     * Returns the {@link ClientBeanManager} that is bound to the client context
     * @return the bean manager
     */
    @Deprecated
    ClientBeanManager getBeanManager();

    /**
     * Disconnects the client context. The method don't block. To verify that the connection has been closed
     * {@link CompletableFuture#get()} must be called on the return value.
     * @return a {@link CompletableFuture} that defines the disconnect task.
     */
    CompletableFuture<Void> disconnect();

    /**
     * This methods adds an error handler for the remoting layer of the client. Based on the cause of the
     * receiving {@link DolphinRemotingException} you can check what error happened.
     * @param callback the error handler
     * @return a {@link Subscription} that can be used to removePresentationModel the added error handler
     */
    Subscription onRemotingError(Callback<DolphinRemotingException> callback);

}
