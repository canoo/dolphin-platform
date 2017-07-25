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
package com.canoo.dp.impl.client;

import com.canoo.platform.client.ControllerProxy;
import com.canoo.dolphin.impl.Converters;
import com.canoo.dolphin.impl.InternalAttributesBean;
import com.canoo.dolphin.impl.commands.CreateControllerCommand;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.EventDispatcher;
import com.canoo.dp.impl.platform.core.Assert;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.comm.AbstractClientConnector;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ControllerProxyFactoryImpl implements ControllerProxyFactory {

    private final ClientPlatformBeanRepository platformBeanRepository;

    private final DolphinCommandHandler dolphinCommandHandler;

    private final AbstractClientConnector clientConnector;

    private final Converters converters;

    public ControllerProxyFactoryImpl(final DolphinCommandHandler dolphinCommandHandler, final AbstractClientConnector clientConnector, final ClientModelStore modelStore, final BeanRepository beanRepository, final EventDispatcher dispatcher, final Converters converters) {
        this.converters = Assert.requireNonNull(converters, "converters");
        this.platformBeanRepository = new ClientPlatformBeanRepository(modelStore, beanRepository, dispatcher, converters);
        this.dolphinCommandHandler = Assert.requireNonNull(dolphinCommandHandler, "dolphinCommandHandler");
        this.clientConnector = Assert.requireNonNull(clientConnector, "clientConnector");

    }

    @Override
    public <T> CompletableFuture<ControllerProxy<T>> create(String name) {
       return create(name, null);
    }

    @Override
    public <T> CompletableFuture<ControllerProxy<T>> create(String name, String parentControllerId) {
        Assert.requireNonBlank(name, "name");
        final InternalAttributesBean bean = platformBeanRepository.getInternalAttributesBean();

        final CreateControllerCommand createControllerCommand = new CreateControllerCommand();
        createControllerCommand.setControllerName(name);
        if(parentControllerId != null) {
            createControllerCommand.setParentControllerId(parentControllerId);
        }

        return dolphinCommandHandler.invokeDolphinCommand(createControllerCommand).thenApply(new Function<Void, ControllerProxy<T>>() {
            @Override
            public ControllerProxy<T> apply(Void aVoid) {
                return new ControllerProxyImpl<T>(bean.getControllerId(), (T) bean.getModel(), clientConnector, platformBeanRepository, ControllerProxyFactoryImpl.this, converters);
            }
        });
    }
}
