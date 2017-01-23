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

import com.canoo.remoting.client.communication.BlindCommandBatcher
import com.canoo.remoting.client.communication.OnFinishedData
import com.canoo.remoting.client.communication.OnFinishedHandler
import com.canoo.remoting.client.communication.RunLaterUiThreadHandler
import com.canoo.remoting.client.communication.UiThreadHandler
import com.canoo.remoting.server.DTO
import com.canoo.remoting.server.DefaultServerDolphin
import com.canoo.remoting.server.ServerAttribute
import com.canoo.remoting.server.ServerDolphin
import com.canoo.remoting.server.Slot
import com.canoo.communication.common.PresentationModel
import com.canoo.communication.common.LogConfig
import com.canoo.remoting.client.ClientAttribute
import com.canoo.remoting.client.ClientDolphin
import com.canoo.remoting.client.ClientPresentationModel
import com.canoo.communication.common.commands.Command
import com.canoo.communication.common.commands.DataCommand
import com.canoo.communication.common.commands.NamedCommand
import com.canoo.communication.common.commands.ValueChangedCommand
import com.canoo.remoting.server.action.DolphinServerAction
import com.canoo.remoting.server.communication.ActionRegistry
import com.canoo.remoting.server.communication.CommandHandler

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.logging.Level

/**
 * Showcase for how to test an application without the GUI by
 * issuing the respective commands and model changes against the
 * ClientModelStore
 */

class FunctionalPresentationModelTests extends GroovyTestCase {

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

    void testQualifiersInClientPMs() {
        PresentationModel modelA = clientDolphin.presentationModel("1", new ClientAttribute("a", 0, "QUAL"))
        PresentationModel modelB = clientDolphin.presentationModel("2", new ClientAttribute("b", 0, "QUAL"))

        modelA.getAttribute("a").setValue(1)

        assert modelB.getAttribute("b").getValue() == 1
        context.assertionsDone() // make sure the assertions are really executed
    }

    void testPerformanceWithStandardCommandBatcher() {
        doTestPerformance()
    }

    void testPerformanceWithBlindCommandBatcher() {
        def batcher = new BlindCommandBatcher(mergeValueChanges: true, deferMillis: 100)
        def connector = new InMemoryClientConnector(context.clientDolphin, serverDolphin.serverConnector, batcher)
        connector.uiThreadHandler = new RunLaterUiThreadHandler()
        context.clientDolphin.clientConnector = connector
        doTestPerformance()
    }

    void testPerformanceWithSynchronousConnector() {
        def connector = new SynchronousInMemoryClientConnector(context.clientDolphin, serverDolphin.serverConnector)
        connector.uiThreadHandler = { fail "should not reach here! " } as UiThreadHandler
        context.clientDolphin.clientConnector = connector
        doTestPerformance()
    }

    void doTestPerformance() {
        long id = 0
        registerAction serverDolphin, "performance", { cmd, response ->
            100.times { attr ->
                serverDolphin.presentationModelCommand(response, "id_${id++}".toString(), null, new DTO(new Slot("attr_$attr", attr)))
            }
        }
        def start = System.nanoTime()
        100.times { soOften ->
            clientDolphin.send "performance", new OnFinishedHandler() {
                @Override
                void onFinished(List<ClientPresentationModel> presentationModels) {
                    assert presentationModels.size() == 100
                    presentationModels.each { pm -> clientDolphin.delete(pm) }
                }
            }
        }
        clientDolphin.send "performance", new OnFinishedHandler() {

            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                assert presentationModels.size() == 100
                println((System.nanoTime() - start).intdiv(1_000_000))
                context.assertionsDone() // make sure the assertions are really executed
            }
        }
    }

    void testCreationRoundtripDefaultBehavior() {
        registerAction serverDolphin, "create", { cmd, response ->
            serverDolphin.presentationModelCommand(response, "id".toString(), null, new DTO(new Slot("attr", 'attr')))
        }
        registerAction serverDolphin, "checkNotificationReached", { cmd, response ->
            assert 1 == serverDolphin.listPresentationModels().size()
            assert serverDolphin.getPresentationModel("id")
        }

        clientDolphin.send "create", new OnFinishedHandler() {

            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                assert presentationModels.size() == 1
                assert 'attr' == presentationModels.first().getAttribute("attr").value
                clientDolphin.send "checkNotificationReached", new OnFinishedHandler() {

                    @Override
                    void onFinished(List<ClientPresentationModel> pms) {
                        context.assertionsDone() // make sure the assertions are really executed
                    }
                }
            }
        }
    }

    void testCreationRoundtripForTags() {
        registerAction serverDolphin, "create", { cmd, response ->
            def NO_TYPE = null
            def NO_QUALIFIER = null
            serverDolphin.presentationModelCommand(response, "id".toString(), NO_TYPE, new DTO(new Slot("attr", true, NO_QUALIFIER)))
        }
        registerAction serverDolphin, "checkTagIsKnownOnServerSide", { cmd, response ->
        }

        clientDolphin.send "create", new OnFinishedHandler() {

            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                clientDolphin.send "checkTagIsKnownOnServerSide", new OnFinishedHandler() {

                    @Override
                    void onFinished(List<ClientPresentationModel> pms) {
                        context.assertionsDone()
                    }
                }
            }
        }
    }

    void testFetchingAnInitialListOfData() {
        registerAction serverDolphin, "fetchData", { cmd, response ->
            ('a'..'z').each {
                DTO dto = new DTO(new Slot('char', it))
                // sending CreatePresentationModelCommand _without_ adding the pm to the server model store
                serverDolphin.presentationModelCommand(response, it, null, dto)
            }
        }
        clientDolphin.send "fetchData", new OnFinishedHandler() {

            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                assert presentationModels.size() == 26
                assert presentationModels.collect { it.id }.sort(false) == presentationModels.collect { it.id }
                // pmIds from a single action should come in sequence
                assert 'a' == context.clientDolphin.getPresentationModel('a').getAttribute("char").value
                assert 'z' == context.clientDolphin.getPresentationModel('z').getAttribute("char").value
                context.assertionsDone() // make sure the assertions are really executed
            }
        }
    }

    void registerAction(ServerDolphin serverDolphin, String name, CommandHandler<NamedCommand> handler) {
        serverDolphin.register(new DolphinServerAction() {

            @Override
            void registerIn(ActionRegistry registry) {
                registry.register(name, handler);
            }
        });
    }

    void testLoginUseCase() {
        registerAction serverDolphin, "loginCmd", { cmd, response ->
            def user = context.serverDolphin.getPresentationModel('user')
            if (user.getAttribute("name").value == 'Dierk' && user.getAttribute("password").value == 'Koenig') {
                DefaultServerDolphin.changeValueCommand(response, user.getAttribute("loggedIn"), 'true')
            }
        }
        def user = clientDolphin.presentationModel 'user', name: null, password: null, loggedIn: null
        clientDolphin.send "loginCmd", new OnFinishedHandler() {

            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                assert !user.getAttribute("loggedIn").value
            }
        }
        user.getAttribute("name").value = "Dierk"
        user.getAttribute("password").value = "Koenig"

        clientDolphin.send "loginCmd", new OnFinishedHandler() {

            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                assert user.getAttribute("loggedIn").value
                context.assertionsDone()
            }
        }
    }

    void testAsynchronousExceptionOnTheServer() {
        LogConfig.logOnLevel(Level.INFO);
        def count = 0
        clientDolphin.clientConnector.onException = { count++ }

        registerAction serverDolphin, "someCmd", { cmd, response ->
            throw new RuntimeException("EXPECTED: some arbitrary exception on the server")
        }

        clientDolphin.send "someCmd", new OnFinishedHandler() {

            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                fail "the onFinished handler will not be reached in this case"
            }
        }
        clientDolphin.sync {
            assert count == 1
        }

        // provoke a second exception
        clientDolphin.send "someCmd", new OnFinishedHandler() {

            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                fail "the onFinished handler will not be reached either"
            }
        }
        clientDolphin.sync {
            assert count == 2
        }
        clientDolphin.sync {
            context.assertionsDone()
        }
    }

    void testAsynchronousExceptionInOnFinishedHandler() {

        clientDolphin.clientConnector.uiThreadHandler = { it() } as UiThreadHandler
        // not "run later" we need it immediately here
        clientDolphin.clientConnector.onException = { context.assertionsDone() }

        registerAction serverDolphin, "someCmd", { cmd, response ->
            // nothing to do
        }
        clientDolphin.send "someCmd", new OnFinishedHandler() {

            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                throw new RuntimeException("EXPECTED: some arbitrary exception in the onFinished handler")
            }
        }
    }

    void testUnregisteredCommandWithLog() {
        serverDolphin.serverConnector.setLogLevel(Level.ALL);
        clientDolphin.send "no-such-action-registered", new OnFinishedHandler() {

            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
// unknown actions are silently ignored and logged as warnings on the server side.
            }
        }
        context.assertionsDone()
    }

    void testUnregisteredCommandWithoutLog() {
        serverDolphin.serverConnector.setLogLevel(Level.OFF);
        clientDolphin.send "no-such-action-registered"
        context.assertionsDone()
    }

    // silly and only for the coverage, we test behavior when id is wrong ...
    void testIdNotFoundInVariousCommands() {
        clientDolphin.clientConnector.send new ValueChangedCommand(attributeId: 0)
        DefaultServerDolphin.changeValueCommand(null, null, null)
        DefaultServerDolphin.changeValueCommand(null, new ServerAttribute('a', 42), 42)
        context.assertionsDone()
    }

    void testDataRequest() {
        registerAction serverDolphin, "myData", { cmd, resp ->
            resp << new DataCommand([a: 1, b: 2])
        }
        clientDolphin.send("myData", new OnFinishedData() {
            @Override
            void onFinishedData(List<Map> data) {
                assert data.size() == 1
                assert data[0].a == 1
                assert data[0].b == 2
                context.assertionsDone()
            }

            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {

            }
        });
    }

    void testActionAndSendJavaLike() {
        boolean reached = false
        registerAction(serverDolphin, "java", new CommandHandler<NamedCommand>() {
            @Override
            void handleCommand(NamedCommand command, List<Command> response) {
                reached = true
            }
        });
        clientDolphin.send("java", new OnFinishedHandler() {
            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                assert reached
                context.assertionsDone()
            }
        })
    }

    void testRemovePresentationModel() {
        clientDolphin.presentationModel('pm', attr: 1)

        registerAction serverDolphin, 'delete', { cmd, response ->
//            serverDolphin.delete(response, serverDolphin['pm']) // deprecated
            serverDolphin.removePresentationModel(serverDolphin.getPresentationModel('pm'))
            assert serverDolphin.getPresentationModel('pm') == null
        }
        assert clientDolphin.getPresentationModel('pm')

        clientDolphin.send 'delete', new OnFinishedHandler() {

            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                assert clientDolphin.getPresentationModel('pm') == null
                context.assertionsDone()
            }
        }
    }

    void testWithNullResponses() {
        clientDolphin.presentationModel('pm', attr: 1)

        registerAction serverDolphin, 'arbitrary', { cmd, response ->
            serverDolphin.deleteCommand([], null)
            serverDolphin.deleteCommand([], '')
            serverDolphin.deleteCommand(null, '')
            serverDolphin.presentationModelCommand(null, null, null, null)
            serverDolphin.changeValueCommand([], null, null)
        }
        clientDolphin.send('arbitrary', new OnFinishedHandler() {

            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                context.assertionsDone()
            }
        });
    }

    void testStateConflictBetweenClientAndServer() {
        LogConfig.logOnLevel(Level.INFO);
        def latch = new CountDownLatch(1)
        def pm = clientDolphin.presentationModel('pm', attr: 1)
        def attr = pm.getAttribute('attr')

        registerAction serverDolphin, 'set2', { cmd, response ->
            latch.await() // mimic a server delay such that the client has enough time to change the value concurrently
            serverDolphin.getPresentationModel('pm').getAttribute('attr').value == 1
            serverDolphin.getPresentationModel('pm').getAttribute('attr').value = 2
            serverDolphin.getPresentationModel('pm').getAttribute('attr').value == 2 // immediate change of server state
        }
        registerAction serverDolphin, 'assert3', { cmd, response ->
            assert serverDolphin.getPresentationModel('pm').getAttribute('attr').value == 3
        }

        clientDolphin.send('set2') // a conflict could arise when the server value is changed ...
        attr.value = 3            // ... while the client value is changed concurrently
        latch.countDown()
        clientDolphin.send('assert3')
        // since from the client perspective, the last change was to 3, server and client should both see the 3

        // in between these calls a conflicting value change could be transferred, setting both value to 2

        clientDolphin.send('assert3', new OnFinishedHandler() {

            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                assert attr.value == 3
                context.assertionsDone()
            }
        });

    }

}