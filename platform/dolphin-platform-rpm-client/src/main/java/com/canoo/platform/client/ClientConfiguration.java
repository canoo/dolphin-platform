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
package com.canoo.platform.client;

import com.canoo.impl.platform.client.DefaultHttpURLConnectionFactory;
import com.canoo.impl.platform.client.DefaultHttpURLConnectionResponseHandler;
import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.platform.core.IdentitySet;
import com.canoo.dp.impl.platform.core.SimpleDolphinPlatformThreadFactory;
import com.canoo.platform.core.PlatformThreadFactory;
import com.canoo.platform.core.functional.Subscription;
import org.opendolphin.core.client.comm.RemotingExceptionHandler;
import org.opendolphin.core.client.comm.SimpleExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.CookieManager;
import java.net.CookieStore;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Configuration class for a Dolphin Platform client. A configuration is needed to create a {@link ClientContext} by
 * using the {@link ClientContextFactory} (see {@link ClientContextFactory#create(ClientConfiguration)}).
 * The configuration wraps the url to the Dolphin Platform server endpoint and a specific ui thread handler.
 * Since Dolphin Platform manages UI releated concurrency for you it needs a handler to call methods directly on the
 * ui thread. For platforms like JavaFX the JavaFX client lib of Dolphin Platform contains a specific
 * configuration class that extends the {@link ClientConfiguration} and already defines the needed ui handler. If
 * you want to use Dolphin Platform with a different Java based UI you need to extends this class or create a ui handler
 * on your own.
 */
public class ClientConfiguration {

    private final static long DEFAULT_CONNECTION_TIMEOUT = 5_000;

    private final URL serverEndpoint;

    private final Executor uiExecutor;

    private final ExecutorService backgroundExecutor;

    private final PlatformThreadFactory dolphinPlatformThreadFactory;

    protected Set<RemotingExceptionHandler> remotingExceptionHandlers = new IdentitySet<>();

    private long connectionTimeout;

    private HttpURLConnectionFactory connectionFactory;

    private HttpURLConnectionResponseHandler responseHandler;

    private CookieStore cookieStore;

    private final static Logger LOG = LoggerFactory.getLogger(ClientConfiguration.class);

    /**
     * Default constructor of a client configuration
     *
     * @param serverEndpoint the Dolphin Platform server url
     * @param uiExecutor     the ui thread handler
     */
    public ClientConfiguration(URL serverEndpoint, Executor uiExecutor) {
        this.serverEndpoint = Assert.requireNonNull(serverEndpoint, "serverEndpoint");
        this.uiExecutor = Assert.requireNonNull(uiExecutor, "uiExecutor");
        this.connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
        dolphinPlatformThreadFactory = new SimpleDolphinPlatformThreadFactory();
        backgroundExecutor = Executors.newCachedThreadPool(dolphinPlatformThreadFactory);
        cookieStore = new CookieManager().getCookieStore();
        connectionFactory = new DefaultHttpURLConnectionFactory();
        responseHandler = new DefaultHttpURLConnectionResponseHandler();
        remotingExceptionHandlers.add(new SimpleExceptionHandler());
    }

    /**
     * Returns the ui thread handler
     *
     * @return ui thread handler
     */
    public Executor getUiExecutor() {
        return uiExecutor;
    }

    /**
     * Returns the Dolphin Platform server endpoint
     *
     * @return the server endpoint
     */
    public URL getServerEndpoint() {
        return serverEndpoint;
    }

    /**
     * Returns the connection timeout in milliseconds
     *
     * @return the connection timeout in milliseconds
     */
    public long getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Sets the connection timeout in milliseconds. if the value is < 1 it will be set to the default value {@link #DEFAULT_CONNECTION_TIMEOUT} (5000 ms)
     *
     * @param connectionTimeout the connection timeout in milliseconds
     */
    public void setConnectionTimeout(long connectionTimeout) {
        if (connectionTimeout > 0) {
            this.connectionTimeout = connectionTimeout;
        } else {
            LOG.warn("Default connection timeout (" + DEFAULT_CONNECTION_TIMEOUT + " ms) is used instead of " + connectionTimeout + " ms");
            this.connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
        }
    }

    public ExecutorService getBackgroundExecutor() {
        return backgroundExecutor;
    }

    public PlatformThreadFactory getDolphinPlatformThreadFactory() {
        return dolphinPlatformThreadFactory;
    }

    public HttpURLConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }

    public HttpURLConnectionResponseHandler getResponseHandler() {
        return responseHandler;
    }

    public void setConnectionFactory(HttpURLConnectionFactory connectionFactory) {
        this.connectionFactory = Assert.requireNonNull(connectionFactory, "connectionFactory");
    }

    public void setCookieStore(CookieStore cookieStore) {
        this.cookieStore = Assert.requireNonNull(cookieStore, "cookieStore");
    }

    public void setResponseHandler(HttpURLConnectionResponseHandler responseHandler) {
        this.responseHandler = Assert.requireNonNull(responseHandler, "responseHandler");
    }

    public Set<RemotingExceptionHandler> getRemotingExceptionHandlers() {
        return Collections.unmodifiableSet(remotingExceptionHandlers);
    }

    public Subscription addRemotingExceptionHandler(final RemotingExceptionHandler remotingExceptionHandler) {
        Assert.requireNonNull(remotingExceptionHandler, "remotingExceptionHandlers");
        this.remotingExceptionHandlers.add(remotingExceptionHandler);
        return new Subscription() {

            @Override
            public void unsubscribe() {
                ClientConfiguration.this.remotingExceptionHandlers.remove(remotingExceptionHandler);
            }
        };
    }
}
