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
package com.canoo.implementation.dolphin.server.javaee;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.server.binding.PropertyBinder;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.javaee.ClientScoped;
import com.canoo.dolphin.server.session.DolphinSession;
import com.canoo.implementation.dolphin.server.binding.PropertyBinderImpl;
import com.canoo.implementation.dolphin.server.bootstrap.DolphinPlatformBootstrap;
import com.canoo.implementation.dolphin.server.event.DefaultDolphinEventBus;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

/**
 * Factory that provides all needed Dolphin Platform extensions as CDI beans.
 *
 * @author Hendrik Ebbers
 */
@Dependent
public class CdiBeanFactory {

    @Produces
    @ClientScoped
    public BeanManager createManager() {
        return DolphinPlatformBootstrap.getContextProvider().getCurrentContext().getBeanManager();
    }

    @Produces
    @ClientScoped
    public DolphinSession createDolphinSession() {
        return DolphinPlatformBootstrap.getContextProvider().getCurrentDolphinSession();
    }

    @Produces
    @ApplicationScoped
    public DolphinEventBus createEventBus() {
        return new DefaultDolphinEventBus(DolphinPlatformBootstrap.getContextProvider(), DolphinPlatformBootstrap.getSessionLifecycleHandler());
    }

    @Produces
    @ApplicationScoped
    public PropertyBinder createPropertyBinder() {
        return new PropertyBinderImpl();
    }
}
