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
package com.canoo.dolphin.test.impl;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.server.DolphinSession;
import com.canoo.dolphin.server.binding.PropertyBinder;
import com.canoo.dolphin.server.binding.impl.PropertyBinderImpl;
import com.canoo.dolphin.server.context.DolphinSessionProvider;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.impl.DefaultDolphinEventBus;
import com.canoo.dolphin.server.spring.ClientScope;
import com.canoo.dolphin.util.Assert;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

@Configuration
public class DolphinPlatformSpringTestBootstrap {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected TestConfiguration createTestConfiguration(final WebApplicationContext context) {
        Assert.requireNonNull(context, "context");
        try {
            return new TestConfiguration(context);
        } catch (Exception e) {
            throw new RuntimeException("Can not create test configuration", e);
        }
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected ClientContext createClientContext(final TestConfiguration testConfiguration) {
        Assert.requireNonNull(testConfiguration, "testConfiguration");
        return testConfiguration.getClientContext();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected DolphinTestContext createServerContext(final TestConfiguration testConfiguration) {
        Assert.requireNonNull(testConfiguration, "testConfiguration");
        return testConfiguration.getDolphinTestContext();
    }

    /**
     * Method to create a spring managed {@link com.canoo.dolphin.impl.BeanManagerImpl} instance in client scope.
     *
     * @return the instance
     */
    @Bean(name = "beanManager")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected BeanManager createManager(final TestConfiguration testConfiguration) {
        Assert.requireNonNull(testConfiguration, "testConfiguration");
        return testConfiguration.getDolphinTestContext().getBeanManager();
    }

    @Bean(name = "dolphinSession")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected DolphinSession createDolphinSession(final TestConfiguration testConfiguration) {
        Assert.requireNonNull(testConfiguration, "testConfiguration");
        return testConfiguration.getDolphinTestContext().getDolphinSession();
    }

    /**
     * Method to create a spring managed {@link DolphinEventBus} instance in singleton scope.
     *
     * @return the instance
     */
    @Bean(name = "dolphinEventBus")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected DolphinEventBus createEventBus(final DolphinSession dolphinSession) {
        Assert.requireNonNull(dolphinSession, "dolphinSession");
        return new DefaultDolphinEventBus(new DolphinSessionProvider() {
            @Override
            public DolphinSession getCurrentDolphinSession() {
                return dolphinSession;
            }
        });
    }

    @Bean(name = "propertyBinder")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected PropertyBinder createPropertyBinder() {
        return new PropertyBinderImpl();
    }

    @Bean(name = "customScopeConfigurer")
    public static CustomScopeConfigurer createClientScope(final DolphinSession dolphinSession) {
        Assert.requireNonNull(dolphinSession, "dolphinSession");
        CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        configurer.addScope(ClientScope.CLIENT_SCOPE, new ClientScope(new DolphinSessionProvider() {
            @Override
            public DolphinSession getCurrentDolphinSession() {
                return dolphinSession;
            }
        }));
        return configurer;
    }
}
