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
package org.opendolphin.core.comm;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.client.ClientAttribute;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.comm.OnFinishedHandler;
import org.opendolphin.core.server.*;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;
import org.opendolphin.core.util.AbstractTestCommand;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Functional tests for scenarios that customers observed when controlling
 * the application purely from server side.
 */
public class ServerControlledFunctionalTests {

    private volatile TestInMemoryConfig context;
    private DefaultServerDolphin serverDolphin;
    private ClientDolphin clientDolphin;

    final private class CreatePmCommand extends AbstractTestCommand {
        public CreatePmCommand() {
            super("CreatePmCommand");
        }
    }

    final private class CheckPmIsThereCommand extends AbstractTestCommand {
        public CheckPmIsThereCommand() {
            super("CheckPmIsThereCommand");
        }
    }

    final private class DeleteAndRecreateCommand extends AbstractTestCommand {
        public DeleteAndRecreateCommand() {
            super("DeleteAndRecreateCommand");
        }
    }

    final private class AssertRetainedServerStateCommand extends AbstractTestCommand {
        public AssertRetainedServerStateCommand() {
            super("AssertRetainedServerStateCommand");
        }
    }

    final private class ChangeValueMultipleTimesAndBackToBaseCommand extends AbstractTestCommand {
        public ChangeValueMultipleTimesAndBackToBaseCommand() {
            super("ChangeValueMultipleTimesAndBackToBaseCommand");
        }
    }

    final private class RemoveCommand extends AbstractTestCommand {
        public RemoveCommand() {
            super("RemoveCommand");
        }
    }

    final private class SetAndUnsetQualifierCommand extends AbstractTestCommand {
        public SetAndUnsetQualifierCommand() {
            super("SetAndUnsetQualifierCommand");
        }
    }

    final private class SetQualifierCommand extends AbstractTestCommand {
        public SetQualifierCommand() {
            super("SetQualifierCommand");
        }
    }

    @Before
    public void setUp() {
        context = new TestInMemoryConfig();
        serverDolphin = ((DefaultServerDolphin) (context.getServerDolphin()));
        clientDolphin = context.getClientDolphin();
    }

    @After
    public void tearDown() {
        try {
            Assert.assertTrue(context.getDone().await(20, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

    }

    private <T extends Command> void registerAction(ServerDolphin serverDolphin, final Class<T> commandType, final CommandHandler<T> handler) {
        serverDolphin.getServerConnector().register(new DolphinServerAction() {
            @Override
            public void registerIn(ActionRegistry registry) {
                registry.register(commandType, handler);
            }

        });
    }

    @Test
    public void testPMsWereDeletedAndRecreated() {
        // a pm created on the client side
        clientDolphin.getModelStore().createModel("pm1", null, new ClientAttribute("a", 0));

        // register a server-side action that sees the second PM
        registerAction(serverDolphin, CheckPmIsThereCommand.class, new CommandHandler<CheckPmIsThereCommand>() {
            @Override
            public void handleCommand(CheckPmIsThereCommand command, List<Command> response) {
                Assert.assertEquals(1, serverDolphin.getModelStore().findPresentationModelById("pm1").getAttribute("a").getValue());
                Assert.assertEquals(1, clientDolphin.getModelStore().findPresentationModelById("pm1").getAttribute("a").getValue());
                context.assertionsDone();
            }

        });
        Assert.assertEquals(0, clientDolphin.getModelStore().findPresentationModelById("pm1").getAttribute("a").getValue());
        clientDolphin.getModelStore().delete(clientDolphin.getModelStore().findPresentationModelById("pm1"));
        clientDolphin.getModelStore().createModel("pm1", null, new ClientAttribute("a", 1));
        clientDolphin.getClientConnector().send(new CheckPmIsThereCommand(), null);
    }

    @Test
    public void testPMsWereCreatedOnServerSideDeletedByTypeRecreatedOnServer() {// the "Baerbel" problem
        registerAction(serverDolphin, CreatePmCommand.class, new CommandHandler<CreatePmCommand>() {
            @Override
            public void handleCommand(CreatePmCommand command, List<Command> response) {
                serverDolphin.getModelStore().presentationModel(null, "myType", new DTO(new Slot("a", 0)));
            }

        });
        registerAction(serverDolphin, DeleteAndRecreateCommand.class, new CommandHandler<DeleteAndRecreateCommand>() {
            @Override
            public void handleCommand(DeleteAndRecreateCommand command, List<Command> response) {
                List<ServerPresentationModel> toDelete = new ArrayList<ServerPresentationModel>();
                for (ServerPresentationModel model : serverDolphin.getModelStore().findAllPresentationModelsByType("myType")) {
                    ((ArrayList<ServerPresentationModel>) toDelete).add(model);
                }


                for (ServerPresentationModel model : toDelete) {
                    serverDolphin.getModelStore().remove(model);
                }


                serverDolphin.getModelStore().presentationModel(null, "myType", new DTO(new Slot("a", 0)));// recreate
                serverDolphin.getModelStore().presentationModel(null, "myType", new DTO(new Slot("a", 1)));// recreate

                Assert.assertEquals(2, serverDolphin.getModelStore().findAllPresentationModelsByType("myType").size());
                Assert.assertEquals(0, serverDolphin.getModelStore().findAllPresentationModelsByType("myType").get(0).getAttribute("a").getValue());
                Assert.assertEquals(1, serverDolphin.getModelStore().findAllPresentationModelsByType("myType").get(1).getAttribute("a").getValue());
            }

        });
        registerAction(serverDolphin, AssertRetainedServerStateCommand.class, new CommandHandler<AssertRetainedServerStateCommand>() {
            @Override
            public void handleCommand(AssertRetainedServerStateCommand command, List<Command> response) {
                Assert.assertEquals(2, serverDolphin.getModelStore().findAllPresentationModelsByType("myType").size());
                Assert.assertEquals(0, serverDolphin.getModelStore().findAllPresentationModelsByType("myType").get(0).getAttribute("a").getValue());
                Assert.assertEquals(1, serverDolphin.getModelStore().findAllPresentationModelsByType("myType").get(1).getAttribute("a").getValue());
                context.assertionsDone();
            }

        });


        clientDolphin.getClientConnector().send(new CreatePmCommand(), new OnFinishedHandler() {
            @Override
            public void onFinished() {
                Assert.assertEquals(1, clientDolphin.getModelStore().findAllPresentationModelsByType("myType").size());
                Assert.assertEquals(0, clientDolphin.getModelStore().findAllPresentationModelsByType("myType").get(0).getAttribute("a").getValue());
            }

        });
        clientDolphin.getClientConnector().send(new DeleteAndRecreateCommand(), new OnFinishedHandler() {
            @Override
            public void onFinished() {
                Assert.assertEquals(2, clientDolphin.getModelStore().findAllPresentationModelsByType("myType").size());
                Assert.assertEquals(0, clientDolphin.getModelStore().findAllPresentationModelsByType("myType").get(0).getAttribute("a").getValue());
                Assert.assertEquals(1, clientDolphin.getModelStore().findAllPresentationModelsByType("myType").get(1).getAttribute("a").getValue());
            }

        });
        clientDolphin.getClientConnector().send(new AssertRetainedServerStateCommand(), null);
    }

    @Test
    public void testChangeValueMultipleTimesAndBackToBase() {// Alex issue
        // register a server-side action that creates a PM
        registerAction(serverDolphin, CreatePmCommand.class, new CommandHandler<CreatePmCommand>() {
            @Override
            public void handleCommand(CreatePmCommand command, List<Command> response) {
                serverDolphin.getModelStore().presentationModel("myPm", null, new DTO(new Slot("a", 0)));
            }

        });
        registerAction(serverDolphin, ChangeValueMultipleTimesAndBackToBaseCommand.class, new CommandHandler<ChangeValueMultipleTimesAndBackToBaseCommand>() {
            @Override
            public void handleCommand(ChangeValueMultipleTimesAndBackToBaseCommand command, List<Command> response) {
                PresentationModel myPm = serverDolphin.getModelStore().findPresentationModelById("myPm");
                ((ServerPresentationModel) myPm).getAttribute("a").setValue(1);
                ((ServerPresentationModel) myPm).getAttribute("a").setValue(2);
                ((ServerPresentationModel) myPm).getAttribute("a").setValue(0);
            }

        });


        clientDolphin.getClientConnector().send(new CreatePmCommand(), null);
        clientDolphin.getClientConnector().send(new ChangeValueMultipleTimesAndBackToBaseCommand(), new OnFinishedHandler() {
            @Override
            public void onFinished() {
                PresentationModel myPm = clientDolphin.getModelStore().findPresentationModelById("myPm");
                Assert.assertEquals(0, ((ClientPresentationModel) myPm).getAttribute("a").getValue());
                context.assertionsDone();
            }

        });
    }

    @Test
    public void testServerSideRemove() {
        registerAction(serverDolphin, CreatePmCommand.class, new CommandHandler<CreatePmCommand>() {
            @Override
            public void handleCommand(CreatePmCommand command, List<Command> response) {
                serverDolphin.getModelStore().presentationModel("myPm", null, new DTO(new Slot("a", 0)));
            }

        });
        registerAction(serverDolphin, RemoveCommand.class, new CommandHandler<RemoveCommand>() {
            @Override
            public void handleCommand(RemoveCommand command, List<Command> response) {
                PresentationModel myPm = serverDolphin.getModelStore().findPresentationModelById("myPm");
                serverDolphin.getModelStore().remove((ServerPresentationModel) myPm);
                Assert.assertNull(serverDolphin.getModelStore().findPresentationModelById("myPm"));
            }

        });

        clientDolphin.getClientConnector().send(new CreatePmCommand(), new OnFinishedHandler() {
            @Override
            public void onFinished() {
                Assert.assertNotNull(clientDolphin.getModelStore().findPresentationModelById("myPm"));
            }

        });
        clientDolphin.getClientConnector().send(new RemoveCommand(), new OnFinishedHandler() {
            @Override
            public void onFinished() {
                Assert.assertNull(clientDolphin.getModelStore().findPresentationModelById("myPm"));
                context.assertionsDone();
            }

        });
    }

    @Test
    public void testServerSideSetAndUnsetQualifier() {
        registerAction(serverDolphin, CreatePmCommand.class, new CommandHandler<CreatePmCommand>() {
            @Override
            public void handleCommand(CreatePmCommand command, List<Command> response) {
                serverDolphin.getModelStore().presentationModel(null, "myType", new DTO(new Slot("a", 0)));
            }

        });
        registerAction(serverDolphin, SetAndUnsetQualifierCommand.class, new CommandHandler<SetAndUnsetQualifierCommand>() {
            @Override
            public void handleCommand(SetAndUnsetQualifierCommand command, List<Command> response) {
                PresentationModel myPm = serverDolphin.getModelStore().findAllPresentationModelsByType("myType").get(0);
                ((ServerPresentationModel) myPm).getAttribute("a").setQualifier("myQualifier");
                ((ServerPresentationModel) myPm).getAttribute("a").setQualifier("othervalue");
            }

        });

        clientDolphin.getClientConnector().send(new CreatePmCommand(), new OnFinishedHandler() {
            @Override
            public void onFinished() {
                final ClientPresentationModel pm = clientDolphin.getModelStore().findAllPresentationModelsByType("myType").get(0);
                pm.getAttribute("a").addPropertyChangeListener("qualifier", new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {// assume a client side listener
                        pm.getAttribute("a").setQualifier("myQualifier");
                    }

                });
            }

        });
        clientDolphin.getClientConnector().send(new SetAndUnsetQualifierCommand(), new OnFinishedHandler() {
            @Override
            public void onFinished() {
                Assert.assertEquals("myQualifier", clientDolphin.getModelStore().findAllPresentationModelsByType("myType").get(0).getAttribute("a").getQualifier());
                context.assertionsDone();
            }

        });
    }

    @Test
    public void testServerSideSetQualifier() {
        registerAction(serverDolphin, CreatePmCommand.class, new CommandHandler<CreatePmCommand>() {
            @Override
            public void handleCommand(CreatePmCommand command, List<Command> response) {
                serverDolphin.getModelStore().presentationModel(null, "myType", new DTO(new Slot("a", 0)));
                serverDolphin.getModelStore().presentationModel("target", null, new DTO(new Slot("a", 1)));
            }

        });
        registerAction(serverDolphin, SetQualifierCommand.class, new CommandHandler<SetQualifierCommand>() {
            @Override
            public void handleCommand(SetQualifierCommand command, List<Command> response) {
                PresentationModel myPm = serverDolphin.getModelStore().findAllPresentationModelsByType("myType").get(0);
                ((ServerPresentationModel) myPm).getAttribute("a").setQualifier("myQualifier");
            }

        });

        clientDolphin.getClientConnector().send(new CreatePmCommand(), new OnFinishedHandler() {
            @Override
            public void onFinished() {
                Assert.assertNotNull(clientDolphin.getModelStore().findAllPresentationModelsByType("myType").get(0));
            }

        });
        clientDolphin.getClientConnector().send(new SetQualifierCommand(), new OnFinishedHandler() {
            @Override
            public void onFinished() {
                Assert.assertEquals("myQualifier", clientDolphin.getModelStore().findAllPresentationModelsByType("myType").get(0).getAttribute("a").getQualifier());
                context.assertionsDone();
            }

        });
    }


}
