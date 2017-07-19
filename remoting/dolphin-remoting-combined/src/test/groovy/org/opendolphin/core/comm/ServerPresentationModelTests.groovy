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
import org.opendolphin.RemotingConstants
import org.opendolphin.core.Attribute
import org.opendolphin.core.ModelStoreConfig
import org.opendolphin.core.PresentationModel
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.client.comm.OnFinishedHandler
import org.opendolphin.core.server.*
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry
import org.opendolphin.core.server.comm.CommandHandler
import org.opendolphin.core.util.AbstractTestCommand

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
/**
 * Functional tests for the server-side state changes.
 */

class ServerPresentationModelTests extends GroovyTestCase {

    private final class SetValueCommand extends AbstractTestCommand {}

    private final class AssertValueCommand extends AbstractTestCommand {}

    private final class ChangeValueCommand extends AbstractTestCommand {}

    private final class AttachListenerCommand extends AbstractTestCommand {}

    private final class ChangeBaseValueCommand extends AbstractTestCommand {}

    private final class CreateCommand extends AbstractTestCommand {}

    private final class AssertVisibleCommand extends AbstractTestCommand {}

    private final class RemoveCommand extends AbstractTestCommand {}

    private final class AssertValueChangeCommand extends AbstractTestCommand {}

    private volatile TestInMemoryConfig context
    private DefaultServerDolphin serverDolphin
    private ClientDolphin clientDolphin

    @Override
    protected void setUp() {
        context = new TestInMemoryConfig();
        serverDolphin = context.getServerDolphin();
        clientDolphin = context.getClientDolphin();
    }

    @Override
    protected void tearDown() {
        Assert.assertTrue(context.getDone().await(10, TimeUnit.SECONDS));
    }

    void testServerModelStoreAcceptsConfig() {
        new ServerModelStore(new ModelStoreConfig());
        context.assertionsDone();
    }

    public <T extends Command> void registerAction(ServerDolphin serverDolphin, Class<T> commandType, CommandHandler<T> handler) {
        serverDolphin.getServerConnector().register(new DolphinServerAction() {

            @Override
            void registerIn(ActionRegistry registry) {
                registry.register(commandType, handler);
            }
        });
    }


    void testServerPresentationModelDoesNotRejectAutoId() {

        //TODO: How to refactor this to Java?

        // re-enable the shouldFail once we have proper Separation of commands and notifications
//        shouldFail IllegalArgumentException, {
        assert new ServerPresentationModel("1${RemotingConstants.SERVER_PM_AUTO_ID_SUFFIX}", [], new ServerModelStore())
//        }
        context.assertionsDone();
    }

    void testSecondServerActionCanRelyOnAttributeValueChange() {
        ClientPresentationModel model = clientDolphin.getModelStore().createModel("PM1", null, new ClientAttribute("att1", null));

        registerAction(serverDolphin, SetValueCommand.class, new CommandHandler<SetValueCommand>() {

            @Override
            void handleCommand(SetValueCommand command, List<Command> response) {
                serverDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att1").setValue(1);
            }
        });

        registerAction(serverDolphin, AssertValueCommand.class, new CommandHandler<AssertValueCommand>() {

            @Override
            void handleCommand(AssertValueCommand command, List<Command> response) {
                Assert.assertEquals(1, serverDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att1").getValue());
            }
        });



        clientDolphin.getClientConnector().send(new SetValueCommand(), null);
        clientDolphin.getClientConnector().send new AssertValueCommand(), new OnFinishedHandler() {

            @Override
            void onFinished() {
                Assert.assertEquals(1, model.getAttribute("att1").getValue());
                context.assertionsDone();
            }
        }
    }

    void testServerSideValueChangesUseQualifiers() {
        ClientPresentationModel model = clientDolphin.getModelStore().createModel("PM1", null, new ClientAttribute("att1", "base"), new ClientAttribute("att2", "base"));
        model.getAttribute("att1").setQualifier('qualifier');
        model.getAttribute("att2").setQualifier('qualifier');

        registerAction(serverDolphin, ChangeValueCommand.class, new CommandHandler<ChangeValueCommand>() {

            @Override
            void handleCommand(ChangeValueCommand command, List<Command> response) {
                Attribute at1 = serverDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att1");
                Assert.assertEquals('base', at1.getValue());
                at1.setValue("changed");
                Assert.assertEquals('changed', serverDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att2").getValue());
            }
        });


        clientDolphin.getClientConnector().send(new ChangeValueCommand(), null);

        clientDolphin.sync {
            context.assertionsDone();
        }
    }

    void testServerSideEventListenerCanChangeSelfValue() {
        ClientPresentationModel model = clientDolphin.getModelStore().createModel("PM1", null, new ClientAttribute("att1", "base"));


        registerAction(serverDolphin, AttachListenerCommand.class, new CommandHandler<AttachListenerCommand>() {

            @Override
            void handleCommand(AttachListenerCommand command, List<Command> response) {
                ServerAttribute at1 = serverDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att1");
                at1.addPropertyChangeListener("value", new PropertyChangeListener() {
                    @Override
                    void propertyChange(PropertyChangeEvent evt) {
                        at1.setValue("changed from PCL");
                    }
                });
            }
        });

        clientDolphin.sync {
            //...
        }

        clientDolphin.getClientConnector().send(new AttachListenerCommand(), null);

        clientDolphin.sync {
            model.getAttribute("att1").setValue("changed");
            clientDolphin.sync {
                Assert.assertEquals("changed from PCL", model.getAttribute("att1").getValue());
                context.assertionsDone();
            }
        }
    }


    void testSecondServerActionCanRelyOnPmCreate() {

        PresentationModel pmWithNullId;


        registerAction(serverDolphin, CreateCommand.class, new CommandHandler<CreateCommand>() {

            @Override
            void handleCommand(CreateCommand command, List<Command> response) {
                DTO dto = new DTO(new Slot("att1", 1));
                PresentationModel pm = serverDolphin.getModelStore().presentationModel("PM1", null, dto);
                pmWithNullId = serverDolphin.getModelStore().presentationModel(null, "pmType", dto);
                Assert.assertNotNull(pm);
                Assert.assertNotNull(pmWithNullId);
                Assert.assertNotNull(serverDolphin.getModelStore().findPresentationModelById("PM1"));
                Assert.assertEquals(pmWithNullId, serverDolphin.getModelStore().findAllPresentationModelsByType("pmType").get(0));
            }
        });
        registerAction(serverDolphin, AssertVisibleCommand.class, new CommandHandler<AssertVisibleCommand>() {

            @Override
            void handleCommand(AssertVisibleCommand command, List<Command> response) {
                Assert.assertNotNull(serverDolphin.getModelStore().findPresentationModelById("PM1"));
                Assert.assertEquals(pmWithNullId, serverDolphin.getModelStore().findAllPresentationModelsByType("pmType").get(0));
            }
        });

        clientDolphin.getClientConnector().send(new CreateCommand(), null);
        clientDolphin.getClientConnector().send(new AssertVisibleCommand(), null);

        clientDolphin.sync {
            Assert.assertNotNull(clientDolphin.getModelStore().findPresentationModelById("PM1"));
            Assert.assertEquals(1, clientDolphin.getModelStore().findAllPresentationModelsByType("pmType").size());
            System.out.println(clientDolphin.getModelStore().findAllPresentationModelsByType("pmType").get(0).getId());
            context.assertionsDone();
        }
    }

    void testServerCreatedAttributeChangesValueOnTheClientSide() {

        AtomicBoolean pclReached = new AtomicBoolean(false);

        registerAction(serverDolphin, CreateCommand.class, new CommandHandler<CreateCommand>() {

            @Override
            void handleCommand(CreateCommand command, List<Command> response) {
                DTO dto = new DTO(new Slot("att1", 1));
                serverDolphin.getModelStore().presentationModel("PM1", null, dto);
                serverDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att1").addPropertyChangeListener("value", new PropertyChangeListener() {
                    @Override
                    void propertyChange(PropertyChangeEvent evt) {
                        pclReached.set(true);
                    }
                });
            }
        });

        registerAction(serverDolphin, AssertValueChangeCommand.class, new CommandHandler<AssertValueChangeCommand>() {

            @Override
            void handleCommand(AssertValueChangeCommand command, List<Command> response) {
                Assert.assertTrue(pclReached.get());
                Assert.assertEquals(2, serverDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att1").getValue());
                context.assertionsDone();
            }
        });


        clientDolphin.getClientConnector().send new CreateCommand(), new OnFinishedHandler() {

            @Override
            void onFinished() {
                Assert.assertEquals(1, clientDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att1").getValue());
                clientDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att1").setValue(2);
                clientDolphin.getClientConnector().send(new AssertValueChangeCommand(), null);
            }
        }

    }

    void testServerSidePmRemoval() {

        clientDolphin.getModelStore().createModel("client-side-with-id", null, new ClientAttribute("attr1", 1));

        registerAction(serverDolphin, RemoveCommand.class, new CommandHandler<RemoveCommand>() {

            @Override
            void handleCommand(RemoveCommand command, List<Command> response) {
                PresentationModel pm = serverDolphin.getModelStore().findPresentationModelById("client-side-with-id");
                Assert.assertNotNull(pm);
                serverDolphin.getModelStore().remove(pm);
                Assert.assertNull(serverDolphin.getModelStore().findPresentationModelById("client-side-with-id")); // immediately removed on server
            }
        });

        Assert.assertNotNull(clientDolphin.getModelStore().findPresentationModelById("client-side-with-id"));


        clientDolphin.getClientConnector().send(new RemoveCommand(), new OnFinishedHandler() {

            @Override
            void onFinished() {
                Assert.assertNull(clientDolphin.getModelStore().findPresentationModelById("client-side-with-id"));
                // removed from client before callback
                context.assertionsDone();
            }
        });
    }

    void testServerSideBaseValueChange() {
        ClientPresentationModel source = clientDolphin.getModelStore().createModel("source", null, new ClientAttribute("attr1", "sourceValue"))

        registerAction(serverDolphin, ChangeBaseValueCommand.class, new CommandHandler<ChangeBaseValueCommand>() {

            @Override
            void handleCommand(ChangeBaseValueCommand command, List<Command> response) {
                Attribute attribute = serverDolphin.getModelStore().findPresentationModelById("source").getAttribute("attr1");
            }
        });

        clientDolphin.getClientConnector().send(new ChangeBaseValueCommand(), new OnFinishedHandler() {

            @Override
            void onFinished() {
                Assert.assertEquals("sourceValue", source.getAttribute("attr1").getValue());
                context.assertionsDone();
            }
        });
    }

    void testServerSideQualifierChange() {
        ClientPresentationModel source = clientDolphin.getModelStore().createModel("source", null, new ClientAttribute("attr1", "sourceValue"));
        source.getAttribute("attr1").setQualifier("qualifier");

        registerAction(serverDolphin, ChangeBaseValueCommand.class, new CommandHandler<ChangeBaseValueCommand>() {

            @Override
            void handleCommand(ChangeBaseValueCommand command, List<Command> response) {
                Attribute attribute = serverDolphin.getModelStore().findPresentationModelById("source").getAttribute("attr1")
                attribute.setQualifier("changed");
                // immediately applied on server
                Assert.assertEquals("changed", attribute.getQualifier());
            }
        });


        clientDolphin.getClientConnector().send(new ChangeBaseValueCommand(), new OnFinishedHandler() {

            @Override
            void onFinished() {
                Assert.assertEquals("sourceValue", source.getAttribute("attr1").getValue());
                Assert.assertEquals("changed", source.getAttribute("attr1").getQualifier());
                context.assertionsDone();
            }
        });
    }

    // todo dk: think about these use cases:
    // dolphin.copy(pm) on client and server (done) - todo: make js version use the same approach
    // server-side tagging

}