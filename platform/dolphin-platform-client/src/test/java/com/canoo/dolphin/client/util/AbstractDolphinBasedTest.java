/*
 * Copyright 2015-2016 Canoo Engineering AG.
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
import com.canoo.implementation.dolphin.client.ClientBeanBuilderImpl;
import com.canoo.implementation.dolphin.client.ClientEventDispatcher;
import com.canoo.implementation.dolphin.client.ClientPresentationModelBuilderFactory;
import com.canoo.implementation.dolphin.BeanManagerImpl;
import com.canoo.implementation.dolphin.BeanRepositoryImpl;
import com.canoo.implementation.dolphin.ClassRepositoryImpl;
import com.canoo.implementation.dolphin.Converters;
import com.canoo.implementation.dolphin.PresentationModelBuilderFactory;
import com.canoo.implementation.dolphin.collections.ListMapperImpl;
import com.canoo.implementation.dolphin.BeanBuilder;
import com.canoo.implementation.dolphin.ClassRepository;
import com.canoo.implementation.dolphin.EventDispatcher;
import com.canoo.implementation.dolphin.collections.ListMapper;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.comm.HttpClientConnector;
import core.comm.DefaultInMemoryConfig;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerModelStore;

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

    protected ClientDolphin createClientDolphin(HttpClientConnector connector) {
        final ClientDolphin dolphin = new ClientDolphin();
        dolphin.setClientModelStore(new ClientModelStore(dolphin));
        dolphin.setClientConnector(connector);
        return dolphin;
    }

    protected DolphinTestConfiguration createDolphinTestConfiguration() {
        DefaultInMemoryConfig config = new DefaultInMemoryConfig();
        config.getServerDolphin().registerDefaultActions();
        ServerModelStore store = config.getServerDolphin().getModelStore();
        store.setCurrentResponse(new ArrayList<>());

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
