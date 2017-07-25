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
package com.canoo.platform.client.javafx;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.client.*;
import com.canoo.platform.core.DolphinRuntimeException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Defines a basic application class for Dolphin Platform based applications that can be used like the {@link Application}
 * class. Next to the general {@link Application} class of JavaFX this class supports the DOlphin Platform connecttion lifecycle.
 */
public abstract class DolphinPlatformApplication extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(DolphinPlatformApplication.class);

    private ClientContext clientContext;

    private ClientInitializationException initializationException;

    private final List<DolphinRuntimeException> runtimeExceptionsAtInitialization = new CopyOnWriteArrayList<>();

    private Stage primaryStage;

    private AtomicBoolean connectInProgress = new AtomicBoolean(false);

    /**
     * Returns the server url of the Dolphin Platform server endpoint.
     *
     * @return the server url
     */
    protected abstract URL getServerEndpoint() throws MalformedURLException;

    /**
     * Returns the Dolphin Platform configuration for the client. As long as all the default configurations can be used
     * this method don't need to be overridden. The URL of the server will be configured by the {@link DolphinPlatformApplication#getServerEndpoint()}
     * method.
     *
     * @return The Dolphin Platform configuration for this client
     */
    protected JavaFXConfiguration createClientConfiguration() {
        try {
            JavaFXConfiguration configuration = new JavaFXConfiguration(getServerEndpoint());
            configuration.addRemotingExceptionHandler(e -> {
                if (connectInProgress.get()) {
                    runtimeExceptionsAtInitialization.add(new DolphinRuntimeException("Dolphin Platform remoting error", e));
                } else {
                    onRuntimeError(primaryStage, new DolphinRuntimeException("Dolphin Platform remoting error!", e));
                }
            });
            return configuration;
        } catch (MalformedURLException e) {
            throw new ClientInitializationException("Client configuration cannot be created", e);
        }
    }

    private final ClientContext createClientContext(final ClientConfiguration clientConfiguration) throws Exception {
        Assert.requireNonNull(clientConfiguration, "clientConfiguration");
        return ClientContextFactory.create(clientConfiguration);
    }

    /**
     * Creates the connection to the Dolphin Platform server. If this method will be overridden always call the super method.
     *
     * @throws Exception a exception if the connection can't be created
     */
    @Override
    public final void init() throws Exception {
        final ClientConfiguration clientConfiguration = createClientConfiguration();
        clientConfiguration.getDolphinPlatformThreadFactory().setUncaughtExceptionHandler((Thread thread, Throwable exception) -> {
            clientConfiguration.getUiExecutor().execute(() -> {
                Assert.requireNonNull(thread, "thread");
                Assert.requireNonNull(exception, "exception");

                if (connectInProgress.get()) {
                    runtimeExceptionsAtInitialization.add(new DolphinRuntimeException(thread, "Unhandled error in Dolphin Platform background thread", exception));
                } else {
                    onRuntimeError(primaryStage, new DolphinRuntimeException(thread, "Unhandled error in Dolphin Platform background thread", exception));
                }
            });
        });

        try {
            clientContext = createClientContext(clientConfiguration);
            Assert.requireNonNull(clientContext, "clientContext");

            connectInProgress.set(true);
            clientContext.connect().get(clientConfiguration.getConnectionTimeout(), TimeUnit.MILLISECONDS);
        } catch (ClientInitializationException e) {
            initializationException = e;
        } catch (Exception e) {
            initializationException = new ClientInitializationException("Can not initialize Dolphin Platform Context", e);
        } finally {
            connectInProgress.set(false);
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
     * This methods defines parts of the Dolphin Platform lifecyycle and is therefore defined as final.
     * Use the {@link DolphinPlatformApplication#start(Stage, ClientContext)} method instead.
     *
     * @param primaryStage the primary stage
     * @throws Exception in case of an error
     */
    @Override
    public final void start(final Stage primaryStage) throws Exception {
        Assert.requireNonNull(primaryStage, "primaryStage");

        this.primaryStage = primaryStage;

        if (initializationException == null) {
            if (clientContext != null) {
                try {
                    start(primaryStage, clientContext);
                } catch (Exception e) {
                    handleInitializationError(primaryStage, new ClientInitializationException("Error in application start!", e));
                }
            } else {
                handleInitializationError(primaryStage, new ClientInitializationException("No clientContext was created!"));
            }
        } else {
            handleInitializationError(primaryStage, initializationException);
        }
    }

    protected final CompletableFuture<Void> disconnect() {
        if (clientContext != null) {
            return clientContext.disconnect();
        } else {
            CompletableFuture<Void> result = new CompletableFuture<>();
            result.complete(null);
            return result;
        }
    }

    /**
     * Whenever JavaFX calls the stop method the connection to the Dolphin Platform server will be closed.
     *
     * @throws Exception an error
     */
    @Override
    public final void stop() throws Exception {
        disconnect();
    }

    protected final CompletableFuture<Void> reconnect(final Stage primaryStage) {
        Assert.requireNonNull(primaryStage, "primaryStage");
        final CompletableFuture<Void> result = new CompletableFuture<>();

        final ClientConfiguration clientConfiguration = createClientConfiguration();

        clientConfiguration.getBackgroundExecutor().execute(() -> {
            try {
                disconnect().get(1_000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                LOG.warn("Can not disconnect. Trying to reconnect anyway.");
            }

            try {
                if (clientContext == null) {
                    clientContext = createClientContext(clientConfiguration);
                }
                Assert.requireNonNull(clientContext, "clientContext");

                connectInProgress.set(true);
                clientContext.connect().get(clientConfiguration.getConnectionTimeout(), TimeUnit.MILLISECONDS);

                Platform.runLater(() -> {
                    try {
                        start(primaryStage, clientContext);
                    } catch (Exception e) {
                        handleInitializationError(primaryStage, new ClientInitializationException("Error in application reconnect", e));
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> handleInitializationError(primaryStage, new ClientInitializationException("Error in application reconnect", e)));
            } finally {
                connectInProgress.set(false);
            }
            result.complete(null);
        });
        return result;
    }

    protected void onInitializationError(Stage primaryStage, ClientInitializationException initializationException, Iterable<DolphinRuntimeException> possibleCauses) {
        LOG.error("Dolphin Platform initialization error", initializationException);
        for (DolphinRuntimeException cause : possibleCauses) {
            LOG.error("Possible cause", cause);
        }
        Platform.exit();
    }

    private final void handleInitializationError(final Stage primaryStage, final ClientInitializationException initializationException) {
        Iterable<DolphinRuntimeException> possibleCauses = Collections.unmodifiableList(runtimeExceptionsAtInitialization);
        runtimeExceptionsAtInitialization.clear();
        onInitializationError(primaryStage, initializationException, possibleCauses);
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
