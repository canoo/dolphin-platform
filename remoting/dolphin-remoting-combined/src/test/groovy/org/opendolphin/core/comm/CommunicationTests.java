package org.opendolphin.core.comm;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opendolphin.LogConfig;
import org.opendolphin.core.client.ClientAttribute;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.comm.AbstractClientConnector;
import org.opendolphin.core.server.ServerConnector;
import org.opendolphin.core.server.comm.CommandHandler;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

/**
 * Tests for the sequence between client requests and server responses.
 * They are really more integration tests than unit tests.
 */
public class CommunicationTests {

    final private class ButtonActionCommand extends Command {
    }

    private ServerConnector serverConnector;
    private AbstractClientConnector clientConnector;
    private ClientModelStore clientModelStore;
    private ClientDolphin clientDolphin;
    private TestInMemoryConfig config;

    @Before
    public void setUp() {
        LogConfig.logOnLevel(Level.INFO);
        config = new TestInMemoryConfig();
        serverConnector = config.getServerDolphin().getServerConnector();
        clientConnector = config.getClientConnector();
        clientModelStore = config.getClientDolphin().getModelStore();
        clientDolphin = config.getClientDolphin();
    }

    @After
    public void tearDown() {
        try {
            Assert.assertTrue(config.getDone().await(2, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSimpleAttributeChangeIsVisibleOnServer() {
        ClientAttribute ca = new ClientAttribute("name", null);
        ClientPresentationModel cpm = new ClientPresentationModel("model", Arrays.asList(ca));
        clientModelStore.add(cpm);

        final AtomicReference<Command> receivedCommand = new AtomicReference<>(null);
        CommandHandler testServerAction = new CommandHandler<ValueChangedCommand>() {
            @Override
            public void handleCommand(ValueChangedCommand command, List<Command> response) {
                receivedCommand.set(command);
            }

        };
        serverConnector.getRegistry().register(ValueChangedCommand.class, testServerAction);
        ca.setValue("initial");

        clientDolphin.sync(new Runnable() {
            @Override
            public void run() {
                Assert.assertNotNull(receivedCommand.get());
                Assert.assertEquals("ValueChanged", receivedCommand.get().getId());
                Assert.assertEquals(ValueChangedCommand.class, receivedCommand.get().getClass());

                ValueChangedCommand cmd = (ValueChangedCommand) receivedCommand.get();
                Assert.assertEquals(null, cmd.getOldValue());
                Assert.assertEquals("initial", cmd.getNewValue());
                config.assertionsDone();
            }
        });
    }

    @Test
    public void testServerIsNotifiedAboutNewAttributesAndTheirPms() {
        final AtomicReference<Command> receivedCommand = new AtomicReference<>(null);
        CommandHandler testServerAction = new CommandHandler<CreatePresentationModelCommand>() {
            @Override
            public void handleCommand(CreatePresentationModelCommand command, List<Command> response) {
                receivedCommand.set(command);
            }

        };
        serverConnector.getRegistry().register(CreatePresentationModelCommand.class, testServerAction);
        clientModelStore.add(new ClientPresentationModel("testPm", Arrays.asList(new ClientAttribute("name", null))));
        clientDolphin.sync(new Runnable() {
            @Override
            public void run() {
                Assert.assertNotNull(receivedCommand.get());
                Assert.assertEquals("CreatePresentationModel", receivedCommand.get().getId());
                Assert.assertEquals(CreatePresentationModelCommand.class, receivedCommand.get().getClass());
                CreatePresentationModelCommand cmd = (CreatePresentationModelCommand) receivedCommand.get();
                Assert.assertEquals("testPm", cmd.getPmId());

                config.assertionsDone();
            }
        });
    }

    @Test
    public void testWhenServerChangesValueThisTriggersUpdateOnClient() {
        final ClientAttribute ca = new ClientAttribute("name", null);

        CommandHandler setValueAction = new CommandHandler<CreatePresentationModelCommand>() {
            @Override
            public void handleCommand(CreatePresentationModelCommand command, List<Command> response) {
                response.add(new ValueChangedCommand(command.getAttributes().get(0).get("id").toString(), null, "set from server"));
            }
        };

        CommandHandler valueChangedAction = new CommandHandler<ValueChangedCommand>() {
            @Override
            public void handleCommand(ValueChangedCommand command, List<Command> response) {
                clientDolphin.sync(new Runnable() {
                    @Override
                    public void run() {
                        // there is no onFinished for value changes, so we have to do it here
                        Assert.assertEquals("set from server", ca.getValue());// client is updated
                        Assert.assertEquals(ca.getId(), command.getAttributeId());// client notified server about value change
                        config.assertionsDone();
                    }
                });
            }

        };

        serverConnector.getRegistry().register(CreatePresentationModelCommand.class, setValueAction);
        serverConnector.getRegistry().register(ValueChangedCommand.class, valueChangedAction);
        clientModelStore.add(new ClientPresentationModel("testPm", Arrays.asList(ca)));// trigger the whole cycle
    }

    @Test
    public void testRequestingSomeGeneralCommandExecution() {
        final AtomicBoolean reached = new AtomicBoolean(false);
        serverConnector.getRegistry().register(ButtonActionCommand.class, new CommandHandler<ButtonActionCommand>() {
            @Override
            public void handleCommand(ButtonActionCommand command, List response) {
                reached.set(true);
            }

        });
        clientConnector.send(new ButtonActionCommand());

        clientDolphin.sync(new Runnable() {
            @Override
            public void run() {
                Assert.assertTrue(reached.get());
                config.assertionsDone();
            }
        });
    }

}