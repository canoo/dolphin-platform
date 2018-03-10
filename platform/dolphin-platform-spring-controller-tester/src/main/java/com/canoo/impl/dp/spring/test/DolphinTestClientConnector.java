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
package com.canoo.impl.dp.spring.test;

import com.canoo.dp.impl.client.legacy.ClientModelStore;
import com.canoo.dp.impl.client.legacy.communication.AbstractClientConnector;
import com.canoo.dp.impl.client.legacy.communication.CommandAndHandler;
import com.canoo.dp.impl.client.legacy.communication.CommandBatcher;
import com.canoo.dp.impl.client.legacy.communication.OnFinishedHandler;
import com.canoo.dp.impl.client.legacy.communication.SimpleExceptionHandler;
import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.remoting.legacy.commands.StartLongPollCommand;
import com.canoo.dp.impl.remoting.legacy.communication.Command;
import org.apiguardian.api.API;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class DolphinTestClientConnector extends AbstractClientConnector {

    private final Function<List<Command>, List<Command>> communicationFunction;

    public DolphinTestClientConnector(final ClientModelStore clientModelStore, final Executor uiExecutor, final Function<List<Command>, List<Command>> communicationFunction) {
        super(clientModelStore, uiExecutor, new CommandBatcher(), new SimpleExceptionHandler(), Executors.newCachedThreadPool());
        this.communicationFunction = Assert.requireNonNull(communicationFunction, "communicationFunction");
    }

    @Override
    protected void commandProcessing() {}

    @Override
    public void disconnect() {}

    @Override
    public void send(Command command, OnFinishedHandler callback) {
        List<Command> answer = transmit(new ArrayList<>(Arrays.asList(command)));
        CommandAndHandler handler = new CommandAndHandler(command, callback);
        processResponse(answer, new ArrayList<>(Arrays.asList(handler)));
    }

    @Override
    protected List<Command> transmit(List<Command> commands) {
        ArrayList<Command> realCommands = new ArrayList<>(commands);
        realCommands.add(new StartLongPollCommand());
        return communicationFunction.apply(commands);
    }

}
