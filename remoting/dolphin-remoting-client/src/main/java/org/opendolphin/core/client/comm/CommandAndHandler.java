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
package org.opendolphin.core.client.comm;

import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.EmptyCommand;

public class CommandAndHandler {

    private final Command command;

    private final OnFinishedHandler handler;

    private final HandlerType handlerType;

    @Deprecated
    public CommandAndHandler(final Command command) {
        this(command, null, HandlerType.UI);
    }

    @Deprecated
    public CommandAndHandler(final Command command, final OnFinishedHandler handler) {
        this(command, handler, HandlerType.UI);
    }

    public CommandAndHandler(final Command command, final OnFinishedHandler handler, final HandlerType handlerType) {

        //TODO: null in several groovy tests. Tests need to be reacftored....
        //this.command = Objects.requireNonNull(command, "Command should bot be null");

        //if(handler != null && handlerExecutor == null) {
        //    throw new IllegalArgumentException("A handlerExecutor must be specified for the handler!");
        //}
        this.command = command;
        this.handler = handler;
        this.handlerType = handlerType;
    }

    /**
     * whether this command/handler can be batched
     */
    public boolean isBatchable() {
        if (handler != null) return false;
        if (command instanceof EmptyCommand) return false;
        return true;
    }

    public Command getCommand() {
        return command;
    }

    public OnFinishedHandler getHandler() {
        return handler;
    }

}
