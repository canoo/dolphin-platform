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
package com.canoo.dolphin.client.util;

import com.canoo.dp.impl.client.legacy.ClientModelStore;
import com.canoo.dp.impl.client.legacy.communication.AbstractClientConnector;
import com.canoo.dp.impl.client.legacy.communication.CommandBatcher;
import com.canoo.dp.impl.client.legacy.communication.SimpleExceptionHandler;
import com.canoo.dp.impl.remoting.legacy.commands.Command;
import com.canoo.dp.impl.server.legacy.ServerConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class InMemoryClientConnector extends AbstractClientConnector {

    private static final Logger LOG = LoggerFactory.getLogger(InMemoryClientConnector.class);

    private final ServerConnector serverConnector;

    private long sleepMillis = 0;

    public InMemoryClientConnector(final ClientModelStore clientModelStore, final ServerConnector serverConnector, final CommandBatcher commandBatcher, final Executor uiExecutor) {
        super(clientModelStore, uiExecutor, commandBatcher, new SimpleExceptionHandler(), Executors.newCachedThreadPool());
        this.serverConnector = Objects.requireNonNull(serverConnector);
        connect(false);
    }

    @Override
    protected void release() {
    }

    @Override
    public String getClientId() {
        return null;
    }

    @Override
    public List<Command> transmit(final List<Command> commands) {
        LOG.trace("transmitting {} commands", commands.size());
        if (serverConnector == null) {
            LOG.warn("no server connector wired for in-memory connector");
            return Collections.EMPTY_LIST;
        }
        if (sleepMillis > 0) {
            try {
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        List<Command> result = new LinkedList<Command>();
        for (Command command : commands) {
            LOG.trace("processing {}", command);
            result.addAll(serverConnector.receive(command));// there is no need for encoding since we are in-memory
        }
        return result;
    }

    public void setSleepMillis(final long sleepMillis) {
        this.sleepMillis = sleepMillis;
    }
}
