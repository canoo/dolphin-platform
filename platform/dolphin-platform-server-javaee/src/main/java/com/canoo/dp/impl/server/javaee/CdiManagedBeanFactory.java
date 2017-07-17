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
package com.canoo.dp.impl.server.javaee;

import com.canoo.impl.platform.core.Assert;
import com.canoo.impl.server.beans.ManagedBeanFactory;
import com.canoo.impl.server.beans.PostConstructInterceptor;
import org.apache.deltaspike.core.api.provider.BeanManagerProvider;
import org.apache.deltaspike.core.util.bean.BeanBuilder;
import org.apache.deltaspike.core.util.metadata.builder.DelegatingContextualLifecycle;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * JavaEE / CDI based implementation of the {@link ManagedBeanFactory}
 *
 * @author Hendrik Ebbers
 */
public class CdiManagedBeanFactory implements ManagedBeanFactory {

    private Map<Object, CreationalContext> contextMap = new HashMap<>();

    private Map<Object, Bean> beanMap = new HashMap<>();

    @Override
    public void init(ServletContext servletContext) {}

    @Override
    public <T> T createDependendInstance(Class<T> cls) {
        Assert.requireNonNull(cls, "cls");
        BeanManager bm = BeanManagerProvider.getInstance().getBeanManager();
        AnnotatedType annotatedType = bm.createAnnotatedType(cls);
        final InjectionTarget<T> injectionTarget = bm.createInjectionTarget(annotatedType);
        final Bean<T> bean = new BeanBuilder<T>(bm)
                .beanClass(cls)
                .name(UUID.randomUUID().toString())
                .scope(Dependent.class)
                .beanLifecycle(new DelegatingContextualLifecycle<T>(injectionTarget))
                .create();
        Class<?> beanClass = bean.getBeanClass();
        CreationalContext<T> creationalContext = bm.createCreationalContext(bean);
        T instance = (T) bm.getReference(bean, beanClass, creationalContext);
        contextMap.put(instance, creationalContext);
        beanMap.put(instance, bean);
        return instance;
    }

    @Override
    public <T> T createDependendInstance(Class<T> cls, PostConstructInterceptor<T> interceptor) {
        Assert.requireNonNull(cls, "cls");
        Assert.requireNonNull(interceptor, "interceptor");
        BeanManager bm = BeanManagerProvider.getInstance().getBeanManager();
        AnnotatedType annotatedType = bm.createAnnotatedType(cls);
        final InjectionTarget<T> injectionTarget = bm.createInjectionTarget(annotatedType);

        //CDI.current().select()

        final Bean<T> bean = new BeanBuilder<T>(bm)
                .beanClass(cls)
                .name(UUID.randomUUID().toString())
                .scope(Dependent.class)
                .beanLifecycle(new DolphinPlatformContextualLifecycle<T>(injectionTarget, interceptor))
                .create();
        Class<?> beanClass = bean.getBeanClass();
        CreationalContext<T> creationalContext = bm.createCreationalContext(bean);
        T instance = (T) bm.getReference(bean, beanClass, creationalContext);
        contextMap.put(instance, creationalContext);
        beanMap.put(instance, bean);
        return instance;
    }

    @Override
    public <T> void destroyDependendInstance(T instance, Class<T> cls) {
        Assert.requireNonNull(instance, "instance");
        Bean bean = beanMap.remove(instance);
        CreationalContext context = contextMap.remove(instance);
        bean.destroy(instance, context);
    }

}
