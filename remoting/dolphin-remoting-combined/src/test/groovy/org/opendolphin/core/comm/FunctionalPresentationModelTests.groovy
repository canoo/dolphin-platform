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
package org.opendolphin.core.comm

import org.opendolphin.LogConfig
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.client.comm.OnFinishedHandler
import org.opendolphin.core.server.*
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry
import org.opendolphin.core.server.comm.CommandHandler

import java.util.concurrent.TimeUnit
import java.util.logging.Level
/**
 * Showcase for how to test an application without the GUI by
 * issuing the respective commands and model changes against the
 * ClientModelStore
 */

class FunctionalPresentationModelTests extends GroovyTestCase {

    private final class CreateCommand extends Command {}
    private final class PerformanceCommand extends Command {}
    private final class CheckNotificationReachedCommand extends Command {}
    private final class JavaCommand extends Command {}
    private final class ArbitraryCommand extends Command {}
    private final class DeleteCommand extends Command {}
    private final class FetchDataCommand extends Command {}
    private final class LoginCommand extends Command {}
    private final class NoSuchActionRegisteredCommand extends Command {}
    private final class Set2Command extends Command {}
    private final class Assert3Command extends Command {}
    private final class CheckTagIsKnownOnServerSideCommand extends Command {}


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
        ClientPresentationModel modelA = new ClientPresentationModel("1", Arrays.asList(new ClientAttribute("a", 0, "QUAL")));
        clientDolphin.getModelStore().add(modelA);

        ClientPresentationModel modelB = new ClientPresentationModel("2", Arrays.asList(new ClientAttribute("b", 0, "QUAL")));
        clientDolphin.getModelStore().add(modelB);

        modelA.getAttribute("a").setValue(1)

        assert modelB.getAttribute("b").getValue() == 1
        context.assertionsDone() // make sure the assertions are really executed
    }

    void testPerformanceWithStandardCommandBatcher() {
        doTestPerformance()
    }

    //TODO: Rewrite
//
//    void testPerformanceWithBlindCommandBatcher() {
//        def batcher = new BlindCommandBatcher(mergeValueChanges: true, deferMillis: 100)
//
//        def connector = new InMemoryClientConnector(context.clientDolphin.modelStore, serverDolphin.serverConnector, batcher, new RunLaterUiThreadHandler())
//        connector.connect(false);
//
//        context.clientDolphin.clientConnector = connector
//        doTestPerformance()
//    }


    void doTestPerformance() {
        long id = 0
        registerAction serverDolphin, PerformanceCommand.class, { cmd ->
            100.times { attr ->
                ServerModelStore.presentationModelCommand(serverDolphin.modelStore.currentResponse, "id_${id++}".toString(), null, new DTO(new Slot("attr_$attr", attr)))
            }
        }
        def start = System.nanoTime()
        100.times { soOften ->
            clientDolphin.getClientConnector().send new PerformanceCommand(), new OnFinishedHandler() {
                @Override
                void onFinished() {
                }
            }
        }
        clientDolphin.getClientConnector().send new PerformanceCommand(), new OnFinishedHandler() {

            @Override
            void onFinished() {
                println((System.nanoTime() - start).intdiv(1_000_000))
                context.assertionsDone() // make sure the assertions are really executed
            }
        }
    }

    void testCreationRoundtripDefaultBehavior() {
        registerAction serverDolphin, CreateCommand.class, { cmd ->
            ServerModelStore.presentationModelCommand(serverDolphin.modelStore.currentResponse, "id".toString(), null, new DTO(new Slot("attr", 'attr')))
        }
        registerAction serverDolphin, CheckNotificationReachedCommand.class, { cmd ->
            assert 1 == serverDolphin.getModelStore().listPresentationModels().size()
            assert serverDolphin.getModelStore().findPresentationModelById("id")
        }

        clientDolphin.getClientConnector().send new CreateCommand(), new OnFinishedHandler() {

            @Override
            void onFinished() {
                clientDolphin.getClientConnector().send new CheckNotificationReachedCommand(), new OnFinishedHandler() {

                    @Override
                    void onFinished() {
                        context.assertionsDone() // make sure the assertions are really executed
                    }
                }
            }
        }
    }

    void testCreationRoundtripForTags() {
        registerAction serverDolphin, CreateCommand.class, { cmd ->
            def NO_TYPE = null
            def NO_QUALIFIER = null
            ServerModelStore.presentationModelCommand(serverDolphin.modelStore.currentResponse, "id".toString(), NO_TYPE, new DTO(new Slot("attr", true, NO_QUALIFIER)))
        }
        registerAction serverDolphin, CheckTagIsKnownOnServerSideCommand.class, { cmd ->
        }

        clientDolphin.getClientConnector().send new CreateCommand(), new OnFinishedHandler() {

            @Override
            void onFinished() {
                clientDolphin.getClientConnector().send new CheckTagIsKnownOnServerSideCommand(), new OnFinishedHandler() {

                    @Override
                    void onFinished() {
                        context.assertionsDone()
                    }
                }
            }
        }
    }

    void testFetchingAnInitialListOfData() {
        registerAction serverDolphin, FetchDataCommand.class, { cmd ->
            ('a'..'z').each {
                DTO dto = new DTO(new Slot('char', it))
                List<Map<String, Object>> list = new ArrayList<>();
                for (Slot slot : dto.getSlots()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("propertyName", slot.getPropertyName());
                    map.put("value", slot.getValue());
                    map.put("qualifier", slot.getQualifier());
                    list.add(map);
                }
                serverDolphin.modelStore.currentResponse.add(new CreatePresentationModelCommand(it, null, list));
            }
        }
        clientDolphin.getClientConnector().send new FetchDataCommand(), new OnFinishedHandler() {

            @Override
            void onFinished() {
                // pmIds from a single action should come in sequence
                assert 'a' == context.clientDolphin.getModelStore().findPresentationModelById('a').getAttribute("char").value
                assert 'z' == context.clientDolphin.getModelStore().findPresentationModelById('z').getAttribute("char").value
                context.assertionsDone() // make sure the assertions are really executed
            }
        }
    }

    public <T extends Command> void registerAction(ServerDolphin serverDolphin, Class<T> commandClass, CommandHandler<T> handler) {
        serverDolphin.getServerConnector().register(new DolphinServerAction() {

            @Override
            void registerIn(ActionRegistry registry) {
                registry.register(commandClass, handler);
            }
        });
    }

    void testLoginUseCase() {
        registerAction serverDolphin, LoginCommand.class, { cmd ->
            def user = context.serverDolphin.getModelStore().findPresentationModelById('user')
            if (user.getAttribute("name").value == 'Dierk' && user.getAttribute("password").value == 'Koenig') {
                ServerModelStore.changeValueCommand(serverDolphin.modelStore.currentResponse, user.getAttribute("loggedIn"), 'true')
            }
        }

        ClientPresentationModel user = new ClientPresentationModel("user", Arrays.asList(new ClientAttribute("name", null), new ClientAttribute("password", null), new ClientAttribute("loggedIn", null)));
        clientDolphin.getModelStore().add(user);

        clientDolphin.getClientConnector().send new LoginCommand(), new OnFinishedHandler() {

            @Override
            void onFinished() {
                assert !user.getAttribute("loggedIn").value
            }
        }
        user.getAttribute("name").value = "Dierk"
        user.getAttribute("password").value = "Koenig"

        clientDolphin.getClientConnector().send new LoginCommand(), new OnFinishedHandler() {

            @Override
            void onFinished() {
                assert user.getAttribute("loggedIn").value
                context.assertionsDone()
            }
        }
    }

    void testUnregisteredCommandWithLog() {
        clientDolphin.getClientConnector().send new NoSuchActionRegisteredCommand(), new OnFinishedHandler() {

            @Override
            void onFinished() {
// unknown actions are silently ignored and logged as warnings on the server side.
            }
        }
        context.assertionsDone()
    }

    void testUnregisteredCommandWithoutLog() {
        clientDolphin.getClientConnector().send(new NoSuchActionRegisteredCommand(), null)
        context.assertionsDone()
    }

    // silly and only for the coverage, we test behavior when id is wrong ...
    void testIdNotFoundInVariousCommands() {
        clientDolphin.clientConnector.send new ValueChangedCommand(attributeId: 0)
        ServerModelStore.changeValueCommand(null, null, null)
        ServerModelStore.changeValueCommand(null, new ServerAttribute('a', 42), 42)
        context.assertionsDone()
    }


    void testActionAndSendJavaLike() {
        boolean reached = false
        registerAction(serverDolphin, JavaCommand.class, new CommandHandler<JavaCommand>() {
            @Override
            void handleCommand(JavaCommand command) {
                reached = true
            }
        });
        clientDolphin.getClientConnector().send(new JavaCommand(), new OnFinishedHandler() {
            @Override
            void onFinished() {
                assert reached
                context.assertionsDone()
            }
        })
    }


    void testRemovePresentationModel() {
        ClientPresentationModel model = new ClientPresentationModel("pm", Arrays.asList(new ClientAttribute("attr", 1)));
        clientDolphin.getModelStore().add(model);

        registerAction serverDolphin, DeleteCommand.class, { cmd ->
            serverDolphin.getModelStore().remove(serverDolphin.getModelStore().findPresentationModelById('pm'))
            assert serverDolphin.getModelStore().findPresentationModelById('pm') == null
        }
        assert clientDolphin.getModelStore().findPresentationModelById('pm')

        clientDolphin.getClientConnector().send new DeleteCommand(), new OnFinishedHandler() {

            @Override
            void onFinished() {
                assert clientDolphin.getModelStore().findPresentationModelById('pm') == null
                context.assertionsDone()
            }
        }
    }


    void testWithNullResponses() {
        ClientPresentationModel model = new ClientPresentationModel("pm", Arrays.asList(new ClientAttribute("attr", 1)));
        clientDolphin.getModelStore().add(model);

        registerAction serverDolphin, ArbitraryCommand.class, { cmd ->
            ServerModelStore.deleteCommand([], null)
            ServerModelStore.deleteCommand([], '')
            ServerModelStore.deleteCommand(null, '')
            ServerModelStore.presentationModelCommand(null, null, null, null)
            ServerModelStore.changeValueCommand([], null, null)
        }
        clientDolphin.getClientConnector().send(new ArbitraryCommand(), new OnFinishedHandler() {

            @Override
            void onFinished() {
                context.assertionsDone()
            }
        });
    }

    //TODO: rewrite

//    void testStateConflictBetweenClientAndServer() {
//        LogConfig.logOnLevel(Level.INFO);
//        def latch = new CountDownLatch(1)
//        ClientPresentationModel pm = new ClientPresentationModel("pm", Arrays.asList(new ClientAttribute("attr", 1)));
//        clientDolphin.getModelStore().add(pm);
//        def attr = pm.getAttribute('attr')
//
//        registerAction serverDolphin, Set2Command.class, { cmd ->
//            latch.await() // mimic a server delay such that the client has enough time to change the value concurrently
//            assert serverDolphin.getModelStore().findPresentationModelById('pm').getAttribute('attr').value == 1
//            serverDolphin.getModelStore().findPresentationModelById('pm').getAttribute('attr').value = 2
//            assert serverDolphin.getModelStore().findPresentationModelById('pm').getAttribute('attr').value == 2 // immediate change of server state
//        }
//        registerAction serverDolphin, Assert3Command.class, { cmd ->
//            assert serverDolphin.getModelStore().findPresentationModelById('pm').getAttribute('attr').value == 3
//        }
//
//        clientDolphin.getClientConnector().send(new Set2Command(), null) // a conflict could arise when the server value is changed ...
//        attr.value = 3            // ... while the client value is changed concurrently
//        latch.countDown()
//
//        clientDolphin.getClientConnector().send(new Assert3Command(), new OnFinishedHandler() {
//
//            @Override
//            void onFinished() {
//                assert attr.value == 3
//                context.assertionsDone()
//            }
//        });
//
//    }

}