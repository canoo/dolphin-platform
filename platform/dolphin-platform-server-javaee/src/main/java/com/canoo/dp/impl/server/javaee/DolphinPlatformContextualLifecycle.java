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

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.server.beans.PostConstructInterceptor;
import org.apache.deltaspike.core.util.metadata.builder.ContextualLifecycle;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionTarget;

/**
 * Implements a CDI Lifecylce for Dolphin Platform Controllers
 *
 * @param <T> the class of the bean instance
 *
 * @author Hendrik Ebbers
 */
public class DolphinPlatformContextualLifecycle<T> implements ContextualLifecycle<T> {

    private final InjectionTarget<T> injectionTarget;

    private PostConstructInterceptor<T> interceptor;

    public DolphinPlatformContextualLifecycle(InjectionTarget<T> injectionTarget, PostConstructInterceptor<T> interceptor) {
        this.injectionTarget = Assert.requireNonNull(injectionTarget, "injectionTarget");
        this.interceptor = Assert.requireNonNull(interceptor, "interceptor");
    }

    @Override
    public T create(Bean<T> bean, CreationalContext<T> creationalContext) {
        Assert.requireNonNull(bean, "bean");
        Assert.requireNonNull(creationalContext, "creationalContext");
        if(interceptor == null) {
            throw new ModelInjectionException("No interceptor defined!");
        }
        try {
            T instance = injectionTarget.produce(creationalContext);
            interceptor.intercept(instance);
            injectionTarget.inject(instance, creationalContext);
            injectionTarget.postConstruct(instance);
            return instance;
        } finally {
            interceptor = null;
        }
    }

    @Override
    public void destroy(Bean<T> bean, T instance, CreationalContext<T> creationalContext) {
        Assert.requireNonNull(bean, "bean");
        Assert.requireNonNull(creationalContext, "creationalContext");
        injectionTarget.preDestroy(instance);
        creationalContext.release();
    }
}
