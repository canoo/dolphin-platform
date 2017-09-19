package com.canoo.dolphin.client.legacy.communication;

import com.canoo.dp.impl.client.legacy.ClientAttribute;
import com.canoo.dp.impl.client.legacy.ClientDolphin;
import com.canoo.dp.impl.client.legacy.ClientModelStore;
import com.canoo.dp.impl.client.legacy.ClientPresentationModel;
import com.canoo.dp.impl.client.legacy.ModelSynchronizer;
import com.canoo.dp.impl.client.legacy.communication.AbstractClientConnector;
import com.canoo.dp.impl.client.legacy.communication.AttributeChangeListener;
import com.canoo.dp.impl.client.legacy.communication.CommandBatcher;
import com.canoo.dp.impl.client.legacy.communication.SimpleExceptionHandler;
import com.canoo.dp.impl.remoting.legacy.commands.AttributeMetadataChangedCommand;
import com.canoo.dp.impl.remoting.legacy.commands.ChangeAttributeMetadataCommand;
import com.canoo.dp.impl.remoting.legacy.commands.Command;
import com.canoo.dp.impl.remoting.legacy.commands.CreatePresentationModelCommand;
import com.canoo.dp.impl.remoting.legacy.commands.DeletePresentationModelCommand;
import com.canoo.dp.impl.remoting.legacy.commands.EmptyCommand;
import com.canoo.dp.impl.remoting.legacy.commands.InterruptLongPollCommand;
import com.canoo.dp.impl.remoting.legacy.commands.StartLongPollCommand;
import com.canoo.dp.impl.remoting.legacy.commands.ValueChangedCommand;
import com.canoo.dp.impl.remoting.legacy.core.Attribute;
import com.canoo.dp.impl.remoting.legacy.util.DirectExecutor;
import com.canoo.platform.core.functional.Provider;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientConnectorTests {

    @BeforeMethod
    public void setUp() {
        dolphin = new ClientDolphin();
        ModelSynchronizer defaultModelSynchronizer = new ModelSynchronizer(new Provider<AbstractClientConnector>() {
            @Override
            public AbstractClientConnector get() {
                return dolphin.getClientConnector();
            }

        });
        ClientModelStore clientModelStore = new ClientModelStore(defaultModelSynchronizer);
        dolphin.setClientModelStore(clientModelStore);
        clientConnector = new TestClientConnector(clientModelStore, DirectExecutor.getInstance());
        dolphin.setClientConnector(clientConnector);

        try {
            attributeChangeListener = dolphin.getModelStore().getAttributeChangeListener();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        clientConnector.connect(false);

        initLatch();
    }

    private void initLatch() {
        syncDone = new CountDownLatch(1);
    }

    private boolean waitForLatch() {
        try {
            return syncDone.await(2, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void syncAndWaitUntilDone() {
        dolphin.sync(new Runnable() {
            @Override
            public void run() {
                syncDone.countDown();
            }
        });
        Assert.assertTrue(waitForLatch());
    }

    private void assertCommandsTransmitted(final int count) {
        Assert.assertEquals(count, clientConnector.getTransmitCount());
    }

    private void assertOnlySyncCommandWasTransmitted() {
        assertCommandsTransmitted(1);
        // 1 command was sent because of the sent sync (resulting in a EMPTY command):
        Assert.assertFalse(clientConnector.getTransmittedCommands().isEmpty());
        Assert.assertEquals(EmptyCommand.class, clientConnector.getTransmittedCommands().get(0).getClass());
    }

    @Test
    public void testSevereLogWhenCommandNotFound() {
        clientConnector.dispatchHandle(new EmptyCommand());
        syncAndWaitUntilDone();
        assertOnlySyncCommandWasTransmitted();
    }

    @Test
    public void testHandleSimpleCreatePresentationModelCommand() {
        final String myPmId = "myPmId";
        Assert.assertEquals(null, dolphin.getModelStore().findPresentationModelById(myPmId));
        CreatePresentationModelCommand command = new CreatePresentationModelCommand();
        command.setPmId(myPmId);
        clientConnector.dispatchHandle(command);
        Assert.assertNotNull(dolphin.getModelStore().findPresentationModelById(myPmId));
        syncAndWaitUntilDone();
        assertCommandsTransmitted(2);
    }

    @Test
    public void testValueChange_OldAndNewValueSame() {
        attributeChangeListener.propertyChange(new PropertyChangeEvent("dummy", Attribute.VALUE_NAME, "sameValue", "sameValue"));
        syncAndWaitUntilDone();
        assertOnlySyncCommandWasTransmitted();
    }

    @Test
    public void testValueChange_noQualifier() {
        final ClientAttribute attribute = new ClientAttribute("attr", "initialValue");
        final ClientPresentationModel model = new ClientPresentationModel("id", Collections.singletonList(attribute));

        dolphin.getModelStore().add(model);
        attributeChangeListener.propertyChange(new PropertyChangeEvent(attribute, Attribute.VALUE_NAME, attribute.getValue(), "newValue"));
        syncAndWaitUntilDone();
        assertCommandsTransmitted(3);
        Assert.assertEquals("initialValue", attribute.getValue());

        boolean valueChangedCommandFound = false;
        for (Command c : clientConnector.getTransmittedCommands()) {
            if (c instanceof ValueChangedCommand) {
            }

            valueChangedCommandFound = true;
        }

        Assert.assertTrue(valueChangedCommandFound);
    }

    @Test
    public void testValueChange_withQualifier() {
        syncDone = new CountDownLatch(1);
        ClientAttribute attribute = new ClientAttribute("attr", "initialValue");
        attribute.setQualifier("qualifier");
        ClientPresentationModel model = new ClientPresentationModel("id", Collections.singletonList(attribute));
        dolphin.getModelStore().add(model);
        attributeChangeListener.propertyChange(new PropertyChangeEvent(attribute, Attribute.VALUE_NAME, attribute.getValue(), "newValue"));
        syncAndWaitUntilDone();
        assertCommandsTransmitted(4);
        Assert.assertEquals("newValue", attribute.getValue());

        boolean valueChangedCommandFound = false;
        for (Command c : clientConnector.getTransmittedCommands()) {
            if (c instanceof ValueChangedCommand) {
            }

            valueChangedCommandFound = true;
        }

        Assert.assertTrue(valueChangedCommandFound);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testAddTwoAttributesInConstructorWithSameQualifierToSamePMIsNotAllowed() {
        final ClientAttribute attribute1 = new ClientAttribute("a", "0");
        attribute1.setQualifier("QUAL");
        final ClientAttribute attribute2 = new ClientAttribute("b", "0");
        attribute2.setQualifier("QUAL");
        final ClientPresentationModel model = new ClientPresentationModel(UUID.randomUUID().toString(), Arrays.asList(attribute1, attribute2));
        dolphin.getModelStore().add(model);
    }

    @Test
    public void testMetaDataChange_UnregisteredAttribute() {
        ClientAttribute attribute = new ExtendedAttribute("attr", "initialValue");
        attribute.setQualifier("qualifier");
        ((ExtendedAttribute) attribute).setAdditionalParam("oldValue");
        attributeChangeListener.propertyChange(new PropertyChangeEvent(attribute, "additionalParam", null, "newTag"));
        syncAndWaitUntilDone();
        assertCommandsTransmitted(2);
        Assert.assertFalse(clientConnector.getTransmittedCommands().isEmpty());
        Assert.assertEquals(ChangeAttributeMetadataCommand.class, clientConnector.getTransmittedCommands().get(0).getClass());
        Assert.assertEquals("oldValue", ((ExtendedAttribute) attribute).getAdditionalParam());
    }

    @Test
    public void testHandle_ValueChangedWithBadBaseValueIgnoredInNonStrictMode() {
        final ClientAttribute attribute = new ClientAttribute("attr", "initialValue");
        final ClientPresentationModel model = new ClientPresentationModel("id", Collections.singletonList(attribute));

        dolphin.getModelStore().add(model);
        clientConnector.dispatchHandle(new ValueChangedCommand(attribute.getId(), "newValue"));
        Assert.assertEquals("newValue", attribute.getValue());
    }

    @Test
    public void testHandle_ValueChanged() {
        final ClientAttribute attribute = new ClientAttribute("attr", "initialValue");
        final ClientPresentationModel model = new ClientPresentationModel("id", Collections.singletonList(attribute));

        dolphin.getModelStore().add(model);

        clientConnector.dispatchHandle(new ValueChangedCommand(attribute.getId(), "newValue"));
        Assert.assertEquals("newValue", attribute.getValue());
    }

    @Test(expectedExceptions = Exception.class)
    public void testHandle_CreatePresentationModelTwiceFails() {
        List<Map<String, Object>> attributes = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("propertyName", "attr");
        map.put("value", "initialValue");
        map.put("qualifier", "qualifier");
        ((ArrayList<Map<String, Object>>) attributes).add(map);
        clientConnector.dispatchHandle(new CreatePresentationModelCommand("p1", "type", attributes));
        clientConnector.dispatchHandle(new CreatePresentationModelCommand("p1", "type", attributes));
    }

    @Test
    public void testHandle_CreatePresentationModel() {
        List<Map<String, Object>> attributes = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<>();
        map.put("propertyName", "attr");
        map.put("value", "initialValue");
        map.put("qualifier", "qualifier");
        ((ArrayList<Map<String, Object>>) attributes).add(map);

        clientConnector.dispatchHandle(new CreatePresentationModelCommand("p1", "type", attributes));
        Assert.assertNotNull(dolphin.getModelStore().findPresentationModelById("p1"));
        Assert.assertNotNull(dolphin.getModelStore().findPresentationModelById("p1").getAttribute("attr"));
        Assert.assertEquals("initialValue", dolphin.getModelStore().findPresentationModelById("p1").getAttribute("attr").getValue());
        Assert.assertEquals("qualifier", dolphin.getModelStore().findPresentationModelById("p1").getAttribute("attr").getQualifier());
        syncAndWaitUntilDone();
        assertCommandsTransmitted(2);
        Assert.assertFalse(clientConnector.getTransmittedCommands().isEmpty());
        Assert.assertEquals(CreatePresentationModelCommand.class, clientConnector.getTransmittedCommands().get(0).getClass());
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testHandle_CreatePresentationModel_MergeAttributesToExistingModel() {
        final ClientPresentationModel model = new ClientPresentationModel("p1", Collections.<ClientAttribute>emptyList());
        dolphin.getModelStore().add(model);
        clientConnector.dispatchHandle(new CreatePresentationModelCommand("p1", "type", Collections.<Map<String, Object>>emptyList()));
    }

    @Test
    public void testHandle_DeletePresentationModel() {
        ClientPresentationModel p1 = new ClientPresentationModel("p1", Collections.<ClientAttribute>emptyList());
        dolphin.getModelStore().add(p1);
        ClientPresentationModel p2 = new ClientPresentationModel("p2", Collections.<ClientAttribute>emptyList());
        dolphin.getModelStore().add(p2);
        clientConnector.dispatchHandle(new DeletePresentationModelCommand(null));
        ClientPresentationModel model = new ClientPresentationModel("p3", Collections.<ClientAttribute>emptyList());
        clientConnector.dispatchHandle(new DeletePresentationModelCommand(model.getId()));
        clientConnector.dispatchHandle(new DeletePresentationModelCommand(p1.getId()));
        clientConnector.dispatchHandle(new DeletePresentationModelCommand(p2.getId()));
        Assert.assertNull(dolphin.getModelStore().findPresentationModelById(p1.getId()));
        Assert.assertNull(dolphin.getModelStore().findPresentationModelById(p2.getId()));
        assertCommandsTransmitted(2);
    }

    private TestClientConnector clientConnector;
    private ClientDolphin dolphin;
    private AttributeChangeListener attributeChangeListener;
    /**
     * Since command transmission is done in parallel to test execution thread the test method might finish
     * before the command processing is complete. Therefore tearDown() waits for this CountDownLatch
     * (which btw. is initialized in {@link #setUp()} and decremented in the handler of a {@code dolphin.sync ( )} call).
     * Also putting asserts in the callback handler of a {@code dolphin.sync ( )} call seems not to be reliable since JUnit
     * seems not to be informed (reliably) of failing assertions.
     * <p>
     * Therefore the following approach for the test methods has been taken to:
     * - initialize the CountDownLatch in {@code testBaseValueChange # setup ( )}
     * - after the "act" section of a test method: call {@code syncAndWaitUntilDone ( )} which releases the latch inside a dolphin.sync handler and then (in the main thread) waits for the latch
     * - performs all assertions
     */
    private CountDownLatch syncDone;

    public class TestClientConnector extends AbstractClientConnector {
        public TestClientConnector(ClientModelStore modelStore, Executor uiExecutor) {
            super(modelStore, uiExecutor, new CommandBatcher(), new SimpleExceptionHandler(), Executors.newCachedThreadPool());
        }

        public int getTransmitCount() {
            return transmittedCommands.size();
        }

        public List<Command> transmit(List<Command> commands) {
            System.out.print("transmit: " + commands.size());
            LinkedList result = new LinkedList<Command>();
            for (Command cmd : commands) {
                result.addAll(transmitCommand(cmd));
            }

            return result;
        }

        @Override
        public String getClientId() {
            return null;
        }

        public List<Command> transmitCommand(Command command) {
            System.out.print("transmitCommand: " + command);

            if (command != null && !(command instanceof StartLongPollCommand) && !(command instanceof InterruptLongPollCommand)) {
                transmittedCommands.add(command);
            }

            return construct(command);
        }

        public List<Command> getTransmittedCommands() {
            return transmittedCommands;
        }

        public List<AttributeMetadataChangedCommand> construct(ChangeAttributeMetadataCommand command) {
            return Collections.singletonList(new AttributeMetadataChangedCommand(command.getAttributeId(), command.getMetadataName(), command.getValue()));
        }

        public List construct(Command command) {
            return Collections.emptyList();
        }

        private List<Command> transmittedCommands = new ArrayList<Command>();
    }

    public class ExtendedAttribute extends ClientAttribute {
        public ExtendedAttribute(String propertyName, Object initialValue) {
            super(propertyName, initialValue);
        }

        public String getAdditionalParam() {
            return additionalParam;
        }

        public void setAdditionalParam(String additionalParam) {
            this.additionalParam = additionalParam;
        }

        private String additionalParam;
    }
}
