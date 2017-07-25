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
package com.canoo.dp.impl.server.bootstrap.modules;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.server.servlet.HttpSessionMutexHolder;
import com.canoo.platform.server.spi.AbstractBaseModule;
import com.canoo.platform.server.spi.ModuleDefinition;
import com.canoo.platform.server.spi.ModuleInitializationException;
import com.canoo.platform.server.spi.ServerCoreComponents;

import javax.servlet.ServletContext;

@ModuleDefinition(HttpMutexModule.HTTP_MUTEX_MODULE)
public class HttpMutexModule extends AbstractBaseModule {

    public static final String HTTP_MUTEX_MODULE = "HttpMutexModule";

    public static final String HTTP_MUTEX_MODULE_ACTIVE = "httpMutexModuleActive";

    @Override
    protected String getActivePropertyName() {
        return HTTP_MUTEX_MODULE_ACTIVE;
    }

    @Override
    public void initialize(final ServerCoreComponents coreComponents) throws ModuleInitializationException {
        Assert.requireNonNull(coreComponents, "coreComponents");
        final ServletContext servletContext = coreComponents.getServletContext();
        Assert.requireNonNull(servletContext, "servletContext");

        final HttpSessionMutexHolder mutexHolder = new HttpSessionMutexHolder();
        servletContext.addListener(mutexHolder);
    }
}
