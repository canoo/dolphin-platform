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

import org.junit.Assert
import org.opendolphin.core.PresentationModel
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.client.comm.OnFinishedHandler
import org.opendolphin.core.server.DefaultServerDolphin
import org.opendolphin.core.server.ServerDolphin
import org.opendolphin.core.server.ServerModelStore
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry
import org.opendolphin.core.server.comm.CommandHandler
import org.opendolphin.core.util.AbstractTestCommand

import java.util.concurrent.TimeUnit

class DeletePresentationModelTests extends GroovyTestCase {

    private volatile TestInMemoryConfig context;
    private DefaultServerDolphin serverDolphin;
    private ClientDolphin clientDolphin;

    private final class TriggerDeleteCommand extends AbstractTestCommand {}


    @Override
    protected void setUp() {
        context = new TestInMemoryConfig();
        serverDolphin = context.getServerDolphin();
        clientDolphin = context.getClientDolphin();
    }

    @Override
    protected void tearDown() {
        Assert.assertTrue(context.getDone().await(2, TimeUnit.SECONDS));
    }

    public <T extends Command> void registerAction(ServerDolphin serverDolphin, Class<T> commandClass, CommandHandler<T> handler) {
        serverDolphin.getServerConnector().register(new DolphinServerAction() {

            @Override
            void registerIn(ActionRegistry registry) {
                registry.register(commandClass, handler);
            }
        });
    }

    void testCreateAndDeletePresentationModel() {
        // create the pm
        String modelId = 'modelId';
        ClientPresentationModel model = clientDolphin.getModelStore().createModel(modelId, null, new ClientAttribute("someAttribute", "someValue"));
        // sanity check: we have a least the client model store listening to changes of someAttribute
        assert model.getAttribute("someAttribute").propertyChangeListeners
        // the model is in the client model store
        PresentationModel found = clientDolphin.getModelStore().findPresentationModelById(modelId)
        Assert.assertEquals(model, found);
        // ... and in the server model store after roundtrip
        clientDolphin.sync {
            Assert.assertNotNull(serverDolphin.getModelStore().findPresentationModelById(modelId));
        }
        // when we now delete the pm
        clientDolphin.getModelStore().delete(model);
        // ... it is no longer in the client model store
        Assert.assertNull(clientDolphin.getModelStore().findPresentationModelById(modelId));
        // ... all listeners have been detached from model and all its attributes
        assert !model.getPropertyChangeListeners()
        // what is allowed to remain is the "detached" model still listening to its own attribute changes
        model.attributes*.propertyChangeListeners.flatten()*.listener.each {
            assert (it.toString() =~ "PresentationModel")
            // todo dk: the below should also work but there is some weird boxing going on
            // assert it.is(model)
        }
        // the model is also gone from the server model store
        clientDolphin.sync {
            assert !serverDolphin.getModelStore().findPresentationModelById(modelId)
            context.assertionsDone()
        }
    }

    void testCreateAndDeletePresentationModelFromServer() {
        // create the pm
        String modelId = 'modelId';
        ClientPresentationModel model = clientDolphin.getModelStore().createModel(modelId, null, new ClientAttribute("someAttribute", "someValue"));
        // the model is in the client model store
        ClientPresentationModel found = clientDolphin.getModelStore().findPresentationModelById(modelId);
        Assert.assertEquals(model, found);
        // ... and in the server model store after roundtrip
        clientDolphin.sync {
            Assert.assertNotNull(serverDolphin.getModelStore().findPresentationModelById(modelId));
        }

        registerAction(serverDolphin, TriggerDeleteCommand.class, new CommandHandler<TriggerDeleteCommand>() {

            @Override
            void handleCommand(TriggerDeleteCommand command, List<Command> response) {
                ServerModelStore.deleteCommand(response, modelId);
            }
        });
        // when we now delete the pm
        clientDolphin.getClientConnector().send(new TriggerDeleteCommand(), new OnFinishedHandler() {
            @Override
            void onFinished() {
                clientDolphin.sync {
                    // ... it is no longer in the client model store
                    Assert.assertNull(clientDolphin.getModelStore().findPresentationModelById(modelId));
                }
                clientDolphin.sync {
                    // the model is also gone from the server model store
                    Assert.assertNull(serverDolphin.getModelStore().findPresentationModelById(modelId));
                    // we are done
                    context.assertionsDone();
                }
            }
        });
    }

}