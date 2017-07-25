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
package com.canoo.impl.server.context;

import com.canoo.dolphin.BeanManager;
import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.impl.server.binding.PropertyBinderImpl;
import com.canoo.platform.server.ClientSessionExecutor;
import com.canoo.platform.server.RemotingContext;
import com.canoo.platform.server.binding.PropertyBinder;
import com.canoo.platform.server.client.ClientSession;
import com.canoo.platform.server.event.DolphinEventBus;

import java.util.concurrent.Executor;

public class RemotingContextImpl implements RemotingContext {

    private final DolphinContext dolphinContext;

    private final DolphinEventBus eventBus;

    private final PropertyBinder propertyBinder = new PropertyBinderImpl();

    private final ClientSessionExecutor clientSessionExecutor;

    public RemotingContextImpl(final DolphinContext dolphinContext, DolphinEventBus eventBus) {
        this.dolphinContext = Assert.requireNonNull(dolphinContext, "dolphinContext");
        this.eventBus = Assert.requireNonNull(eventBus, "eventBus");
        clientSessionExecutor = new ClientSessionExecutorImpl(new Executor() {
            @Override
            public void execute(Runnable command) {
                dolphinContext.runLater(command);
            }
        });
    }

    @Override
    public String getId() {
        return dolphinContext.getId();
    }

    @Override
    public ClientSessionExecutor createSessionExecutor() {
        return clientSessionExecutor;
    }

    @Override
    public PropertyBinder getBinder() {
        return propertyBinder;
    }

    @Override
    public BeanManager getBeanManager() {
        return dolphinContext.getBeanManager();
    }

    @Override
    public DolphinEventBus getEventBus() {
        return eventBus;
    }

    @Override
    public ClientSession getClientSession() {
        return dolphinContext.getDolphinSession();
    }
}
