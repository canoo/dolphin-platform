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
package org.opendolphin.core.server.comm

import org.opendolphin.core.comm.Command

class ActionRegistryTests extends GroovyTestCase {
    ActionRegistry registry

    private class TestDataCommand extends Command {}


    @Override
    protected void setUp() throws Exception {
        registry = new ActionRegistry()
    }

    void testRegisterCommand() {
        assert 0 == registry.actions.size()
        CommandHandler firstAction = new CommandHandler<Command>() {
            @Override
            void handleCommand(Command command) {

            }
        };
        registry.register(TestDataCommand.class, firstAction)
        assert 1 == registry.getActionsFor(TestDataCommand.class).size()
        assert registry.getActionsFor(TestDataCommand.class).contains(firstAction)

        CommandHandler otherAction = new CommandHandler<Command>() {
            @Override
            void handleCommand(Command command) {

            }
        }
        assert 1 == registry.actions.size()
        assert 1 == registry.getActionsFor(TestDataCommand.class).size()
    }

    void testRegisterCommandHandler(){
        CommandHandler<TestDataCommand> commandHandler = new CommandHandler<TestDataCommand>() {

            @Override
            void handleCommand(TestDataCommand command) {

            }
        }
        registry.register(TestDataCommand.class, commandHandler)
        assert registry.getActionsFor(TestDataCommand.class).contains(commandHandler)
        assert 1 == registry.actions.size()
        assert 1 == registry.getActionsFor(TestDataCommand.class).size()
    }

    void testRegisterCommand_MultipleCalls() {
        assert 0 == registry.actions.size()
        def action = new CommandHandler<Command>() {
            @Override
            void handleCommand(Command command) {

            }
        }
        registry.register(TestDataCommand.class, action)
        assert 1 == registry.getActionsFor(TestDataCommand.class).size()

        registry.register(TestDataCommand.class, action)
        assert 1 == registry.getActionsFor(TestDataCommand.class).size()
    }

}
