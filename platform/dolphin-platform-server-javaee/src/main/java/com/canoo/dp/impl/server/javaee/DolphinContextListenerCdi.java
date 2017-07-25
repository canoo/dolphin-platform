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
import com.canoo.platform.server.ServerListener;
import com.canoo.platform.server.client.ClientSession;
import com.canoo.platform.server.client.ClientSessionListener;
import com.canoo.platform.server.javaee.ClientScoped;
import org.apache.deltaspike.core.api.provider.BeanManagerProvider;

import javax.enterprise.inject.spi.BeanManager;

/**
 * This listener destroyes the {@link ClientScoped} CDI scope whenever a {@link ClientSession} will be destroyed
 */
@ServerListener
public class DolphinContextListenerCdi implements ClientSessionListener {

    @Override
    public void sessionCreated(ClientSession dolphinSession) {

    }

    @Override
    public void sessionDestroyed(ClientSession dolphinSession) {
        Assert.requireNonNull(dolphinSession, "dolphinSession");
        BeanManager bm = BeanManagerProvider.getInstance().getBeanManager();
        ClientScopeContext clientContext = (ClientScopeContext) bm.getContext(ClientScoped.class);
        clientContext.destroy();
    }
}
