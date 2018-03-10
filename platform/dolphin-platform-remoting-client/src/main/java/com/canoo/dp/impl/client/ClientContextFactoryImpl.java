/*
 * Copyright 2015-2018 Canoo Engineering AG.
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
package com.canoo.dp.impl.client;

import com.canoo.dp.impl.client.legacy.communication.SimpleExceptionHandler;
import com.canoo.dp.impl.platform.client.session.StrictClientSessionResponseHandler;
import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.client.ClientConfiguration;
import com.canoo.platform.client.PlatformClient;
import com.canoo.platform.client.session.ClientSessionStore;
import com.canoo.platform.core.http.HttpClient;
import com.canoo.platform.core.http.HttpURLConnectionHandler;
import com.canoo.platform.remoting.client.ClientContext;
import com.canoo.platform.remoting.client.ClientContextFactory;
import com.canoo.platform.remoting.client.ClientInitializationException;
import org.apiguardian.api.API;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

import static com.canoo.dp.impl.client.RemotingConfigurationConstants.DEFAULT_EXCEPTION_HANDLER_ACTIVE;
import static org.apiguardian.api.API.Status.INTERNAL;


@API(since = "0.x", status = INTERNAL)
public class ClientContextFactoryImpl implements ClientContextFactory {

    public ClientContextFactoryImpl() {
    }


    public ClientContext create(final ClientConfiguration configuration, final URI endpoint) {
        Assert.requireNonNull(configuration, "clientConfiguration");
        final HttpClient httpClient = PlatformClient.getService(HttpClient.class);
        final HttpURLConnectionHandler clientSessionCheckResponseHandler = new StrictClientSessionResponseHandler(endpoint);
        httpClient.addResponseHandler(clientSessionCheckResponseHandler);
        final ClientSessionStore sessionStore = PlatformClient.getService(ClientSessionStore.class);
        final ClientContext context =  new ClientContextImpl(configuration, endpoint, httpClient, sessionStore);
        if(configuration.getBooleanProperty(DEFAULT_EXCEPTION_HANDLER_ACTIVE, true)) {
            context.addRemotingExceptionHandler(new SimpleExceptionHandler());
        }
        return context;
    }

}
