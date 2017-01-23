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
package com.canoo.remoting.combined;

import com.canoo.remoting.client.communication.CommandAndHandler;
import com.canoo.remoting.client.communication.OnFinishedHandler;
import com.canoo.remoting.client.ClientDolphin;
import com.canoo.communication.common.commands.Command;
import com.canoo.remoting.server.ServerConnector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An in-memory client connector without any asynchronous calls such that
 * technologies like GWT, Vaadin, or ULC can use it safely on the server side
 * without leaving the request thread.
 * It may also be useful for unit-testing purposes.
 */
public class SynchronousInMemoryClientConnector extends InMemoryClientConnector {

    public SynchronousInMemoryClientConnector(ClientDolphin clientDolphin, ServerConnector serverConnector) {
        super(clientDolphin, serverConnector);
    }

    protected void startCommandProcessing() {
        /* do nothing! */
    }

    public void send(Command command, OnFinishedHandler callback) {
        List<Command> answer = transmit(new ArrayList(Arrays.asList(command)));
        CommandAndHandler handler = new CommandAndHandler();
        handler.setCommand(command);
        handler.setHandler(callback);
        processResults(answer, new ArrayList(Arrays.asList(handler)));
    }

    public void send(Command command) {
        send(command, null);
    }

}
