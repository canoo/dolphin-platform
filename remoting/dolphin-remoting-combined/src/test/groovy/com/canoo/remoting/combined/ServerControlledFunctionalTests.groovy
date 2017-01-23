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
import com.canoo.remoting.client.ClientAttribute
import com.canoo.remoting.client.ClientDolphin
import com.canoo.remoting.client.ClientPresentationModel
import com.canoo.remoting.client.communication.OnFinishedHandler
import com.canoo.remoting.server.DTO
import com.canoo.remoting.server.DefaultServerDolphin
import com.canoo.remoting.server.ServerDolphin
import com.canoo.remoting.server.ServerPresentationModel
import com.canoo.remoting.server.Slot
import com.canoo.communication.common.commands.NamedCommand
import com.canoo.remoting.server.action.DolphinServerAction
import com.canoo.remoting.server.communication.ActionRegistry
import com.canoo.remoting.server.communication.CommandHandler

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.util.concurrent.TimeUnit
/**
 * Functional tests for scenarios that customers observed when controlling
 * the application purely from server side.
 */

class ServerControlledFunctionalTests extends GroovyTestCase {

    volatile TestInMemoryConfig context
    DefaultServerDolphin serverDolphin
    ClientDolphin clientDolphin

    @Override
    protected void setUp() {
        context = new TestInMemoryConfig()
        serverDolphin = context.serverDolphin
        clientDolphin = context.clientDolphin
//        LogConfig.noLogs()
    }

    @Override
    protected void tearDown() {
        assert context.done.await(20, TimeUnit.SECONDS)
    }

    void registerAction(ServerDolphin serverDolphin, String name, CommandHandler<NamedCommand> handler) {
        serverDolphin.register(new DolphinServerAction() {

            @Override
            void registerIn(ActionRegistry registry) {
                registry.register(name, handler);
            }
        });
    }

    void testPMsWereDeletedAndRecreated() {
        // a pm created on the client side
        clientDolphin.presentationModel("pm1", new ClientAttribute("a", 0 ))

        // register a server-side action that sees the second PM
        registerAction (serverDolphin, "checkPmIsThere", { cmd, list ->
            assert serverDolphin.getPresentationModel("pm1").getAttribute("a").value == 1
            assert clientDolphin.getPresentationModel("pm1").getAttribute("a").value == 1
            context.assertionsDone()
        });

        assert clientDolphin.getPresentationModel("pm1").getAttribute("a").value == 0
        clientDolphin.delete(clientDolphin.getPresentationModel("pm1"))
        clientDolphin.presentationModel("pm1", new ClientAttribute("a", 1 ))
        clientDolphin.send("checkPmIsThere")
    }


    void testPMsWereCreatedOnServerSideDeletedByTypeRecreatedOnServer() { // the "Baerbel" problem
        registerAction( serverDolphin, ("createPM"), { cmd, list ->
            serverDolphin.presentationModel(null, "myType", new DTO(new Slot('a',0)))
        });
        registerAction( serverDolphin, ("deleteAndRecreate"), { cmd, list ->
            List<ServerPresentationModel> toDelete = new ArrayList<>();
            for(ServerPresentationModel model : serverDolphin.findAllPresentationModelsByType("myType")) {
                toDelete.add(model);
            }
            for(ServerPresentationModel model : toDelete) {
                serverDolphin.removePresentationModel(model);
            }

            serverDolphin.presentationModel(null, "myType", new DTO(new Slot('a',0))) // recreate
            serverDolphin.presentationModel(null, "myType", new DTO(new Slot('a',1))) // recreate

            assert serverDolphin.findAllPresentationModelsByType("myType").size() == 2
            assert serverDolphin.findAllPresentationModelsByType("myType")[0].getAttribute("a").value == 0
            assert serverDolphin.findAllPresentationModelsByType("myType")[1].getAttribute("a").value == 1
        });

        registerAction( serverDolphin, ("assertRetainedServerState"), { cmd, list ->
            assert serverDolphin.findAllPresentationModelsByType("myType").size() == 2
            assert serverDolphin.findAllPresentationModelsByType("myType")[0].getAttribute("a").value == 0
            assert serverDolphin.findAllPresentationModelsByType("myType")[1].getAttribute("a").value == 1
            context.assertionsDone()
        });

        clientDolphin.send("createPM", new OnFinishedHandler() {
            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                assert clientDolphin.findAllPresentationModelsByType("myType").size() == 1
                assert clientDolphin.findAllPresentationModelsByType("myType").first().getAttribute("a").value == 0
            }
        });
        clientDolphin.send("deleteAndRecreate", new OnFinishedHandler() {
            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                assert clientDolphin.findAllPresentationModelsByType("myType").size() == 2
                assert clientDolphin.findAllPresentationModelsByType("myType")[0].getAttribute("a").value == 0
                assert clientDolphin.findAllPresentationModelsByType("myType")[1].getAttribute("a").value == 1
            }
        });
        clientDolphin.send("assertRetainedServerState")
    }

    void testChangeValueMultipleTimesAndBackToBase() { // Alex issue
        // register a server-side action that creates a PM
        registerAction( serverDolphin, ("createPM"), { cmd, list ->
            serverDolphin.presentationModel("myPm", null, new DTO(new Slot('a',0)))
        });
        registerAction( serverDolphin, ("changeValueMultipleTimesAndBackToBase"), { cmd, list ->
            def myPm = serverDolphin.getPresentationModel("myPm")
            myPm.getAttribute("a").value = 1
            myPm.getAttribute("a").value = 2
            myPm.getAttribute("a").value = 0
        });

        clientDolphin.send("createPM")
        clientDolphin.send("changeValueMultipleTimesAndBackToBase", new OnFinishedHandler() {
            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                def myPm = clientDolphin.getPresentationModel("myPm")
                assert myPm.getAttribute("a").value == 0
                context.assertionsDone()
            }
        });
    }

    void testServerSideRemove() {
        registerAction(serverDolphin, "createPM", { cmd, list ->
            serverDolphin.presentationModel("myPm", null, new DTO(new Slot('a',0)))
        });
        registerAction(serverDolphin, "remove", { cmd, list ->
            def myPm = serverDolphin.getPresentationModel("myPm")
            serverDolphin.removePresentationModel(myPm)
            assert null == serverDolphin.getPresentationModel("myPm")
        });

        clientDolphin.send("createPM", new OnFinishedHandler() {
            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                assert clientDolphin.getPresentationModel("myPm")
            }
        });
        clientDolphin.send("remove", new OnFinishedHandler() {
            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                assert null == clientDolphin.getPresentationModel("myPm")
                context.assertionsDone()
            }
        });
    }

    void testServerSideSetAndUnsetQualifier() {
        registerAction(serverDolphin, "createPM", { cmd, list ->
            serverDolphin.presentationModel(null, "myType", new DTO(new Slot('a',0)))
        });
        registerAction(serverDolphin, "setAndUnsetQualifier", { cmd, list ->
            def myPm = serverDolphin.findAllPresentationModelsByType("myType").first()
            myPm.getAttribute("a").qualifier = "myQualifier"
            myPm.getAttribute("a").qualifier = "othervalue"
        });

        clientDolphin.send("createPM", new OnFinishedHandler() {
            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                def pm = clientDolphin.findAllPresentationModelsByType("myType").first()
                pm.getAttribute('a').addPropertyChangeListener("qualifier", new PropertyChangeListener() {
                    @Override
                    void propertyChange(PropertyChangeEvent evt) { // assume a client side listener
                        pm.getAttribute('a').qualifier="myQualifier"
                    }
                })
            }
        });
        clientDolphin.send("setAndUnsetQualifier", new OnFinishedHandler() {
            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                assert "myQualifier" == clientDolphin.findAllPresentationModelsByType("myType").first().getAttribute("a").qualifier
                context.assertionsDone()
            }
        });
    }

    void testServerSideSetQualifier() {
        registerAction( serverDolphin, ("createPM"), { cmd, list ->
            serverDolphin.presentationModel(null, "myType", new DTO(new Slot('a',0)))
            serverDolphin.presentationModel("target", null, new DTO(new Slot('a',1)))
        });
        registerAction( serverDolphin, ("setQualifier"), { cmd, list ->
            def myPm = serverDolphin.findAllPresentationModelsByType("myType").first()
            myPm.getAttribute("a").qualifier = "myQualifier"
        })

        clientDolphin.send("createPM", new OnFinishedHandler() {
            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                assert clientDolphin.findAllPresentationModelsByType("myType").first()
            }
        });
        clientDolphin.send("setQualifier", new OnFinishedHandler() {
            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                assert clientDolphin.findAllPresentationModelsByType("myType").first().getAttribute("a").qualifier == "myQualifier"
                context.assertionsDone()
            }
        });
    }

}