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
package com.canoo.remoting.server.action;

import com.canoo.communication.common.commands.EmptyNotification;
import com.canoo.remoting.server.communication.ActionRegistry;
import com.canoo.remoting.server.communication.CommandHandler;

import java.util.List;
import java.util.logging.Logger;

/**
 * An action that does nothing on the server.
 * It is only used to hook into the communication at a known point
 * such that the onFinished handler for the command is executed
 * in the expected sequence.
 */
public class EmptyAction implements ServerAction {

    private static final Logger LOG = Logger.getLogger(EmptyAction.class.getName());

    public void registerIn(ActionRegistry registry) {
        registry.register(EmptyNotification.class, new CommandHandler<EmptyNotification>() {
            @Override
            public void handleCommand(EmptyNotification command, List response) {
                LOG.finest("empty action reached - doing nothing on the server");
            }
        });
    }

}
