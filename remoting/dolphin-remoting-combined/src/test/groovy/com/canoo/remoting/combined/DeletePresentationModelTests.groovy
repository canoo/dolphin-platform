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
import com.canoo.remoting.client.ClientDolphin
import com.canoo.remoting.client.ClientPresentationModel
import com.canoo.remoting.client.communication.OnFinishedHandler
import com.canoo.communication.common.commands.Command
import com.canoo.communication.common.commands.NamedCommand
import com.canoo.remoting.server.DefaultServerDolphin
import com.canoo.remoting.server.ServerDolphin
import com.canoo.remoting.server.action.DolphinServerAction
import com.canoo.remoting.server.communication.ActionRegistry
import com.canoo.remoting.server.communication.CommandHandler

import java.util.concurrent.TimeUnit

class DeletePresentationModelTests extends GroovyTestCase {

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
        assert context.done.await(2, TimeUnit.SECONDS)
    }

    void registerAction(ServerDolphin serverDolphin, String name, CommandHandler<NamedCommand> handler) {
        serverDolphin.register(new DolphinServerAction() {

            @Override
            void registerIn(ActionRegistry registry) {
                registry.register(name, handler);
            }
        });
    }
    
    void testCreateAndDeletePresentationModel() {
        // create the pm
        String modelId = 'modelId'
        def model = clientDolphin.presentationModel(modelId, someAttribute:"someValue")
        // sanity check: we have a least the client model store listening to changes of someAttribute
        assert model.getAttribute("someAttribute").propertyChangeListeners
        // the model is in the client model store
        def found = clientDolphin.getPresentationModel(modelId)
        assert model == found
        // ... and in the server model store after roundtrip
        clientDolphin.sync {
            assert serverDolphin.getPresentationModel(modelId)
        }
        // when we now delete the pm
        clientDolphin.delete(model)
        // ... it is no longer in the client model store
        assert !clientDolphin.getPresentationModel(modelId)
        // ... all listeners have been detached from model and all its attributes
        assert ! model.getPropertyChangeListeners()
        // what is allowed to remain is the "detached" model still listening to its own attribute changes
        model.attributes*.propertyChangeListeners.flatten()*.listener.each {
            assert (it.toString() =~ "PresentationModel")
            // todo dk: the below should also work but there is some weird boxing going on
            // assert it.is(model)
        }
        // the model is also gone from the server model store
        clientDolphin.sync {
            assert !serverDolphin.getPresentationModel(modelId)
            context.assertionsDone()
        }
    }


    void testCreateAndDeletePresentationModelFromServer() {
        // create the pm
        String modelId = 'modelId'
        def model = clientDolphin.presentationModel(modelId, someAttribute:"someValue")
        // the model is in the client model store
        def found = clientDolphin.getPresentationModel(modelId)
        assert model == found
        // ... and in the server model store after roundtrip
        clientDolphin.sync {
            assert serverDolphin.getPresentationModel(modelId)
        }

        registerAction(serverDolphin, 'triggerDelete', new CommandHandler<NamedCommand>() {

            @Override
            void handleCommand(NamedCommand command, List<Command> response) {
                serverDolphin.deleteCommand(response, modelId)
            }
        });
        // when we now delete the pm
        clientDolphin.send 'triggerDelete', new OnFinishedHandler() {
            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                clientDolphin.sync {
                    // ... it is no longer in the client model store
                    assert !clientDolphin.getPresentationModel(modelId)
                }
                clientDolphin.sync {
                    // the model is also gone from the server model store
                    assert !serverDolphin.getPresentationModel(modelId)
                    // we are done
                    context.assertionsDone()
                }
            }
        }
    }

}