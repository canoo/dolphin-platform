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
package com.canoo.dp.impl.client.legacy.communication;

import com.canoo.dp.impl.client.legacy.ClientModelStore;
import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.remoting.legacy.commands.InterruptLongPollCommand;
import com.canoo.dp.impl.remoting.legacy.commands.StartLongPollCommand;
import com.canoo.dp.impl.remoting.legacy.communication.Command;
import com.canoo.platform.core.functional.Subscription;
import com.canoo.platform.remoting.DolphinRemotingException;
import com.canoo.platform.remoting.client.RemotingExceptionHandler;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.apiguardian.api.API.Status.DEPRECATED;

@API(since = "0.x", status = DEPRECATED)
public abstract class AbstractClientConnector {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractClientConnector.class);

    private final Executor uiExecutor;

    private final Executor backgroundExecutor;

    private final RemotingExceptionHandler remotingExceptionHandler;

    private final ClientResponseHandler responseHandler;

    private final ICommandBatcher commandBatcher;

    private final AtomicBoolean releaseNeeded = new AtomicBoolean(false);

    private final AtomicBoolean connectedFlag = new AtomicBoolean(false);

    private final AtomicBoolean useLongPolling = new AtomicBoolean(false);

    private final List<Consumer<Boolean>> connectionListener = new CopyOnWriteArrayList<>();

    private final StartLongPollCommand pushListener;

    private final InterruptLongPollCommand releaseCommand;

    private final Lock connectionStateLock = new ReentrantLock();

    private final Lock errorHandlingLock = new ReentrantLock();

    protected AbstractClientConnector(final ClientModelStore clientModelStore, final Executor uiExecutor, final ICommandBatcher commandBatcher, final RemotingExceptionHandler remotingExceptionHandler, final Executor backgroundExecutor) {
        this.uiExecutor = Assert.requireNonNull(uiExecutor, "uiExecutor");
        this.commandBatcher = Assert.requireNonNull(commandBatcher, "commandBatcher");
        this.remotingExceptionHandler = Assert.requireNonNull(remotingExceptionHandler, "remotingExceptionHandler");
        this.backgroundExecutor = Assert.requireNonNull(backgroundExecutor, "backgroundExecutor");
        this.responseHandler = new ClientResponseHandler(clientModelStore);

        this.pushListener = new StartLongPollCommand();
        this.releaseCommand = new InterruptLongPollCommand();
    }

    private void handleError(final Throwable exception) {
        Assert.requireNonNull(exception, "exception");

        errorHandlingLock.lock();
        try {
            if (isConnected()) {
                try {
                    uiExecutor.execute(() -> {
                        if (exception instanceof DolphinRemotingException) {
                            remotingExceptionHandler.handle((DolphinRemotingException) exception);
                        } else {
                            remotingExceptionHandler.handle(new DolphinRemotingException("internal remoting error", exception));
                        }
                    });
                } finally {
                    disconnect();
                }
            } else {
                LOG.debug("internal remoting error after remoting was closed...", exception);
            }
        } finally {
            errorHandlingLock.unlock();
        }
    }

    private void doRequest() throws InterruptedException, DolphinRemotingException {
        releaseNeeded.set(true);
        try {
            final List<CommandAndHandler> toProcess = commandBatcher.getWaitingBatches().getVal();
            final List<Command> commands = toProcess.stream()
                    .map(c -> c.getCommand())
                    .collect(Collectors.toList());

            if(useLongPolling.get()) {
                commands.add(pushListener);
            }

            if (LOG.isDebugEnabled()) {
                final StringBuffer buffer = new StringBuffer();
                commands.stream()
                        .map(c -> c.getClass().getSimpleName())
                        .forEach(n -> buffer.append(n).append(", "));
                LOG.trace("Sending {} commands to server: {}", commands.size(), buffer.substring(0, buffer.length() - 2));
            } else {
                LOG.trace("Sending {} commands to server", commands.size());
            }

            final List<? extends Command> answers = transmit(commands);

            uiExecutor.execute(() -> processResponse(answers, toProcess));
        } finally {
            releaseNeeded.set(false);
        }
    }

    protected void commandProcessing() {
        try {
            //first request without long poll
            doRequest();

            while (isConnected()) {
                doRequest();
            }
        } catch (Exception e) {
            handleError(e);
        }
    }

    protected abstract List<Command> transmit(final List<Command> commands) throws DolphinRemotingException;

    public void send(final Command command, final OnFinishedHandler callback) {
        Assert.requireNonNull(command, "command");

        if (!connectedFlag.get()) {
            //TODO: Change to DolphinRemotingException
            throw new IllegalStateException("Connection is broken");
        }

        LOG.trace("Command of type {} should be send to server", command.getClass().getSimpleName());

        // we have some change so regardless of the batching we may have to interruptLongPoll a push
        if (useLongPolling.get() && releaseNeeded.get()) {
            interruptLongPoll();
        }
        // we are inside the UI thread and events calls come in strict order as received by the UI toolkit
        CommandAndHandler handler = new CommandAndHandler(command, callback);
        commandBatcher.batch(handler);
    }

    public void send(final Command command) {
        send(command, null);
    }

    protected void processResponse(final List<? extends Command> responseCommands, final List<CommandAndHandler> commandsAndHandlers) {
        Assert.requireNonNull(responseCommands, "response");
        Assert.requireNonNull(commandsAndHandlers, "commandsAndHandlers");

        if (LOG.isDebugEnabled() && responseCommands.size() > 0) {
            final StringBuffer buffer = new StringBuffer();
            responseCommands.stream()
                    .map(c -> c.getClass().getSimpleName())
                    .forEach(n -> buffer.append(n).append(", "));
            LOG.trace("Processing {} commands from server: {}", responseCommands.size(), buffer.substring(0, buffer.length() - 2));
        } else {
            LOG.trace("Processing {} commands from server", responseCommands.size());
        }

        for (final Command serverCommand : responseCommands) {
            responseHandler.dispatchHandle(serverCommand);
        }

        LOG.trace("Handling registered callbacks");
        commandsAndHandlers.stream().map(c -> c.getHandler()).filter(c -> c != null).forEach(c -> {
            try {
                c.onFinished();
            } catch (Exception e) {
                LOG.error("Error in handling callback", e);
                throw e;
            }
        });
    }

    /**
     * Release the current push listener, which blocks the sending queue.
     * Does nothing in case that the push listener is not active.
     */
    protected void interruptLongPoll() {
        releaseNeeded.set(false);// interruptLongPoll is under way
        backgroundExecutor.execute(() -> {
            try {
                if (isConnected()) {
                    transmit(Collections.singletonList(releaseCommand));
                }
            } catch (final DolphinRemotingException e) {
                handleError(e);
            }
        });
    }

    public boolean isConnected() {
        connectionStateLock.lock();
        try {
            return connectedFlag.get();
        } finally {
            connectionStateLock.unlock();
        }
    }

    public void connect() {
        connectionStateLock.lock();
        try {
            if (isConnected()) {
                throw new IllegalStateException("already connected");
            }
            connectedFlag.set(true);
            commandBatcher.clear();
            backgroundExecutor.execute(() -> commandProcessing());
            uiExecutor.execute(() -> connectionListener.forEach(l -> l.accept(true)));

        } finally {
            connectionStateLock.unlock();
        }
    }

    public void disconnect() {
        connectionStateLock.lock();
        try {
            if (!isConnected()) {
                throw new IllegalStateException("already disconnected");
            }
            connectedFlag.set(false);
            useLongPolling.set(false);
            commandBatcher.clear();
            uiExecutor.execute(() -> connectionListener.forEach(l -> l.accept(false)));
        } finally {
            connectionStateLock.unlock();
        }
    }

    public Subscription addConnectionListener(final Consumer<Boolean> listener) {
        Assert.requireNonNull(listener, "listener");
        connectionListener.add(listener);
        return () -> connectionListener.remove(listener);
    }

    public void startLongPolling() {
        useLongPolling.set(true);
    }
}
