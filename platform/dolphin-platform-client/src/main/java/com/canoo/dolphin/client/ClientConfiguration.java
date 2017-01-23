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
package com.canoo.dolphin.client;

import com.canoo.dolphin.client.impl.DefaultHttpURLConnectionFactory;
import com.canoo.dolphin.client.impl.DefaultHttpURLConnectionResponseHandler;
import com.canoo.dolphin.client.impl.DolphinPlatformThreadFactoryImpl;
import com.canoo.common.Assert;
import com.canoo.remoting.client.communication.UiThreadHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.CookieManager;
import java.net.CookieStore;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

/**
 * Configuration class for a Dolphin Platform client. A configuration is needed to create a {@link ClientContext} by
 * using the {@link ClientContextFactory} (see {@link ClientContextFactory#connect(ClientConfiguration)}).
 * The configuration wraps the url to the Dolphin Platform server endpoint and a specific ui thread handler.
 * Since Dolphin Platform manages UI releated concurrency for you it needs a handler to call methods directly on the
 * ui thread. For platforms like JavaFX the JavaFX client lib of Dolphin Platform contains a specific
 * configuration class that extends the {@link ClientConfiguration} and already defines the needed ui handler. If
 * you want to use Dolphin Platform with a different Java based UI you need to extends this class or create a ui handler
 * on your own.
 */
public class ClientConfiguration {

    private final static long DEFAULT_CONNECTION_TIMEOUT = 15000;

    private final URL serverEndpoint;

    private final UiThreadHandler uiThreadHandler;

    private final ExecutorService backgroundExecutor;

    private final DolphinPlatformThreadFactory dolphinPlatformThreadFactory;

    private Level dolphinLogLevel;

    private long connectionTimeout;

    private HttpURLConnectionFactory connectionFactory;

    private HttpURLConnectionResponseHandler responseHandler;

    private CookieStore cookieStore;

    private final static Logger LOG = LoggerFactory.getLogger(ClientConfiguration.class);

    /**
     * Default constructor of a client configuration
     *
     * @param serverEndpoint the Dolphin Platform server url
     * @param uiThreadHandler the ui thread handler
     */
    public ClientConfiguration(URL serverEndpoint, UiThreadHandler uiThreadHandler) {
        this.serverEndpoint = Assert.requireNonNull(serverEndpoint, "serverEndpoint");
        this.uiThreadHandler = Assert.requireNonNull(uiThreadHandler, "uiThreadHandler");
        this.dolphinLogLevel = Level.SEVERE;
        this.connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
        dolphinPlatformThreadFactory = new DolphinPlatformThreadFactoryImpl();
        backgroundExecutor = Executors.newCachedThreadPool(dolphinPlatformThreadFactory);
        cookieStore = new CookieManager().getCookieStore();
        connectionFactory = new DefaultHttpURLConnectionFactory();
        responseHandler = new DefaultHttpURLConnectionResponseHandler();
    }

    /**
     * Returns the ui thread handler
     *
     * @return ui thread handler
     */
    public UiThreadHandler getUiThreadHandler() {
        return uiThreadHandler;
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
     * Returns the logging level for the remoting layer
     *
     * @return the logging level for the remoting layer
     */
    public Level getDolphinLogLevel() {
        return dolphinLogLevel;
    }

    /**
     * Sets the logging level for the remoting layer
     *
     * @param dolphinLogLevel the logging level for the remoting layer
     */
    public void setDolphinLogLevel(Level dolphinLogLevel) {
        Assert.requireNonNull(dolphinLogLevel, "dolphinLogLevel");
        this.dolphinLogLevel = dolphinLogLevel;
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

    public DolphinPlatformThreadFactory getDolphinPlatformThreadFactory() {
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

}
