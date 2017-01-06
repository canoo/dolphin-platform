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
package com.canoo.implementation.dolphin.test.spring;

import com.canoo.implementation.dolphin.PlatformConstants;
import com.canoo.implementation.dolphin.server.context.DolphinContext;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.comm.AbstractClientConnector;
import org.opendolphin.core.client.comm.CommandAndHandler;
import org.opendolphin.core.client.comm.OnFinishedHandler;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.NamedCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hendrikebbers on 01.11.16.
 */
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
