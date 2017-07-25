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

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.server.beans.ManagedBeanFactory;
import com.canoo.dp.impl.server.beans.PostConstructInterceptor;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

public abstract class AbstractSpringManagedBeanFactory implements ManagedBeanFactory {

    protected void init() {
        ApplicationContext context = getContext();
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
        beanFactory.addBeanPostProcessor(SpringModelInjector.getInstance());
    }

    @Override
    public <T> T createDependendInstance(Class<T> cls) {
        Assert.requireNonNull(cls, "cls");
        ApplicationContext context = getContext();
        AutowireCapableBeanFactory beanFactory = context.getAutowireCapableBeanFactory();
        return beanFactory.createBean(cls);
    }

    @Override
    public <T> T createDependendInstance(Class<T> cls, PostConstructInterceptor<T> interceptor) {
        Assert.requireNonNull(cls, "cls");
        Assert.requireNonNull(interceptor, "interceptor");
        ApplicationContext context = getContext();
        AutowireCapableBeanFactory beanFactory = context.getAutowireCapableBeanFactory();
        SpringModelInjector.getInstance().prepare(cls, interceptor);
        return beanFactory.createBean(cls);
    }

    @Override
    public <T> void destroyDependendInstance(T instance, Class<T> cls) {
        Assert.requireNonNull(instance, "instance");
        Assert.requireNonNull(cls, "cls");
        ApplicationContext context = getContext();
        context.getAutowireCapableBeanFactory().destroyBean(instance);
    }

    /**
     * Returns the Spring {@link org.springframework.context.ApplicationContext} for the current {@link javax.servlet.ServletContext}
     *
     * @return the spring context
     */
    protected abstract ApplicationContext getContext();
}
