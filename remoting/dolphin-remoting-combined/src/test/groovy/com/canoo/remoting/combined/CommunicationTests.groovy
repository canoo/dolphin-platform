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
import com.canoo.remoting.client.ClientAttribute
import com.canoo.remoting.client.ClientDolphin
import com.canoo.remoting.client.ClientModelStore
import com.canoo.remoting.client.ClientPresentationModel
import com.canoo.remoting.client.communication.AbstractClientConnector
import com.canoo.communication.common.commands.Command
import com.canoo.communication.common.commands.CreatePresentationModelCommand
import com.canoo.communication.common.commands.NamedCommand
import com.canoo.communication.common.commands.ValueChangedCommand
import com.canoo.remoting.server.ServerConnector
import com.canoo.remoting.server.communication.CommandHandler

import java.util.concurrent.TimeUnit
import java.util.logging.Level

/**
 * Tests for the sequence between client requests and server responses.
 * They are really more integration tests than unit tests.
 */

class CommunicationTests extends GroovyTestCase {

	ServerConnector     serverConnector
	AbstractClientConnector clientConnector
    ClientModelStore    clientModelStore
    ClientDolphin       clientDolphin
    TestInMemoryConfig config

    @Override
	protected void setUp() {
		LogConfig.logOnLevel(Level.INFO);
		config = new TestInMemoryConfig()
        serverConnector  = config.serverDolphin.serverConnector
        clientConnector  = config.clientDolphin.clientConnector
        clientModelStore = config.clientDolphin.clientModelStore
        clientDolphin    = config.clientDolphin
	}

    @Override
    protected void tearDown() {
        assert config.done.await(2, TimeUnit.SECONDS)
    }

	void testSimpleAttributeChangeIsVisibleOnServer() {
		def ca  = new ClientAttribute('name', null)
        def cpm = new ClientPresentationModel('model', [ca])
        clientModelStore.add cpm

		Command receivedCommand = null
		def testServerAction = new CommandHandler<ValueChangedCommand>() {

            @Override
            void handleCommand(ValueChangedCommand command, List<Command> response) {
                receivedCommand = command
            }
        }
		serverConnector.registry.register ValueChangedCommand, testServerAction
		ca.value = 'initial'

        clientDolphin.sync {
            assert receivedCommand
            assert receivedCommand.id == 'ValueChanged'
            assert receivedCommand in ValueChangedCommand
            assert receivedCommand.oldValue == null
            assert receivedCommand.newValue == 'initial'
            config.assertionsDone()
        }
	}

	void testServerIsNotifiedAboutNewAttributesAndTheirPms() {

		Command receivedCommand = null
		def testServerAction = new CommandHandler<CreatePresentationModelCommand>() {

            @Override
            void handleCommand(CreatePresentationModelCommand command, List<Command> response) {
                receivedCommand = command
            }
        }
		serverConnector.registry.register CreatePresentationModelCommand, testServerAction

        clientModelStore.add new ClientPresentationModel('testPm', [new ClientAttribute('name', null)])

        clientDolphin.sync {
            assert receivedCommand.id == "CreatePresentationModel"
            assert receivedCommand instanceof CreatePresentationModelCommand
            assert receivedCommand.pmId == 'testPm'
            assert receivedCommand.attributes.name
            config.assertionsDone()
        }
	}

	void testWhenServerChangesValueThisTriggersUpdateOnClient() {
		def ca = new ClientAttribute('name', null)

		def setValueAction = new CommandHandler<CreatePresentationModelCommand>() {

            @Override
            void handleCommand(CreatePresentationModelCommand command, List<Command> response) {
                response << new ValueChangedCommand(
                        attributeId: command.attributes.id.first(),
                        newValue: "set from server",
                        oldValue: null
                )
            }
        };

		Command receivedCommand = null
		def valueChangedAction = new CommandHandler<ValueChangedCommand>() {

            @Override
            void handleCommand(ValueChangedCommand command, List<Command> response) {
                receivedCommand = command
                clientDolphin.sync {                            // there is no onFinished for value changes, so we have to do it here
                    assert ca.value == "set from server"        // client is updated
                    assert receivedCommand.attributeId == ca.id // client notified server about value change
                    config.assertionsDone()
                }
            }
        };

        serverConnector.registry.register CreatePresentationModelCommand, setValueAction
		serverConnector.registry.register ValueChangedCommand, valueChangedAction

        clientModelStore.add new ClientPresentationModel('testPm', [ca]) // trigger the whole cycle
    }

	void testRequestingSomeGeneralCommandExecution() {
		boolean reached = false
		serverConnector.registry.register("ButtonAction", new CommandHandler<NamedCommand>(){

            @Override
            void handleCommand(NamedCommand command, List response) {
                reached = true
            }
        });
		clientConnector.send(new NamedCommand(id: "ButtonAction"))

        clientDolphin.sync {
            assert reached
            config.assertionsDone()
        }
	}

}
