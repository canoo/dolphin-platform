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

import core.client.comm.InMemoryClientConnector
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.opendolphin.core.Attribute
import org.opendolphin.core.PresentationModel
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.client.comm.BlindCommandBatcher
import org.opendolphin.core.client.comm.OnFinishedHandler
import org.opendolphin.core.client.comm.RunLaterUiThreadHandler
import org.opendolphin.core.server.*
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry
import org.opendolphin.core.server.comm.CommandHandler
import org.opendolphin.core.util.AbstractTestCommand

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
/**
 * Showcase for how to test an application without the GUI by
 * issuing the respective commands and model changes against the
 * ClientModelStore
 */
public class FunctionalPresentationModelTests {

    final private class CreateCommand extends AbstractTestCommand {
    }

    final private class PerformanceCommand extends AbstractTestCommand {
    }

    final private class CheckNotificationReachedCommand extends AbstractTestCommand {
    }

    final private class JavaCommand extends AbstractTestCommand {
    }

    final private class ArbitraryCommand extends AbstractTestCommand {
    }

    final private class DeleteCommand extends AbstractTestCommand {
    }

    final private class FetchDataCommand extends AbstractTestCommand {
    }

    final private class LoginCommand extends AbstractTestCommand {
    }

    final private class NoSuchActionRegisteredCommand extends AbstractTestCommand {
    }

    final private class Set2Command extends AbstractTestCommand {
    }

    final private class Assert3Command extends AbstractTestCommand {
    }

    final private class CheckTagIsKnownOnServerSideCommand extends AbstractTestCommand {
    }

    private volatile TestInMemoryConfig context;
    private DefaultServerDolphin serverDolphin;
    private ClientDolphin clientDolphin;


    @Before
    public void setUp() {
        context = new TestInMemoryConfig();
        serverDolphin = ((DefaultServerDolphin) (context.getServerDolphin()));
        clientDolphin = context.getClientDolphin();
    }

    @After
    public void tearDown() {
        try {
            Assert.assertTrue(context.getDone().await(10, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    private <T extends Command> void registerAction(ServerDolphin serverDolphin, final Class<T> commandClass, final CommandHandler<T> handler) {
        serverDolphin.getServerConnector().register(new DolphinServerAction() {
            @Override
            public void registerIn(ActionRegistry registry) {
                registry.register(commandClass, handler);
            }

        });
    }

    @Test
    public void testQualifiersInClientPMs() {
        PresentationModel modelA = clientDolphin.getModelStore().createModel("1", null, new ClientAttribute("a", 0, "QUAL"));
        PresentationModel modelB = clientDolphin.getModelStore().createModel("2", null, new ClientAttribute("b", 0, "QUAL"));

        ((ClientPresentationModel) modelA).getAttribute("a").setValue(1);
        Assert.assertEquals(1, ((ClientPresentationModel) modelB).getAttribute("b").getValue());
        context.assertionsDone();// make sure the assertions are really executed
    }

    @Test
    public void testPerformanceWithStandardCommandBatcher() {
        doTestPerformance();
    }

    @Test
    public void testPerformanceWithBlindCommandBatcher() {
        BlindCommandBatcher batcher = new BlindCommandBatcher();
        batcher.setMergeValueChanges(true);
        batcher.setDeferMillis(100);
        InMemoryClientConnector connector = new InMemoryClientConnector(clientDolphin.getModelStore(), serverDolphin.getServerConnector(), batcher, new RunLaterUiThreadHandler());
        clientDolphin.setClientConnector(connector);
        doTestPerformance();
    }

    @Test
    public void doTestPerformance() {
        final AtomicLong id = new AtomicLong(0);
        registerAction(serverDolphin, PerformanceCommand.class, new CommandHandler<PerformanceCommand>() {
            @Override
            public void handleCommand(PerformanceCommand command, List<Command> response) {
                for (int i = 0; i < 100; i++) {
                    ServerModelStore.presentationModelCommand(response, "id_" + id.getAndIncrement(), null, new DTO(new Slot("attr_" + i, i)));
                }
            }
        });

        final long start = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            clientDolphin.getClientConnector().send(new PerformanceCommand(), new OnFinishedHandler() {
                @Override
                public void onFinished() {
                }

            });
        }


        clientDolphin.getClientConnector().send(new PerformanceCommand(), new OnFinishedHandler() {
            @Override
            public void onFinished() {
                System.out.print(new char[0]);
                System.out.println((System.nanoTime() - start) / 1_000_000);
                context.assertionsDone();// make sure the assertions are really executed
            }

        });
    }

    @Test
    public void testCreationRoundtripDefaultBehavior() {
        registerAction(serverDolphin, CreateCommand.class, new CommandHandler<CreateCommand>() {
            @Override
            public void handleCommand(CreateCommand command, List<Command> response) {
                ServerModelStore.presentationModelCommand(response, "id", null, new DTO(new Slot("attr", "attr")));
            }

        });

        registerAction(serverDolphin, CheckNotificationReachedCommand.class, new CommandHandler<CheckNotificationReachedCommand>() {
            @Override
            public void handleCommand(CheckNotificationReachedCommand command, List<Command> response) {
                Assert.assertEquals(1, serverDolphin.getModelStore().listPresentationModels().size());
                Assert.assertNotNull(serverDolphin.getModelStore().findPresentationModelById("id"));
            }

        });

        clientDolphin.getClientConnector().send(new CreateCommand(), new OnFinishedHandler() {
            @Override
            public void onFinished() {
                clientDolphin.getClientConnector().send(new CheckNotificationReachedCommand(), new OnFinishedHandler() {
                    @Override
                    public void onFinished() {
                        context.assertionsDone();// make sure the assertions are really executed
                    }

                });
            }

        });
    }

    @Test
    public void testCreationRoundtripForTags() {
        registerAction(serverDolphin, CreateCommand.class, new CommandHandler<CreateCommand>() {
            @Override
            public void handleCommand(CreateCommand command, List<Command> response) {
                ServerModelStore.presentationModelCommand(response, "id", null, new DTO(new Slot("attr", true, null)));
            }

        });

        registerAction(serverDolphin, CheckTagIsKnownOnServerSideCommand.class, new CommandHandler<CheckTagIsKnownOnServerSideCommand>() {
            @Override
            public void handleCommand(CheckTagIsKnownOnServerSideCommand command, List<Command> response) {
            }

        });

        clientDolphin.getClientConnector().send(new CreateCommand(), new OnFinishedHandler() {
            @Override
            public void onFinished() {
                clientDolphin.getClientConnector().send(new CheckTagIsKnownOnServerSideCommand(), new OnFinishedHandler() {
                    @Override
                    public void onFinished() {
                        context.assertionsDone();
                    }

                });
            }

        });
    }

    @Test
    public void testFetchingAnInitialListOfData() {
        registerAction(serverDolphin, FetchDataCommand.class, new CommandHandler<FetchDataCommand>() {
            @Override
            public void handleCommand(FetchDataCommand command, List<Command> response) {
                for (String val : new ArrayList<String>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"))) {
                    // sending CreatePresentationModelCommand _without_ adding the pm to the server model store
                    ServerModelStore.presentationModelCommand(response, val, null, new DTO(new Slot("char", val)));
                }

            }

        });

        clientDolphin.getClientConnector().send(new FetchDataCommand(), new OnFinishedHandler() {
            @Override
            public void onFinished() {
                // pmIds from a single action should come in sequence
                Assert.assertEquals("a", context.getClientDolphin().getModelStore().findPresentationModelById("a").getAttribute("char").getValue());
                Assert.assertEquals("z", context.getClientDolphin().getModelStore().findPresentationModelById("z").getAttribute("char").getValue());
                context.assertionsDone();// make sure the assertions are really executed
            }

        });
    }

    @Test
    public void testLoginUseCase() {
        registerAction(serverDolphin, LoginCommand.class, new CommandHandler<LoginCommand>() {

            @Override
            public void handleCommand(LoginCommand command, List<Command> response) {
                PresentationModel user = serverDolphin.getModelStore().findPresentationModelById("user");
                Attribute nameAttribute = user.getAttribute("name");
                Attribute passwordAttribute = user.getAttribute("password");
                if (nameAttribute.getValue() != null && nameAttribute.getValue().equals("Dierk") && passwordAttribute.getValue() != null &&passwordAttribute.getValue().equals("Koenig")) {
                    ServerModelStore.changeValueCommand(response, ((ServerPresentationModel) user).getAttribute("loggedIn"), "true");
                }
            }
        });


        final ClientPresentationModel user = clientDolphin.getModelStore().createModel("user", null, new ClientAttribute("name", null), new ClientAttribute("password", null), new ClientAttribute("loggedIn", null));

        clientDolphin.getClientConnector().send(new LoginCommand(), new OnFinishedHandler() {

            @Override
            public void onFinished() {
                Assert.assertNull(user.getAttribute("loggedIn").getValue());
            }
        });

        user.getAttribute("name").setValue("Dierk");
        user.getAttribute("password").setValue("Koenig");

        clientDolphin.getClientConnector().send(new LoginCommand(), new OnFinishedHandler() {

            @Override
            public void onFinished() {
                Assert.assertNotNull(user.getAttribute("loggedIn").getValue());
                context.assertionsDone();
            }
        });
    }

    @Test
    public void testUnregisteredCommandWithLog() {
        clientDolphin.getClientConnector().send(new NoSuchActionRegisteredCommand(), new OnFinishedHandler() {
            @Override
            public void onFinished() {
                // unknown actions are silently ignored and logged as warnings on the server side.
            }

        });
        context.assertionsDone();
    }

    @Test
    public void testUnregisteredCommandWithoutLog() {
        clientDolphin.getClientConnector().send(new NoSuchActionRegisteredCommand(), null);
        context.assertionsDone();
    }

    @Test
    public void testIdNotFoundInVariousCommands() {
        clientDolphin.getClientConnector().send(new ValueChangedCommand("0", null));
        ServerModelStore.changeValueCommand(null, null, null);
        ServerModelStore.changeValueCommand(null, new ServerAttribute("a", 42), 42);
        context.assertionsDone();
    }

    @Test
    public void testActionAndSendJavaLike() {
        final AtomicBoolean reached = new AtomicBoolean(false);
        registerAction(serverDolphin, JavaCommand.class, new CommandHandler<JavaCommand>() {
            @Override
            public void handleCommand(JavaCommand command, List<Command> response) {
                reached.set(true);
            }

        });
        clientDolphin.getClientConnector().send(new JavaCommand(), new OnFinishedHandler() {
            @Override
            public void onFinished() {
                Assert.assertTrue(reached.get());
                context.assertionsDone();
            }

        });
    }

    @Test
    public void testRemovePresentationModel() {
        clientDolphin.getModelStore().createModel("pm", null, new ClientAttribute("attr", 1));

        registerAction(serverDolphin, DeleteCommand.class, new CommandHandler<DeleteCommand>() {
            @Override
            public void handleCommand(DeleteCommand command, List<Command> response) {
                serverDolphin.getModelStore().remove(serverDolphin.getModelStore().findPresentationModelById("pm"));
                Assert.assertNull(serverDolphin.getModelStore().findPresentationModelById("pm"));
            }

        });
        Assert.assertNotNull(clientDolphin.getModelStore().findPresentationModelById("pm"));

        clientDolphin.getClientConnector().send(new DeleteCommand(), new OnFinishedHandler() {
            @Override
            public void onFinished() {
                Assert.assertNull(clientDolphin.getModelStore().findPresentationModelById("pm"));
                context.assertionsDone();
            }

        });
    }

    @Test
    public void testWithNullResponses() {
        clientDolphin.getModelStore().createModel("pm", null, new ClientAttribute("attr", 1));

        registerAction(serverDolphin, ArbitraryCommand.class, new CommandHandler<ArbitraryCommand>() {
            @Override
            public void handleCommand(ArbitraryCommand command, List<Command> response) {
                ServerModelStore.deleteCommand(new ArrayList(), null);
                ServerModelStore.deleteCommand(new ArrayList(), "");
                ServerModelStore.deleteCommand(null, "");
                ServerModelStore.presentationModelCommand(null, null, null, null);
                ServerModelStore.changeValueCommand(new ArrayList(), null, null);
            }

        });

        clientDolphin.getClientConnector().send(new ArbitraryCommand(), new OnFinishedHandler() {
            @Override
            public void onFinished() {
                context.assertionsDone();
            }

        });
    }
}
