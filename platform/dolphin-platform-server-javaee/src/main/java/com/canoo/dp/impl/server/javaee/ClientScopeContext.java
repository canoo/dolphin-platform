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
import com.canoo.dp.impl.server.bootstrap.PlatformBootstrap;
import com.canoo.dp.impl.server.client.ClientSessionProvider;
import com.canoo.platform.server.client.ClientSession;
import com.canoo.platform.server.javaee.ClientScoped;
import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;

import javax.enterprise.context.ContextException;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;

public class ClientScopeContext extends AbstractContext {

    private final static String CLIENT_STORAGE_ATTRIBUTE = "DolphinPlatformCdiContextualStorage";

    private final BeanManager beanManager;

    public ClientScopeContext(BeanManager beanManager) {
        super(beanManager);
        this.beanManager = beanManager;
    }

    @Override
    protected ContextualStorage getContextualStorage(Contextual<?> contextual, boolean createIfNotExist) {
        Object val = getDolphinSession().getAttribute(CLIENT_STORAGE_ATTRIBUTE);
        if(val != null) {
            if(val instanceof ContextualStorage) {
                return (ContextualStorage) val;
            } else {
                throw new ContextException("No ClientContext specified!");
            }
        } else {
            if(createIfNotExist) {
                ContextualStorage contextualStorage = new ContextualStorage(beanManager, false, false);
                getDolphinSession().setAttribute(CLIENT_STORAGE_ATTRIBUTE, contextualStorage);
                return contextualStorage;
            } else {
                return null;
            }
        }
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return ClientScoped.class;
    }

    public boolean isActive() {
        return getDolphinSession() != null;
    }

    private ClientSession getDolphinSession() {
        final ClientSessionProvider provider = PlatformBootstrap.getServerCoreComponents().getInstance(ClientSessionProvider.class);
        Assert.requireNonNull(provider, "provider");
        return provider.getCurrentClientSession();
    }

    public void destroy() {
        Object val = getDolphinSession().getAttribute(CLIENT_STORAGE_ATTRIBUTE);
        if(val != null && val instanceof ContextualStorage) {
            AbstractContext.destroyAllActive((ContextualStorage) val);
        }
    }
}
