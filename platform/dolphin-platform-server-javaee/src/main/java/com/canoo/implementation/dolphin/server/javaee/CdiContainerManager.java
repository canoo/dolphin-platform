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

import com.canoo.implementation.dolphin.server.container.ContainerManager;
import com.canoo.implementation.dolphin.server.container.ModelInjector;
import com.canoo.implementation.dolphin.util.Assert;
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
 * JavaEE / CDI based implementation of the {@link ContainerManager}
 *
 * @author Hendrik Ebbers
 */
public class CdiContainerManager implements ContainerManager {

    private Map<Object, CreationalContext> contextMap = new HashMap<>();

    private Map<Object, Bean> beanMap = new HashMap<>();

    @Override
    public void init(ServletContext servletContext) {
    }

    @Override
    public <T> T createManagedController(final Class<T> controllerClass, final ModelInjector modelInjector) {
        Assert.requireNonNull(controllerClass, "controllerClass");
        Assert.requireNonNull(modelInjector, "modelInjector");
        BeanManager bm = BeanManagerProvider.getInstance().getBeanManager();
        AnnotatedType annotatedType = bm.createAnnotatedType(controllerClass);
        final InjectionTarget<T> injectionTarget = bm.createInjectionTarget(annotatedType);
        final Bean<T> bean = new BeanBuilder<T>(bm)
                .beanClass(controllerClass)
                .name(UUID.randomUUID().toString())
                .scope(Dependent.class)
                .beanLifecycle(new DolphinPlatformContextualLifecycle<T>(injectionTarget, modelInjector))
                .create();
        Class<?> beanClass = bean.getBeanClass();
        CreationalContext<T> creationalContext = bm.createCreationalContext(bean);
        T instance = (T) bm.getReference(bean, beanClass, creationalContext);
        contextMap.put(instance, creationalContext);
        beanMap.put(instance, bean);
        return instance;
    }

    @Override
    public <T> T createListener(Class<T> listenerClass) {
        Assert.requireNonNull(listenerClass, "listenerClass");
        BeanManager bm = BeanManagerProvider.getInstance().getBeanManager();
        AnnotatedType annotatedType = bm.createAnnotatedType(listenerClass);
        final InjectionTarget<T> injectionTarget = bm.createInjectionTarget(annotatedType);
        final Bean<T> bean = new BeanBuilder<T>(bm)
                .beanClass(listenerClass)
                .scope(Dependent.class)
                .name(UUID.randomUUID().toString())
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
    public void destroyController(Object instance, Class controllerClass) {
        Assert.requireNonNull(instance, "instance");
        Bean bean = beanMap.remove(instance);
        CreationalContext context = contextMap.remove(instance);
        bean.destroy(instance, context);
    }
}
