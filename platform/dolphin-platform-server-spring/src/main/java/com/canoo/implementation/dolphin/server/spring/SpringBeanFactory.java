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
package com.canoo.implementation.dolphin.server.spring;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.server.session.DolphinSession;
import com.canoo.dolphin.server.binding.PropertyBinder;
import com.canoo.implementation.dolphin.BeanManagerImpl;
import com.canoo.implementation.dolphin.server.binding.PropertyBinderImpl;
import com.canoo.implementation.dolphin.server.bootstrap.DolphinPlatformBootstrap;
import com.canoo.implementation.dolphin.server.context.DolphinSessionProvider;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.implementation.dolphin.server.event.DefaultDolphinEventBus;
import com.canoo.dolphin.server.spring.ClientScoped;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Provides all Dolphin Platform Beans and Scopes for CDI
 */
@Configuration
public class SpringBeanFactory {

    /**
     * Method to create a spring managed {@link BeanManagerImpl} instance in client scope.
     * @return the instance
     */
    @Bean(name="beanManager")
    @ClientScoped
    protected BeanManager createManager() {
        return DolphinPlatformBootstrap.getContextProvider().getCurrentContext().getBeanManager();
    }

    @Bean(name="dolphinSession")
    @ClientScoped
    protected DolphinSession createDolphinSession() {
        return DolphinPlatformBootstrap.getContextProvider().getCurrentDolphinSession();
    }

    /**
     * Method to create a spring managed {@link DolphinEventBus} instance in singleton scope.
     * @return the instance
     */
    @Bean(name="dolphinEventBus")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected DolphinEventBus createEventBus() {
        return new DefaultDolphinEventBus(DolphinPlatformBootstrap.getContextProvider(), DolphinPlatformBootstrap.getSessionLifecycleHandler());
    }

    @Bean(name="propertyBinder")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected PropertyBinder createPropertyBinder() {
        return new PropertyBinderImpl();
    }

    @Bean(name="customScopeConfigurer")
    public static CustomScopeConfigurer createClientScope() {
        CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        configurer.addScope(ClientScope.CLIENT_SCOPE, new ClientScope(new DolphinSessionProvider() {
            @Override
            public DolphinSession getCurrentDolphinSession() {
                return DolphinPlatformBootstrap.getContextProvider().getCurrentDolphinSession();
            }
        }));
        return configurer;
    }
}
