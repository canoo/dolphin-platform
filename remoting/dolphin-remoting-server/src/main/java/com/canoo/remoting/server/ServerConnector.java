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
package com.canoo.remoting.server;

import com.canoo.communication.common.codec.Codec;
import com.canoo.communication.common.commands.Command;
import com.canoo.communication.common.commands.SignalCommand;
import com.canoo.remoting.server.action.DolphinServerAction;
import com.canoo.remoting.server.action.ServerAction;
import com.canoo.remoting.server.communication.ActionRegistry;
import com.canoo.remoting.server.communication.CommandHandler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerConnector {
    /**
     * doesn't fail on missing commands
     **/
    public List<Command> receive(Command command) {
        LOG.info("S:     received " + command);
        List<Command> response = new LinkedList();// collecting parameter pattern

        if (!(command instanceof SignalCommand)) {// signal commands must not update thread-confined state
            for (DolphinServerAction it : dolphinServerActions) {
                it.setDolphinResponse(response);// todo: can be deleted as soon as all action refer to the SMS
            }

            serverModelStore.setCurrentResponse(response);
        }


        List<CommandHandler> actions = registry.getAt(command.getId());
        if (actions.isEmpty()) {
            LOG.warning("S: there is no server action registered for received command: " + String.valueOf(command) + ", known commands are " + String.valueOf(registry.getActions().keySet()));
            return response;
        }

        // copying the list of actions allows an Action to unregister itself
        // avoiding ConcurrentModificationException to be thrown by the loop
        List<CommandHandler> actionsCopy = new ArrayList<CommandHandler>();
        ((ArrayList<CommandHandler>) actionsCopy).addAll(actions);
        try {
            for (CommandHandler action : actionsCopy) {
                action.handleCommand(command, response);
            }

        } catch (Exception exception) {
            LOG.log(Level.SEVERE, "S: an error ocurred while processing " + command, exception);
            throw exception;
        }

        return response;
    }

    public void register(ServerAction action) {
        if (action instanceof DolphinServerAction) {
            // static type checker complains if no explicit cast
            dolphinServerActions.add((DolphinServerAction) action);
        }
        action.registerIn(registry);
    }

    private static final Logger LOG = Logger.getLogger(ServerConnector.class.getName());
    private Codec codec;
    private ServerModelStore serverModelStore;
    private ActionRegistry registry = new ActionRegistry();
    private List<DolphinServerAction> dolphinServerActions = new ArrayList<DolphinServerAction>();

    public Codec getCodec() {
        return codec;
    }

    public void setCodec(Codec codec) {
        this.codec = codec;
    }

    public void setServerModelStore(ServerModelStore serverModelStore) {
        this.serverModelStore = serverModelStore;
    }

    public ActionRegistry getRegistry() {
        return registry;
    }

    /**
     * Hack that is used in old groovy unit test
     * @param logLevel
     */
    public void setLogLevel(Level logLevel) {
        LOG.setLevel(logLevel);
    }
}
