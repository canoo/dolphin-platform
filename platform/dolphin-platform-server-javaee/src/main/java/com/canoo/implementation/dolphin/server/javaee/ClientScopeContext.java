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

import com.canoo.dolphin.server.session.DolphinSession;
import com.canoo.implementation.dolphin.server.bootstrap.DolphinPlatformBootstrap;
import com.canoo.dolphin.server.javaee.ClientScoped;
import com.canoo.implementation.dolphin.util.Assert;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientScopeContext implements Context {

    //see https://rpestano.wordpress.com/2013/06/30/cdi-custom-scope/
    // https://github.com/rmpestano/cdi-custom-scope/tree/master/custom-scope-extension/src/main/java/custom/scope/extension

    private final static String CLIENT_STORE_ATTRIBUTE = "DolphinPlatformCdiClientScopeStore";

    public ClientScopeContext() {
    }

    @Override
    public <T> T get(final Contextual<T> contextual) {
        Assert.requireNonNull(contextual, "contextual");
        Bean bean = (Bean) contextual;
        if (getLocalStore().containsKey(bean.getBeanClass())) {
            return (T) getLocalStore().get(bean.getBeanClass()).getInstance();
        } else {
            return null;
        }
    }

    @Override
    public <T> T get(final Contextual<T> contextual, final CreationalContext<T> creationalContext) {
        Assert.requireNonNull(contextual, "contextual");
        Bean bean = (Bean) contextual;
        if (getLocalStore().containsKey(bean.getBeanClass())) {
            return (T) getLocalStore().get(bean.getBeanClass()).getInstance();
        } else {
            T instance = (T) bean.create(creationalContext);
            ClientScopeInstanceHolder<T> instanceHolder = new ClientScopeInstanceHolder(bean, creationalContext, instance);
            getLocalStore().put(bean.getBeanClass(), instanceHolder);
            return instance;
        }
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return ClientScoped.class;
    }

    public boolean isActive() {
        return getDolphinSession() != null;
    }

    private Map<Class<?>, ClientScopeInstanceHolder<?>> getLocalStore() {
        Map<Class<?>, ClientScopeInstanceHolder<?>> localStore = getDolphinSession().getAttribute(CLIENT_STORE_ATTRIBUTE);
        if(localStore == null) {
            localStore = new ConcurrentHashMap<>();
            getDolphinSession().setAttribute(CLIENT_STORE_ATTRIBUTE, localStore);
        }
        return localStore;
    }

    private DolphinSession getDolphinSession() {
        return DolphinPlatformBootstrap.getContextProvider().getCurrentDolphinSession();
    }

    public void destroy() {
        for(ClientScopeInstanceHolder<?> holder : getLocalStore().values()) {
            holder.destroy();
        }
        getLocalStore().clear();
    }
}
