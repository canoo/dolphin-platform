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
package com.canoo.implementation.dolphin.test.spring;

import com.canoo.implementation.dolphin.server.config.DolphinPlatformConfiguration;
import com.canoo.implementation.dolphin.server.container.ContainerManager;
import com.canoo.implementation.dolphin.server.context.DolphinContext;
import com.canoo.implementation.dolphin.server.context.DolphinContextUtils;
import com.canoo.implementation.dolphin.server.context.DolphinSessionProvider;
import com.canoo.implementation.dolphin.server.context.OpenDolphinFactory;
import com.canoo.implementation.dolphin.server.controller.ControllerRepository;
import com.canoo.implementation.dolphin.util.Assert;
import com.canoo.implementation.dolphin.util.Callback;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.server.DefaultServerDolphin;

import java.util.List;

public class DolphinTestContext extends DolphinContext {

    private final TestInMemoryConfiguration config;

    public DolphinTestContext(DolphinPlatformConfiguration configuration, DolphinSessionProvider dolphinSessionProvider, ContainerManager containerManager, ControllerRepository controllerRepository, TestInMemoryConfiguration config) {
        super(configuration, dolphinSessionProvider, containerManager, controllerRepository, createServerDolphinFactory(config), createEmptyCallback(), createEmptyCallback());
        this.config = Assert.requireNonNull(config, "config");
        DolphinContextUtils.setContextForCurrentThread(this);
    }

    private static Callback<DolphinContext> createEmptyCallback() {
        return new Callback<DolphinContext>() {
            @Override
            public void call(DolphinContext context) {

            }
        };
    }

    private static OpenDolphinFactory createServerDolphinFactory(final TestInMemoryConfiguration config) {
        Assert.requireNonNull(config, "config");
        return new OpenDolphinFactory(){

            @Override
            public DefaultServerDolphin create() {
                DefaultServerDolphin defaultServerDolphin =  config.getServerDolphin();
                return defaultServerDolphin;
            }
        };
    }

    @Override
    public List<Command> handle(List<Command> commands) {
        DolphinContextUtils.setContextForCurrentThread(this);
        return super.handle(commands);
    }

    public ClientDolphin getClientDolphin() {
        return config.getClientDolphin();
    }
}
