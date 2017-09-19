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
package com.canoo.impl.server.util;

import com.canoo.platform.remoting.BeanManager;
import com.canoo.dp.impl.remoting.BeanManagerImpl;
import com.canoo.dp.impl.remoting.BeanRepositoryImpl;
import com.canoo.dp.impl.remoting.ClassRepositoryImpl;
import com.canoo.dp.impl.remoting.Converters;
import com.canoo.dp.impl.remoting.PresentationModelBuilderFactory;
import com.canoo.dp.impl.remoting.collections.ListMapperImpl;
import com.canoo.dp.impl.remoting.BeanBuilder;
import com.canoo.dp.impl.remoting.BeanRepository;
import com.canoo.dp.impl.remoting.ClassRepository;
import com.canoo.dp.impl.remoting.EventDispatcher;
import com.canoo.dp.impl.remoting.ListMapper;
import com.canoo.dp.impl.server.config.RemotingConfiguration;
import com.canoo.dp.impl.server.model.ServerBeanBuilderImpl;
import com.canoo.dp.impl.server.model.ServerEventDispatcher;
import com.canoo.dp.impl.server.model.ServerPresentationModelBuilderFactory;
import com.canoo.dp.impl.server.gc.GarbageCollectionCallback;
import com.canoo.dp.impl.server.gc.GarbageCollector;
import com.canoo.dp.impl.server.gc.Instance;
import com.canoo.dp.impl.remoting.legacy.commands.Command;
import com.canoo.dp.impl.server.legacy.ServerDolphin;
import com.canoo.dp.impl.server.legacy.ServerModelStore;
import com.canoo.dp.impl.remoting.legacy.util.DirectExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class AbstractDolphinBasedTest {

    protected ServerDolphin createServerDolphin() {
        DefaultInMemoryConfig config = new DefaultInMemoryConfig(DirectExecutor.getInstance());
        config.getServerDolphin().getServerConnector().registerDefaultActions();

        ServerModelStore store = config.getServerDolphin().getModelStore();
        List<Command> commands = new ArrayList<>();
        store.setCurrentResponse(commands);

        return config.getServerDolphin();
    }

    protected EventDispatcher createEventDispatcher(ServerDolphin dolphin) {
        return new ServerEventDispatcher(dolphin);
    }

    protected BeanRepository createBeanRepository(ServerDolphin dolphin, EventDispatcher dispatcher) {
        return new BeanRepositoryImpl(dolphin.getModelStore(), dispatcher);
    }

    protected BeanManager createBeanManager(ServerDolphin dolphin, BeanRepository beanRepository, EventDispatcher dispatcher) {
        final Converters converters = new Converters(beanRepository);
        final PresentationModelBuilderFactory builderFactory = new ServerPresentationModelBuilderFactory(dolphin);
        final ClassRepository classRepository = new ClassRepositoryImpl(dolphin.getModelStore(), converters, builderFactory);
        final ListMapper listMapper = new ListMapperImpl(dolphin.getModelStore(), classRepository, beanRepository, builderFactory, dispatcher);
        final RemotingConfiguration configurationForGc = new RemotingConfiguration();
        final GarbageCollector garbageCollector = new GarbageCollector(configurationForGc, new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {

            }
        });
        final BeanBuilder beanBuilder = new ServerBeanBuilderImpl(classRepository, beanRepository, listMapper, builderFactory, dispatcher, garbageCollector);
        return new BeanManagerImpl(beanRepository, beanBuilder);
    }


    protected BeanManager createBeanManager(ServerDolphin dolphin) {
        final EventDispatcher dispatcher = new ServerEventDispatcher(dolphin);
        final BeanRepositoryImpl beanRepository = new BeanRepositoryImpl(dolphin.getModelStore(), dispatcher);
        final Converters converters = new Converters(beanRepository);
        final PresentationModelBuilderFactory builderFactory = new ServerPresentationModelBuilderFactory(dolphin);
        final ClassRepository classRepository = new ClassRepositoryImpl(dolphin.getModelStore(), converters, builderFactory);
        final ListMapper listMapper = new ListMapperImpl(dolphin.getModelStore(), classRepository, beanRepository, builderFactory, dispatcher);
        final RemotingConfiguration configurationForGc = new RemotingConfiguration();
        final GarbageCollector garbageCollector = new GarbageCollector(configurationForGc, new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {

            }
        });
        final BeanBuilder beanBuilder = new ServerBeanBuilderImpl(classRepository, beanRepository, listMapper, builderFactory, dispatcher, garbageCollector);
        return new BeanManagerImpl(beanRepository, beanBuilder);
    }
}
