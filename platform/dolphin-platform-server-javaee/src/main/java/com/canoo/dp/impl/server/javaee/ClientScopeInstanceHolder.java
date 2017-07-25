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

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

public class ClientScopeInstanceHolder<T> {

    private Bean<T> bean;

    private CreationalContext<T> creationalContext;

    private T instance;

    public ClientScopeInstanceHolder(Bean<T> bean, CreationalContext<T> creationalContext, T instance) {
        this.bean = Assert.requireNonNull(bean, "bean");
        this.creationalContext = Assert.requireNonNull(creationalContext, "creationalContext");
        this.instance = Assert.requireNonNull(instance, "instance");
    }

    public Bean<T> getBean() {
        return bean;
    }

    public CreationalContext<T> getCreationalContext() {
        return creationalContext;
    }

    public T getInstance() {
        return instance;
    }

    public void destroy() {
        bean.destroy(instance, creationalContext);
    }
}
