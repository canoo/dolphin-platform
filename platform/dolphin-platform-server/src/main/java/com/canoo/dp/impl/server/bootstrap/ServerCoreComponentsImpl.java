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
package com.canoo.dp.impl.server.bootstrap;

import com.canoo.platform.core.PlatformThreadFactory;
import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.server.beans.ManagedBeanFactory;
import com.canoo.platform.server.spi.ClasspathScanner;
import com.canoo.platform.server.spi.PlatformConfiguration;
import com.canoo.platform.server.spi.ServerCoreComponents;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

public class ServerCoreComponentsImpl implements ServerCoreComponents {

    private final Map<Class<?>, Object> instances = new HashMap<>();

    protected ServerCoreComponentsImpl(final ServletContext servletContext, final PlatformConfiguration configuration, final PlatformThreadFactory threadFactory, ClasspathScanner classpathScanner, ManagedBeanFactory managedBeanFactory) {
        Assert.requireNonNull(servletContext, "servletContext");
        Assert.requireNonNull(configuration, "configuration");
        Assert.requireNonNull(threadFactory, "threadFactory");
        Assert.requireNonNull(classpathScanner, "classpathScanner");
        Assert.requireNonNull(managedBeanFactory, "managedBeanFactory");

        provideInstance(ServletContext.class, servletContext);
        provideInstance(PlatformConfiguration.class, configuration);
        provideInstance(PlatformThreadFactory.class, threadFactory);
        provideInstance(ClasspathScanner.class, classpathScanner);
        provideInstance(ManagedBeanFactory.class, managedBeanFactory);
    }

    public ServletContext getServletContext() {
        return getInstance(ServletContext.class);
    }

    public PlatformConfiguration getConfiguration() {
        return getInstance(PlatformConfiguration.class);
    }

    public PlatformThreadFactory getThreadFactory() {
        return getInstance(PlatformThreadFactory.class);
    }

    public ClasspathScanner getClasspathScanner() {
        return getInstance(ClasspathScanner.class);
    }

    public ManagedBeanFactory getManagedBeanFactory() {
        return getInstance(ManagedBeanFactory.class);
    }

    public <T> void provideInstance(Class<T> cls, T instance) {
        Assert.requireNonNull(cls, "cls");
        Assert.requireNonNull(instance, "instance");

        if(getInstance(cls) != null) {
            throw new IllegalStateException("Instance for class " + cls + " already provided");
        }
        instances.put(cls, instance);
    }

    public <T> T getInstance(Class<T> cls) {
        Assert.requireNonNull(cls, "cls");
        return (T) instances.get(cls);
    }
}
