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
package com.canoo.remoting.client.communication;

import com.canoo.communication.common.commands.Command;
import com.canoo.communication.common.commands.EmptyNotification;
import com.canoo.communication.common.commands.NamedCommand;

public class CommandAndHandler {

    private Command command;

    private OnFinishedHandler handler;

    public CommandAndHandler() {
    }

    public CommandAndHandler(Command command, OnFinishedHandler handler) {
        this.command = command;
        this.handler = handler;
    }

    /**
     * whether this command/handler can be batched
     */
    public boolean isBatchable() {
        if (handler != null) return false;
        if (command instanceof NamedCommand) return false;
        if (command instanceof EmptyNotification) return false;
        return true;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public OnFinishedHandler getHandler() {
        return handler;
    }

    public void setHandler(OnFinishedHandler handler) {
        this.handler = handler;
    }

}
