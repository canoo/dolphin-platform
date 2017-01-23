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

import com.canoo.dolphin.client.impl.ClientBeanBuilderImpl;
import com.canoo.dolphin.client.impl.ClientBeanManagerImpl;
import com.canoo.dolphin.client.impl.ClientContextImpl;
import com.canoo.dolphin.client.impl.ClientEventDispatcher;
import com.canoo.dolphin.client.impl.ClientPlatformBeanRepository;
import com.canoo.dolphin.client.impl.ClientPresentationModelBuilderFactory;
import com.canoo.dolphin.client.impl.ControllerProxyFactory;
import com.canoo.dolphin.client.impl.ControllerProxyFactoryImpl;
import com.canoo.dolphin.client.impl.DolphinCommandHandler;
import com.canoo.dolphin.client.impl.DolphinPlatformHttpClientConnector;
import com.canoo.dolphin.client.impl.ForwardableCallback;
import com.canoo.dolphin.impl.BeanRepositoryImpl;
import com.canoo.dolphin.impl.ClassRepositoryImpl;
import com.canoo.dolphin.impl.Converters;
import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.impl.PresentationModelBuilderFactory;
import com.canoo.dolphin.impl.codec.OptimizedJsonCodec;
import com.canoo.dolphin.impl.collections.ListMapperImpl;
import com.canoo.dolphin.internal.BeanBuilder;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.ClassRepository;
import com.canoo.dolphin.internal.EventDispatcher;
import com.canoo.dolphin.internal.collections.ListMapper;
import com.canoo.common.Assert;
import com.canoo.dolphin.util.DolphinRemotingException;
import com.canoo.remoting.client.ClientDolphin;
import com.canoo.remoting.client.ClientModelStore;
import com.canoo.remoting.client.communication.AbstractClientConnector;
import com.canoo.remoting.client.communication.ExceptionHandler;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory to create a {@link ClientContext}. Normally you will create a {@link ClientContext} at the bootstrap of your
 * client by using the {@link #connect(ClientConfiguration)} method and use this context as a singleton in your client.
 * The {@link ClientContext} defines the connection between the client and the Dolphin Platform server endpoint.
 */
public class ClientContextFactory {

    private ClientContextFactory() {
    }

    /**
     * Create a {@link ClientContext} based on the given configuration. This method doesn't block and returns a
     * {@link CompletableFuture} to receive its result. If the {@link ClientContext} can't be created the
     * {@link CompletableFuture#get()} will throw a {@link ClientInitializationException}.
     *
     * @param clientConfiguration the configuration
     * @return the future
     */
    public static CompletableFuture<ClientContext> connect(final ClientConfiguration clientConfiguration) {
        Assert.requireNonNull(clientConfiguration, "clientConfiguration");
        final CompletableFuture<ClientContext> result = new CompletableFuture<>();

        Level openDolphinLogLevel = clientConfiguration.getDolphinLogLevel();
        Logger openDolphinLogger = Logger.getLogger("org.opendolphin");
        openDolphinLogger.setLevel(openDolphinLogLevel);

        clientConfiguration.getBackgroundExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final ForwardableCallback<DolphinRemotingException> remotingErrorHandler = new ForwardableCallback<>();
                    final ClientDolphin clientDolphin = new ClientDolphin();
                    clientDolphin.setClientModelStore(new ClientModelStore(clientDolphin));
                    final AbstractClientConnector clientConnector = new DolphinPlatformHttpClientConnector(clientConfiguration, clientDolphin, new OptimizedJsonCodec(), remotingErrorHandler);
                    ExceptionHandler exceptionHandler = new ExceptionHandler() {

                        @Override
                        public void handle(Throwable e) {
                            result.completeExceptionally(new DolphinRemotingException("Internal Exception", e));
                        }
                    };

                    clientConnector.setOnException(exceptionHandler);
                    clientDolphin.setClientConnector(clientConnector);
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
                    final ClientContext clientContext = new ClientContextImpl(clientConfiguration, clientDolphin, controllerProxyFactory, dolphinCommandHandler, platformBeanRepository, clientBeanManager, remotingErrorHandler);
                    clientDolphin.startPushListening(PlatformConstants.POLL_EVENT_BUS_COMMAND_NAME, PlatformConstants.RELEASE_EVENT_BUS_COMMAND_NAME);
                    clientConfiguration.getUiThreadHandler().executeInsideUiThread(new Runnable() {
                        @Override
                        public void run() {
                            result.complete(clientContext);
                        }
                    });
                } catch (Exception e) {
                    result.obtrudeException(new ClientInitializationException("Can not connect to server!", e));
                }
            }
        });
        return result;
    }

}
