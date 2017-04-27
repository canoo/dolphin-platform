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

import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.client.comm.OnFinishedHandler
import org.opendolphin.core.server.*
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry
import org.opendolphin.core.server.comm.CommandHandler

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.util.concurrent.TimeUnit
/**
 * Functional tests for scenarios that customers observed when controlling
 * the application purely from server side.
 */

class ServerControlledFunctionalTests extends GroovyTestCase {

    private final class CreatePmCommand extends Command {}
    private final class CheckPmIsThereCommand extends Command {}
    private final class DeleteAndRecreateCommand extends Command {}
    private final class AssertRetainedServerStateCommand extends Command {}
    private final class ChangeValueMultipleTimesAndBackToBaseCommand extends Command {}
    private final class RemoveCommand extends Command {}
    private final class SetAndUnsetQualifierCommand extends Command {}
    private final class SetQualifierCommand extends Command {}



    volatile TestInMemoryConfig context
    DefaultServerDolphin serverDolphin
    ClientDolphin clientDolphin

    @Override
    protected void setUp() {
        context = new TestInMemoryConfig()
        serverDolphin = context.serverDolphin
        clientDolphin = context.clientDolphin
    }

    @Override
    protected void tearDown() {
        assert context.done.await(20, TimeUnit.SECONDS)
    }

    public <T extends Command> void registerAction(ServerDolphin serverDolphin, Class<T> commandType, CommandHandler<T> handler) {
        serverDolphin.getServerConnector().register(new DolphinServerAction() {

            @Override
            void registerIn(ActionRegistry registry) {
                registry.register(commandType, handler);
            }
        });
    }

    void testPMsWereDeletedAndRecreated() {
        // a pm created on the client side
        ClientPresentationModel model = new ClientPresentationModel("pm1", Arrays.asList(new ClientAttribute("a", 0)));
        clientDolphin.getModelStore().add(model);

        registerAction (serverDolphin, RemoveCommand.class, { cmd ->
            assert serverDolphin.getModelStore().findPresentationModelById("pm1").getAttribute("a").value == 0
            assert clientDolphin.getModelStore().findPresentationModelById("pm1").getAttribute("a").value == 0

            serverDolphin.getModelStore().remove(serverDolphin.getModelStore().findPresentationModelById("pm1"));
        });

        clientDolphin.getClientConnector().send(new RemoveCommand());


        clientDolphin.sync(new Runnable() {
            @Override
            void run() {
                ClientPresentationModel model2 = new ClientPresentationModel("pm1", Arrays.asList(new ClientAttribute("a", 1)));
                clientDolphin.getModelStore().add(model2);

                registerAction (serverDolphin, CheckPmIsThereCommand.class, { cmd ->
                    assert serverDolphin.getModelStore().findPresentationModelById("pm1").getAttribute("a").value == 1
                    assert clientDolphin.getModelStore().findPresentationModelById("pm1").getAttribute("a").value == 1
                    context.assertionsDone()
                });

                clientDolphin.getClientConnector().send(new CheckPmIsThereCommand(), null);
            }
        });
    }


    void testPMsWereCreatedOnServerSideDeletedByTypeRecreatedOnServer() { // the "Baerbel" problem
        registerAction( serverDolphin, CreatePmCommand.class, { cmd ->
            serverDolphin.getModelStore().presentationModel(null, "myType", new DTO(new Slot('a',0)))
        });
        registerAction( serverDolphin, DeleteAndRecreateCommand.class, { cmd ->
            List<ServerPresentationModel> toDelete = new ArrayList<>();
            for(ServerPresentationModel model : serverDolphin.getModelStore().findAllPresentationModelsByType("myType")) {
                toDelete.add(model);
            }
            for(ServerPresentationModel model : toDelete) {
                serverDolphin.getModelStore().remove(model);
            }

            serverDolphin.getModelStore().presentationModel(null, "myType", new DTO(new Slot('a',0))) // recreate
            serverDolphin.getModelStore().presentationModel(null, "myType", new DTO(new Slot('a',1))) // recreate

            assert serverDolphin.getModelStore().findAllPresentationModelsByType("myType").size() == 2
            assert serverDolphin.getModelStore().findAllPresentationModelsByType("myType")[0].getAttribute("a").value == 0
            assert serverDolphin.getModelStore().findAllPresentationModelsByType("myType")[1].getAttribute("a").value == 1
        });

        registerAction( serverDolphin, AssertRetainedServerStateCommand.class, { cmd ->
            assert serverDolphin.getModelStore().findAllPresentationModelsByType("myType").size() == 2
            assert serverDolphin.getModelStore().findAllPresentationModelsByType("myType")[0].getAttribute("a").value == 0
            assert serverDolphin.getModelStore().findAllPresentationModelsByType("myType")[1].getAttribute("a").value == 1
            context.assertionsDone()
        });

        clientDolphin.getClientConnector().send(new CreatePmCommand(), new OnFinishedHandler() {
            @Override
            void onFinished() {
                assert clientDolphin.getModelStore().findAllPresentationModelsByType("myType").size() == 1
                assert clientDolphin.getModelStore().findAllPresentationModelsByType("myType").first().getAttribute("a").value == 0
            }
        });
        clientDolphin.getClientConnector().send(new DeleteAndRecreateCommand(), new OnFinishedHandler() {
            @Override
            void onFinished() {
                assert clientDolphin.getModelStore().findAllPresentationModelsByType("myType").size() == 2
                assert clientDolphin.getModelStore().findAllPresentationModelsByType("myType")[0].getAttribute("a").value == 0
                assert clientDolphin.getModelStore().findAllPresentationModelsByType("myType")[1].getAttribute("a").value == 1
            }
        });
        clientDolphin.getClientConnector().send(new AssertRetainedServerStateCommand(), null)
    }

    void testChangeValueMultipleTimesAndBackToBase() { // Alex issue
        // register a server-side action that creates a PM
        registerAction( serverDolphin, (CreatePmCommand.class), { cmd ->
            serverDolphin.getModelStore().presentationModel("myPm", null, new DTO(new Slot('a',0)))
        });
        registerAction( serverDolphin, ChangeValueMultipleTimesAndBackToBaseCommand.class, { cmd ->
            def myPm = serverDolphin.getModelStore().findPresentationModelById("myPm")
            myPm.getAttribute("a").value = 1
            myPm.getAttribute("a").value = 2
            myPm.getAttribute("a").value = 0
        });

        clientDolphin.getClientConnector().send(new CreatePmCommand(), null)
        clientDolphin.getClientConnector().send(new ChangeValueMultipleTimesAndBackToBaseCommand(), new OnFinishedHandler() {
            @Override
            void onFinished() {
                def myPm = clientDolphin.getModelStore().findPresentationModelById("myPm")
                assert myPm.getAttribute("a").value == 0
                context.assertionsDone()
            }
        });
    }

    void testServerSideRemove() {
        registerAction(serverDolphin, CreatePmCommand.class, { cmd ->
            serverDolphin.getModelStore().presentationModel("myPm", null, new DTO(new Slot('a',0)))
        });
        registerAction(serverDolphin, RemoveCommand.class, { cmd ->
            def myPm = serverDolphin.getModelStore().findPresentationModelById("myPm")
            serverDolphin.getModelStore().remove(myPm)
            assert null == serverDolphin.getModelStore().findPresentationModelById("myPm")
        });

        clientDolphin.getClientConnector().send(new CreatePmCommand(), new OnFinishedHandler() {
            @Override
            void onFinished() {
                assert clientDolphin.getModelStore().findPresentationModelById("myPm")
            }
        });
        clientDolphin.getClientConnector().send(new RemoveCommand(), new OnFinishedHandler() {
            @Override
            void onFinished() {
                assert null == clientDolphin.getModelStore().findPresentationModelById("myPm")
                context.assertionsDone()
            }
        });
    }

    void testServerSideSetAndUnsetQualifier() {
        registerAction(serverDolphin, CreatePmCommand.class, { cmd ->
            serverDolphin.getModelStore().presentationModel(null, "myType", new DTO(new Slot('a',0)))
        });
        registerAction(serverDolphin, SetAndUnsetQualifierCommand.class, { cmd ->
            def myPm = serverDolphin.getModelStore().findAllPresentationModelsByType("myType").first()
            myPm.getAttribute("a").qualifier = "myQualifier"
            myPm.getAttribute("a").qualifier = "othervalue"
        });

        clientDolphin.getClientConnector().send(new CreatePmCommand(), new OnFinishedHandler() {
            @Override
            void onFinished() {
                def pm = clientDolphin.getModelStore().findAllPresentationModelsByType("myType").first()
                pm.getAttribute('a').addPropertyChangeListener("qualifier", new PropertyChangeListener() {
                    @Override
                    void propertyChange(PropertyChangeEvent evt) { // assume a client side listener
                        pm.getAttribute('a').qualifier="myQualifier"
                    }
                })
            }
        });
        clientDolphin.getClientConnector().send(new SetAndUnsetQualifierCommand(), new OnFinishedHandler() {
            @Override
            void onFinished() {
                assert "myQualifier" == clientDolphin.getModelStore().findAllPresentationModelsByType("myType").first().getAttribute("a").qualifier
                context.assertionsDone()
            }
        });
    }

    void testServerSideSetQualifier() {
        registerAction( serverDolphin, CreatePmCommand.class, { cmd ->
            serverDolphin.getModelStore().presentationModel(null, "myType", new DTO(new Slot('a',0)))
            serverDolphin.getModelStore().presentationModel("target", null, new DTO(new Slot('a',1)))
        });
        registerAction( serverDolphin, SetQualifierCommand.class, { cmd ->
            def myPm = serverDolphin.getModelStore().findAllPresentationModelsByType("myType").first()
            myPm.getAttribute("a").qualifier = "myQualifier"
        })

        clientDolphin.getClientConnector().send(new CreatePmCommand(), new OnFinishedHandler() {
            @Override
            void onFinished() {
                assert clientDolphin.getModelStore().findAllPresentationModelsByType("myType").first()
            }
        });
        clientDolphin.getClientConnector().send(new SetQualifierCommand(), new OnFinishedHandler() {
            @Override
            void onFinished() {
                assert clientDolphin.getModelStore().findAllPresentationModelsByType("myType").first().getAttribute("a").qualifier == "myQualifier"
                context.assertionsDone()
            }
        });
    }

}