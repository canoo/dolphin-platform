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
package com.canoo.dp.impl.server.spring;

import com.canoo.dp.impl.server.bootstrap.PlatformBootstrap;
import com.canoo.dp.impl.server.client.ClientSessionProvider;
import com.canoo.platform.core.DolphinRuntimeException;
import com.canoo.platform.server.client.ClientSession;
import com.canoo.platform.server.spring.ClientScope;
import org.apiguardian.api.API;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * Provides all Dolphin Platform Beans and Scopes for CDI
 */
@Configuration
@API(since = "0.x", status = INTERNAL)
public class SpringBeanFactory {

    @Bean(name = "clientSession")
    @ClientScope
    protected ClientSession createClientSession() {
        final ClientSessionProvider provider = PlatformBootstrap.getServerCoreComponents().getInstance(ClientSessionProvider.class).orElseThrow(() -> new DolphinRuntimeException(ClientSessionProvider.class.getName() + " not found!"));
        return provider.getCurrentClientSession();
    }
    
    @Bean(name = "customScopeConfigurer")
    public static CustomScopeConfigurer createClientScope() {
        final CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        configurer.addScope(ClientScopeImpl.CLIENT_SCOPE, new ClientScopeImpl());
        return configurer;
    }
}
