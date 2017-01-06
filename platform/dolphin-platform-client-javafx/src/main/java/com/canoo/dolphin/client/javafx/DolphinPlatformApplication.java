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
package com.canoo.dolphin.client.javafx;

import com.canoo.dolphin.client.ClientConfiguration;
import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.ClientContextFactory;
import com.canoo.implementation.dolphin.client.ClientInitializationException;
import com.canoo.implementation.dolphin.client.ClientShutdownException;
import com.canoo.implementation.dolphin.client.DolphinRuntimeException;
import com.canoo.implementation.dolphin.client.ClientContextImpl;
import com.canoo.implementation.dolphin.ReflectionHelper;
import com.canoo.implementation.dolphin.util.Assert;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.opendolphin.core.client.ClientDolphin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Defines a basic application class for Dolphin Platform based applications that can be used like the {@link Application}
 * class. Next to the general {@link Application} class of JavaFX this class supports the DOlphin Platform connecttion lifecycle.
 */
public abstract class DolphinPlatformApplication extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(DolphinPlatformApplication.class);

    private ClientContext clientContext;

    private ClientInitializationException initializationException;

    private Stage primaryStage;

    /**
     * Creates the connection to the Dolphin Platform server. If this method will be overridden always call the super method.
     *
     * @throws Exception a exception if the connection can't be created
     */
    @Override
    public void init() throws Exception {
        try {
            ClientConfiguration clientConfiguration = getClientConfiguration();
            clientConfiguration.getDolphinPlatformThreadFactory().setUncaughtExceptionHandler((Thread thread, Throwable exception) -> {
                clientConfiguration.getUiThreadHandler().executeInsideUiThread(() -> {
                    Assert.requireNonNull(thread, "thread");
                    Assert.requireNonNull(exception, "exception");
                    onRuntimeError(primaryStage, new DolphinRuntimeException(thread, "Unhandled error in Dolphin Platform background thread", exception));
                });
            });
            clientContext = ClientContextFactory.connect(clientConfiguration).get(clientConfiguration.getConnectionTimeout(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            initializationException = new ClientInitializationException("Can not initialize Dolphin Platform Context", e);
        }
    }

    /**
     * Returns the Dolphin Platform configuration for the client. As long as all the default configurations can be used
     * this method don't need to be overridden. The URL of the server will be configured by the {@link DolphinPlatformApplication#getServerEndpoint()}
     * method.
     *
     * @return The Dolphin Platform configuration for this client
     */
    protected JavaFXConfiguration getClientConfiguration() {
        JavaFXConfiguration configuration = null;
        try {
            configuration = new JavaFXConfiguration(getServerEndpoint());
        } catch (MalformedURLException e) {
            throw new ClientInitializationException("Client configuration cannot be created", e);
        }
        return configuration;
    }

    /**
     * Returns the server url of the Dolphin Platform server endpoint.
     *
     * @return the server url
     */
    protected abstract URL getServerEndpoint() throws MalformedURLException;

    /**
     * This methods defines parts of the Dolphin Platform lifecyycle and is therefore defined as final.
     * Use the {@link DolphinPlatformApplication#start(Stage, ClientContext)} method instead.
     *
     * @param primaryStage the primary stage
     * @throws Exception in case of an error
     */
    @Override
    public final void start(final Stage primaryStage) throws Exception {
        startImpl(primaryStage);
    }

    private final void startImpl(final Stage primaryStage) throws Exception {
        Assert.requireNonNull(primaryStage, "primaryStage");
        this.primaryStage = primaryStage;
        if (initializationException == null) {
            if (clientContext != null) {
                clientContext.onRemotingError(e -> onRuntimeError(primaryStage, new DolphinRuntimeException("Dolphin Platform remoting error!", e)));
                try {
                    start(primaryStage, clientContext);
                } catch (Exception e) {
                    onInitializationError(primaryStage, new ClientInitializationException("Error in application start!", e));
                }
            } else {
                onInitializationError(primaryStage, new ClientInitializationException("No clientContext was created!"));
            }
        } else {
            onInitializationError(primaryStage, initializationException);
        }
    }

    /**
     * This method must be defined by each application to create the initial view. The method will be called on
     * the JavaFX Platform thread after the connection to the DOlphin Platform server has been created.
     *
     * @param primaryStage  the primary stage
     * @param clientContext the Dolphin Platform context
     * @throws Exception in case of an error
     */
    protected abstract void start(Stage primaryStage, ClientContext clientContext) throws Exception;

    /**
     * Whenever JavaFX calls the stop method the connection to the Dolphin Platform server will be closed.
     *
     * @throws Exception an error
     */
    @Override
    public final void stop() throws Exception {
        if (clientContext != null) {
            try {
                clientContext.disconnect();
            } catch (Exception e) {
                onShutdownError(new ClientShutdownException(e));
            }
        }
    }

    protected final void reconnect(Stage primaryStage) {
        final ClientConfiguration clientConfiguration = getClientConfiguration();
        clientConfiguration.getBackgroundExecutor().submit(() -> {
            try {
                try {
                    if (clientContext != null) {
                        ClientDolphin clientDolphin = ReflectionHelper.getPrivileged(ReflectionHelper.getInheritedDeclaredField(ClientContextImpl.class, "clientDolphin"), clientContext);

                        //TODO: Not workin with the current connector. We need to stop the connector on disconnect.
                        //clientDolphin.stopPushListening();
                        //clientDolphin.sync(() -> System.out.println("SYNC"));

                        clientContext.disconnect().get(getClientConfiguration().getConnectionTimeout(), TimeUnit.MILLISECONDS);
                    }
                } catch (Exception e) {
                    LOG.error("Error in disconnect. This can end in an memory leak on the server!");
                } finally {
                    clientContext = null;
                }
                init();
                clientConfiguration.getUiThreadHandler().executeInsideUiThread(() -> {
                    try {
                        startImpl(primaryStage);
                    } catch (Exception e) {
                        onInitializationError(primaryStage, new ClientInitializationException("Error in reconnect!", e));
                    }
                });
            } catch (Exception e) {
                clientConfiguration.getUiThreadHandler().executeInsideUiThread(() -> onInitializationError(primaryStage, new ClientInitializationException("Error in reconnect!", e)));
            }
        });
    }

    /**
     * This method is called if the connection to the Dolphin Platform server can't be created. Application developers
     * can define some kind of error handling here.
     * By default the methods prints the exception in the log an call {@link System#exit(int)}
     *
     * @param primaryStage            the primary stage
     * @param initializationException the exception
     */
    protected void onInitializationError(Stage primaryStage, ClientInitializationException initializationException) {
        Assert.requireNonNull(initializationException, "initializationException");
        LOG.error("Dolphin Platform initialization error", initializationException);
        Platform.exit();
    }

    /**
     * This method is called if the connection to the Dolphin Platform server throws an exception at runtime. This can
     * for example happen if the server is shut down while the client is still running or if the server responses with
     * an error code.
     *
     * @param primaryStage     the primary stage
     * @param runtimeException the exception
     */
    protected void onRuntimeError(final Stage primaryStage, final DolphinRuntimeException runtimeException) {
        Assert.requireNonNull(runtimeException, "runtimeException");
        LOG.error("Dolphin Platform runtime error in thread " + runtimeException.getThread().getName(), runtimeException);
        Platform.exit();
    }

    /**
     * This method is called if the connection to the Dolphin Platform server can't be closed on {@link Application#stop()}.
     * Application developers can define some kind of error handling here.
     * By default the methods prints the exception in the log an call {@link System#exit(int)}
     *
     * @param shutdownException
     */
    protected void onShutdownError(ClientShutdownException shutdownException) {
        Assert.requireNonNull(shutdownException, "shutdownException");
        LOG.error("Dolphin Platform shutdown error", shutdownException);
        System.exit(-1);
    }
}
