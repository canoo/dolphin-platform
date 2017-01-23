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

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.client.impl.ClientBeanBuilderImpl;
import com.canoo.dolphin.client.impl.ClientEventDispatcher;
import com.canoo.dolphin.client.impl.ClientPresentationModelBuilderFactory;
import com.canoo.dolphin.impl.BeanManagerImpl;
import com.canoo.dolphin.impl.BeanRepositoryImpl;
import com.canoo.dolphin.impl.ClassRepositoryImpl;
import com.canoo.dolphin.impl.Converters;
import com.canoo.dolphin.impl.PresentationModelBuilderFactory;
import com.canoo.dolphin.impl.collections.ListMapperImpl;
import com.canoo.dolphin.internal.BeanBuilder;
import com.canoo.dolphin.internal.ClassRepository;
import com.canoo.dolphin.internal.EventDispatcher;
import com.canoo.dolphin.internal.collections.ListMapper;
import com.canoo.remoting.combined.DefaultInMemoryConfig;
import com.canoo.remoting.client.ClientDolphin;
import com.canoo.remoting.client.ClientModelStore;
import com.canoo.remoting.client.communication.ClientConnector;
import com.canoo.communication.common.commands.Command;
import com.canoo.remoting.server.ServerDolphin;
import com.canoo.remoting.server.ServerModelStore;

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

    protected ClientDolphin createClientDolphin(ClientConnector connector) {
        final ClientDolphin dolphin = new ClientDolphin();
        dolphin.setClientModelStore(new ClientModelStore(dolphin));
        dolphin.setClientConnector(connector);
        return dolphin;
    }

    protected DolphinTestConfiguration createDolphinTestConfiguration() {
        DefaultInMemoryConfig config = new DefaultInMemoryConfig();
        config.getServerDolphin().registerDefaultActions();
        ServerModelStore store = config.getServerDolphin().getModelStore();
        store.setCurrentResponse(new ArrayList<Command>());

        return new DolphinTestConfiguration(config.getClientDolphin(), config.getServerDolphin());
    }

    protected BeanManager createBeanManager(ClientDolphin dolphin) {
        final EventDispatcher dispatcher = new ClientEventDispatcher(dolphin);
        final BeanRepositoryImpl beanRepository = new BeanRepositoryImpl(dolphin, dispatcher);
        final Converters converters = new Converters(beanRepository);
        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin);
        final ClassRepository classRepository = new ClassRepositoryImpl(dolphin, converters, builderFactory);
        final ListMapper listMapper = new ListMapperImpl(dolphin, classRepository, beanRepository, builderFactory, dispatcher);
        final BeanBuilder beanBuilder = new ClientBeanBuilderImpl(classRepository, beanRepository, listMapper, builderFactory, dispatcher);
        return new BeanManagerImpl(beanRepository, beanBuilder);
    }
}
