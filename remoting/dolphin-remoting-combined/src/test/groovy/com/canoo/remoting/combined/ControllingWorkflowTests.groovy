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
package com.canoo.remoting.combined
import com.canoo.communication.common.LogConfig
import com.canoo.remoting.client.ClientDolphin
import com.canoo.communication.common.commands.CallNamedActionCommand
import com.canoo.communication.common.commands.NamedCommand
import com.canoo.remoting.server.DefaultServerDolphin
import com.canoo.remoting.server.ServerDolphin
import com.canoo.remoting.server.action.DolphinServerAction
import com.canoo.remoting.server.communication.ActionRegistry
import com.canoo.remoting.server.communication.CommandHandler

import java.util.concurrent.TimeUnit
import java.util.logging.Level
/**
 * Showcase for how to set up a workflow on the server side where a series of actions
 * depend on each other and the effect that an earlier action had on the model store.
 */

class ControllingWorkflowTests extends GroovyTestCase {

    volatile TestInMemoryConfig context
    DefaultServerDolphin serverDolphin
    ClientDolphin clientDolphin

    @Override
    protected void setUp() {
        context = new TestInMemoryConfig()
        serverDolphin = context.serverDolphin
        clientDolphin = context.clientDolphin
        LogConfig.logOnLevel(Level.OFF);
    }

    @Override
    protected void tearDown() {
        assert context.done.await(10, TimeUnit.SECONDS)
    }

    void registerAction(ServerDolphin serverDolphin, String name, CommandHandler<NamedCommand> handler) {
        serverDolphin.register(new DolphinServerAction() {

            @Override
            void registerIn(ActionRegistry registry) {
                registry.register(name, handler);
            }
        });
    }


    void testABCSequentialWorkflow() {

        final ACTION_A          = "A"
        final ACTION_B          = "B"
        final ACTION_C          = "C"

        registerAction(serverDolphin, (ACTION_A), { cmd, response ->
            println "in $ACTION_A"
            response << new CallNamedActionCommand(ACTION_B)
        });
        registerAction(serverDolphin, (ACTION_B), { cmd, response ->
            println "in $ACTION_B"
            response << new CallNamedActionCommand(ACTION_C)
        });
        registerAction(serverDolphin, (ACTION_C), { cmd, response ->
            println "in $ACTION_C"
            context.assertionsDone()
            // the last step in the sequence has nothing to do
        });

        clientDolphin.send(ACTION_A)

    }

    void testABCBroadcastWorkflow() {

        final ACTION_A          = "A"
        final ACTION_B          = "B"
        final ACTION_C          = "C"

        boolean reachedB = false

        registerAction(serverDolphin, (ACTION_A), { cmd, response ->
            println "in $ACTION_A"
            response << new CallNamedActionCommand(ACTION_B)
            response << new CallNamedActionCommand(ACTION_C)
        });
        registerAction(serverDolphin, (ACTION_B), { cmd, response ->
            println "in $ACTION_B"
            reachedB = true
        });
        registerAction(serverDolphin, (ACTION_C), { cmd, response ->
            println "in $ACTION_C"
            assert reachedB
            context.assertionsDone()
            // the last step in the sequence has nothing to do
        });

        clientDolphin.send(ACTION_A)

    }
}
