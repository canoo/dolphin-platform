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
package com.canoo.dolphin.test.impl;

import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.server.context.DolphinContext;
import com.canoo.remoting.client.ClientDolphin;
import com.canoo.remoting.client.communication.AbstractClientConnector;
import com.canoo.remoting.client.communication.CommandAndHandler;
import com.canoo.remoting.client.communication.OnFinishedHandler;
import com.canoo.communication.common.commands.Command;
import com.canoo.communication.common.commands.NamedCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DolphinTestClientConnector extends AbstractClientConnector{

    private final DolphinContext dolphinContext;

    public DolphinTestClientConnector(ClientDolphin clientDolphin, DolphinContext dolphinContext) {
        super(clientDolphin);
        this.dolphinContext = dolphinContext;
    }

    @Override
    protected void startCommandProcessing() {
        /* do nothing! */
        //TODO: no implementation since EventBus is used in a different way for this tests. Should be refactored in parent class.
    }

    @Override
    public void send(Command command, OnFinishedHandler callback) {
        List<Command> answer = transmit(new ArrayList<>(Arrays.asList(command)));
        CommandAndHandler handler = new CommandAndHandler();
        handler.setCommand(command);
        handler.setHandler(callback);
        processResults(answer, new ArrayList<>(Arrays.asList(handler)));
    }

    @Override
    public void send(Command command) {
        send(command, null);
    }

    @Override
    public void listen() {
        //TODO: no implementation since EventBus is used in a different way for this tests. Should be refactored in parent class.
    }

    @Override
    public List<Command> transmit(List<Command> commands) {
        ArrayList<Command> realCommands = new ArrayList<>(commands);
        realCommands.add(new NamedCommand(PlatformConstants.POLL_EVENT_BUS_COMMAND_NAME));
        return dolphinContext.handle(commands);
    }

}
