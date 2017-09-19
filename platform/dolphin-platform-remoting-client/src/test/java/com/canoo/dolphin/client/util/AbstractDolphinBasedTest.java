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
package com.canoo.dolphin.client.util;

import com.canoo.dp.impl.client.ClientBeanBuilderImpl;
import com.canoo.dp.impl.client.ClientEventDispatcher;
import com.canoo.dp.impl.client.ClientPresentationModelBuilderFactory;
import com.canoo.dp.impl.client.legacy.ClientDolphin;
import com.canoo.dp.impl.client.legacy.ClientModelStore;
import com.canoo.dp.impl.client.legacy.ModelSynchronizer;
import com.canoo.dp.impl.client.legacy.communication.AbstractClientConnector;
import com.canoo.dp.impl.remoting.BeanBuilder;
import com.canoo.dp.impl.remoting.BeanManagerImpl;
import com.canoo.dp.impl.remoting.BeanRepository;
import com.canoo.dp.impl.remoting.BeanRepositoryImpl;
import com.canoo.dp.impl.remoting.ClassRepository;
import com.canoo.dp.impl.remoting.ClassRepositoryImpl;
import com.canoo.dp.impl.remoting.Converters;
import com.canoo.dp.impl.remoting.EventDispatcher;
import com.canoo.dp.impl.remoting.ListMapper;
import com.canoo.dp.impl.remoting.PresentationModelBuilderFactory;
import com.canoo.dp.impl.remoting.collections.ListMapperImpl;
import com.canoo.dp.impl.remoting.legacy.commands.Command;
import com.canoo.dp.impl.remoting.legacy.util.DirectExecutor;
import com.canoo.dp.impl.server.legacy.ServerDolphin;
import com.canoo.dp.impl.server.legacy.ServerModelStore;
import com.canoo.platform.core.functional.Provider;
import com.canoo.platform.remoting.BeanManager;

import java.util.ArrayList;

public abstract class AbstractDolphinBasedTest {

    public class DolphinTestConfiguration {

        private ClientDolphin clientDolphin;

        private ServerDolphin serverDolphin;

        public DolphinTestConfiguration(ClientDolphin clientDolphin, ServerDolphin serverDolphin) {
            this.clientDolphin = clientDolphin;
            this.serverDolphin = serverDolphin;
        }

        public ClientDolphin getClientDolphin() {
            return clientDolphin;
        }

        public ServerDolphin getServerDolphin() {
            return serverDolphin;
        }
    }

    protected ClientDolphin createClientDolphin(final AbstractClientConnector connector) {
        final ClientDolphin dolphin = new ClientDolphin();
        ModelSynchronizer defaultModelSynchronizer = new ModelSynchronizer(new Provider<AbstractClientConnector>() {
            @Override
            public AbstractClientConnector get() {
                return connector;
            }
        });
        ClientModelStore modelStore = new ClientModelStore(defaultModelSynchronizer);
        dolphin.setClientModelStore(modelStore);
        dolphin.setClientConnector(connector);
        return dolphin;
    }

    protected DolphinTestConfiguration createDolphinTestConfiguration() {
        DefaultInMemoryConfig config = new DefaultInMemoryConfig(DirectExecutor.getInstance());
        config.getServerDolphin().getServerConnector().registerDefaultActions();
        ServerModelStore store = config.getServerDolphin().getModelStore();
        store.setCurrentResponse(new ArrayList<Command>());

        return new DolphinTestConfiguration(config.getClientDolphin(), config.getServerDolphin());
    }

    protected EventDispatcher createEventDispatcher(final ClientDolphin dolphin) {
        final EventDispatcher dispatcher = new ClientEventDispatcher(dolphin.getModelStore());
        return dispatcher;
    }

    protected BeanRepository createBeanRepository(final ClientDolphin dolphin, final EventDispatcher dispatcher) {
        final BeanRepositoryImpl beanRepository = new BeanRepositoryImpl(dolphin.getModelStore(), dispatcher);
        return beanRepository;
    }

    protected BeanManager createBeanManager(final ClientDolphin dolphin, final BeanRepository beanRepository, final EventDispatcher dispatcher) {
        final Converters converters = new Converters(beanRepository);
        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin.getModelStore());
        final ClassRepository classRepository = new ClassRepositoryImpl(dolphin.getModelStore(), converters, builderFactory);
        final ListMapper listMapper = new ListMapperImpl(dolphin.getModelStore(), classRepository, beanRepository, builderFactory, dispatcher);
        final BeanBuilder beanBuilder = new ClientBeanBuilderImpl(classRepository, beanRepository, listMapper, builderFactory, dispatcher);
        return new BeanManagerImpl(beanRepository, beanBuilder);
    }

    protected BeanManager createBeanManager(ClientDolphin dolphin) {
        final EventDispatcher dispatcher = createEventDispatcher(dolphin);
        final BeanRepository repository = createBeanRepository(dolphin, dispatcher);
        return createBeanManager(dolphin, repository, dispatcher);
    }
}
