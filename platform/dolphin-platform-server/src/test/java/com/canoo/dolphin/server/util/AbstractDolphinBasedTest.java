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
package com.canoo.dolphin.server.util;

import com.canoo.dolphin.BeanManager;
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
import com.canoo.implementation.dolphin.server.ServerBeanBuilderImpl;
import com.canoo.implementation.dolphin.server.ServerEventDispatcher;
import com.canoo.implementation.dolphin.server.ServerPresentationModelBuilderFactory;
import com.canoo.implementation.dolphin.server.gc.GarbageCollectionCallback;
import com.canoo.implementation.dolphin.server.gc.GarbageCollector;
import com.canoo.implementation.dolphin.server.gc.Instance;
import org.opendolphin.core.comm.Command;
import core.comm.DefaultInMemoryConfig;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerModelStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class AbstractDolphinBasedTest {

    protected ServerDolphin createServerDolphin() {
        DefaultInMemoryConfig config = new DefaultInMemoryConfig();
        config.getServerDolphin().registerDefaultActions();

        ServerModelStore store = config.getServerDolphin().getModelStore();
        List<Command> commands = new ArrayList<>();
        store.setCurrentResponse(commands);

        return config.getServerDolphin();
    }

    protected BeanManager createBeanManager(ServerDolphin dolphin) {
        final EventDispatcher dispatcher = new ServerEventDispatcher(dolphin);
        final BeanRepositoryImpl beanRepository = new BeanRepositoryImpl(dolphin, dispatcher);
        final Converters converters = new Converters(beanRepository);
        final PresentationModelBuilderFactory builderFactory = new ServerPresentationModelBuilderFactory(dolphin);
        final ClassRepository classRepository = new ClassRepositoryImpl(dolphin, converters, builderFactory);
        final ListMapper listMapper = new ListMapperImpl(dolphin, classRepository, beanRepository, builderFactory, dispatcher);
        final GarbageCollector garbageCollector = new GarbageCollector(new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {

            }
        });
        final BeanBuilder beanBuilder = new ServerBeanBuilderImpl(classRepository, beanRepository, listMapper, builderFactory, dispatcher, garbageCollector);
        return new BeanManagerImpl(beanRepository, beanBuilder);
    }
}
