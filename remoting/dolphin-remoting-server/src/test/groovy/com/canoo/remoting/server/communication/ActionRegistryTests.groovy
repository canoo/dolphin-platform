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
package com.canoo.remoting.server.communication

import com.canoo.communication.common.commands.AttributeCreatedNotification
import com.canoo.communication.common.commands.Command
import com.canoo.communication.common.commands.DataCommand
import com.canoo.communication.common.commands.NamedCommand

class ActionRegistryTests extends GroovyTestCase {
    ActionRegistry registry

    @Override
    protected void setUp() throws Exception {
        registry = new ActionRegistry()
    }

    void testRegisterCommand() {
        assert 0 == registry.actions.size()
        CommandHandler firstAction = new CommandHandler<Command>() {
            @Override
            void handleCommand(Command command, List response) {

            }
        };
        registry.register('Data', firstAction)
        assert 1 == registry.getAt('Data').size()
        assert registry.getAt('Data').contains(firstAction)

        CommandHandler otherAction = new CommandHandler<Command>() {
            @Override
            void handleCommand(Command command, List<Command> response) {

            }
        }
        registry.register(DataCommand, otherAction)
        registry.register(AttributeCreatedNotification, otherAction)
        assert 2 == registry.actions.size()
        assert 2 == registry.getAt('Data').size()
        assert 1 == registry.getAt('AttributeCreated').size()
    }

    void testRegisterCommandHandler(){
        TestSimpleCommandHandler commandHandler = new TestSimpleCommandHandler()
        registry.register('Data', commandHandler)
        assert registry.getAt('Data').contains(commandHandler)
        registry.register(DataCommand, new TestSimpleCommandHandler())
        assert 1 == registry.actions.size()
        assert 2 == registry.getAt('Data').size()
    }

    void testUnregisterCommandHandler() {
        TestSimpleCommandHandler commandHandler = new TestSimpleCommandHandler()
        registry.register('Data',commandHandler)
        assert 1 == registry.getAt('Data').size()
        registry.unregister('Data',commandHandler)
        assert 0 == registry.getAt('Data').size()
        registry.register(DataCommand, commandHandler)
        assert 1 == registry.getAt('Data').size()
        registry.unregister(DataCommand,commandHandler)
        assert 0 == registry.getAt('Data').size()
    }

    void testUnregisterCommand() {
        CommandHandler action = new CommandHandler<Command>() {
            @Override
            void handleCommand(Command command, List<Command> response) {

            }
        };
        registry.register('Data',action)
        assert 1 == registry.getAt('Data').size()
        registry.unregister('Data',action)
        assert 0 == registry.getAt('Data').size()
        registry.register(DataCommand, action)
        assert 1 == registry.getAt('Data').size()
        registry.unregister(DataCommand,action)
        assert 0 == registry.getAt('Data').size()
    }

    void testRegisterCommand_MultipleCalls() {
        assert 0 == registry.actions.size()
        def action = new CommandHandler<NamedCommand>() {
            @Override
            void handleCommand(NamedCommand command, List<Command> response) {

            }
        }
        registry.register('Data', action)
        assert 1 == registry.getAt('Data').size()

        registry.register('Data', action)
        assert 1 == registry.getAt('Data').size()
    }

}
