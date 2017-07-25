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

import com.canoo.dp.impl.server.servlet.HttpSessionTimeoutListener;
import com.canoo.platform.server.spi.*;

import javax.servlet.ServletContext;

@ModuleDefinition(HttpTimeoutModule.HTTP_TIMEOUT_MODULE)
public class HttpTimeoutModule extends AbstractBaseModule {

    public static final String HTTP_TIMEOUT_MODULE = "HttpTimeoutModule";

    public static final String HTTP_TIMEOUT_ACTIVE = "httpTimeoutActive";

    @Override
    protected String getActivePropertyName() {
        return HTTP_TIMEOUT_ACTIVE;
    }

    @Override
    public void initialize(ServerCoreComponents coreComponents) throws ModuleInitializationException {
        final ServletContext servletContext = coreComponents.getServletContext();
        final PlatformConfiguration configuration = coreComponents.getConfiguration();

        HttpSessionTimeoutListener sessionCleaner = new HttpSessionTimeoutListener(configuration);
        servletContext.addListener(sessionCleaner);
    }
}
