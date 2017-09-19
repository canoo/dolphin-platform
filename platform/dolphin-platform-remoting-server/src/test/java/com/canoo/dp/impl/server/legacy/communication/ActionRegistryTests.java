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
package com.canoo.dp.impl.server.legacy.communication;

import com.canoo.dp.impl.remoting.legacy.commands.Command;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class ActionRegistryTests {

    private ActionRegistry registry;

    private final class TestDataCommand extends Command {
        public TestDataCommand() {
            super(TestDataCommand.class.getSimpleName());
        }
    }

    @BeforeMethod
    public void setUp() throws Exception {
        registry = new ActionRegistry();
    }

    @Test
    public void testRegisterCommand() {
        //given:
        Assert.assertEquals(0, registry.getActions().size());
        CommandHandler<TestDataCommand> firstAction = new CommandHandler<TestDataCommand>() {
            @Override
            public void handleCommand(TestDataCommand command, List response) {

            }

        };

        //when:
        registry.register(TestDataCommand.class, firstAction);

        //then:
        Assert.assertEquals(1, registry.getActionsFor(TestDataCommand.class).size());
        Assert.assertTrue(registry.getActionsFor(TestDataCommand.class).contains(firstAction));
        Assert.assertEquals(1, registry.getActions().size());
    }

    @Test
    public void testRegisterCommandHandler() {

        //given:
        CommandHandler<TestDataCommand> commandHandler = new CommandHandler<TestDataCommand>() {
            @Override
            public void handleCommand(TestDataCommand command, List response) {

            }

        };

        //when:
        registry.register(TestDataCommand.class, commandHandler);

        //then:
        Assert.assertTrue(registry.getActionsFor(TestDataCommand.class).contains(commandHandler));
        Assert.assertEquals(1, registry.getActions().size());
        Assert.assertEquals(1, registry.getActionsFor(TestDataCommand.class).size());
    }

    @Test
    public void testRegisterCommand_MultipleCalls() {

        //given:
        Assert.assertEquals(0, registry.getActions().size());
        CommandHandler<TestDataCommand> action = new CommandHandler<TestDataCommand>() {
            @Override
            public void handleCommand(TestDataCommand command, List response) {

            }

        };

        //when:
        registry.register(TestDataCommand.class, action);

        //then:
        Assert.assertEquals(1, registry.getActions().size());
        Assert.assertEquals(1, registry.getActionsFor(TestDataCommand.class).size());

        //when:
        registry.register(TestDataCommand.class, action);

        //then:
        Assert.assertEquals(1, registry.getActionsFor(TestDataCommand.class).size());
    }

}
