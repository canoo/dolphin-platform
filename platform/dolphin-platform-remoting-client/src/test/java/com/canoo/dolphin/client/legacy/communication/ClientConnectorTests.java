/*
 * Copyright 2015-2018 Canoo Engineering AG.
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
package com.canoo.dolphin.client.legacy.communication;

import com.canoo.dp.impl.client.legacy.ClientAttribute;
import com.canoo.dp.impl.client.legacy.ClientModelStore;
import com.canoo.dp.impl.client.legacy.ClientPresentationModel;
import com.canoo.dp.impl.client.legacy.DefaultModelSynchronizer;
import com.canoo.dp.impl.client.legacy.ModelSynchronizer;
import com.canoo.dp.impl.client.legacy.communication.AbstractClientConnector;
import com.canoo.dp.impl.client.legacy.communication.AttributeChangeListener;
import com.canoo.dp.impl.client.legacy.communication.ClientResponseHandler;
import com.canoo.dp.impl.client.legacy.communication.CommandBatcher;
import com.canoo.dp.impl.client.legacy.communication.OnFinishedHandler;
import com.canoo.dp.impl.client.legacy.communication.SimpleExceptionHandler;
import com.canoo.dp.impl.remoting.legacy.commands.InterruptLongPollCommand;
import com.canoo.dp.impl.remoting.legacy.commands.StartLongPollCommand;
import com.canoo.dp.impl.remoting.legacy.communication.AttributeMetadataChangedCommand;
import com.canoo.dp.impl.remoting.legacy.communication.ChangeAttributeMetadataCommand;
import com.canoo.dp.impl.remoting.legacy.communication.Command;
import com.canoo.dp.impl.remoting.legacy.communication.CreatePresentationModelCommand;
import com.canoo.dp.impl.remoting.legacy.communication.DeletePresentationModelCommand;
import com.canoo.dp.impl.remoting.legacy.communication.EmptyCommand;
import com.canoo.dp.impl.remoting.legacy.communication.PresentationModelDeletedCommand;
import com.canoo.dp.impl.remoting.legacy.communication.ValueChangedCommand;
import com.canoo.dp.impl.remoting.legacy.core.Attribute;
import com.canoo.dp.impl.remoting.legacy.util.DirectExecutor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientConnectorTests {

    private TestClientConnector clientConnector;
    private ClientModelStore clientModelStore;
    private ClientResponseHandler responseHandler;
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

    @BeforeMethod
    public void setUp() {
        ModelSynchronizer defaultModelSynchronizer = new DefaultModelSynchronizer(() -> clientConnector);
        clientModelStore = new ClientModelStore(defaultModelSynchronizer);
        clientConnector = new TestClientConnector(clientModelStore, DirectExecutor.getInstance());
        this.responseHandler = new ClientResponseHandler(clientModelStore);
        try {
            attributeChangeListener = clientModelStore.getAttributeChangeListener();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        clientConnector.connect();

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

        clientConnector.send(new EmptyCommand(), new OnFinishedHandler() {
            public void onFinished() {
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
        responseHandler.dispatchHandle(new EmptyCommand());
        syncAndWaitUntilDone();
        assertOnlySyncCommandWasTransmitted();
    }

    @Test
    public void testHandleSimpleCreatePresentationModelCommand() {
        final String myPmId = "myPmId";
        Assert.assertEquals(null, clientModelStore.findPresentationModelById(myPmId));
        CreatePresentationModelCommand command = new CreatePresentationModelCommand();
        command.setPmId(myPmId);
        responseHandler.dispatchHandle(command);
        Assert.assertNotNull(clientModelStore.findPresentationModelById(myPmId));
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
        ClientAttribute attribute = new ClientAttribute("attr", "initialValue");
        clientModelStore.registerAttribute(attribute);
        attributeChangeListener.propertyChange(new PropertyChangeEvent(attribute, Attribute.VALUE_NAME, attribute.getValue(), "newValue"));
        syncAndWaitUntilDone();
        assertCommandsTransmitted(2);
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
        ClientAttribute attribute = new ClientAttribute("attr", "initialValue", "qualifier");
        clientModelStore.registerAttribute(attribute);
        attributeChangeListener.propertyChange(new PropertyChangeEvent(attribute, Attribute.VALUE_NAME, attribute.getValue(), "newValue"));
        syncAndWaitUntilDone();
        assertCommandsTransmitted(3);
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
        clientModelStore.createModel("1", null, new ClientAttribute("a", "0", "QUAL"), new ClientAttribute("b", "0", "QUAL"));
    }

    @Test
    public void testMetaDataChange_UnregisteredAttribute() {
        ClientAttribute attribute = new ExtendedAttribute("attr", "initialValue", "qualifier");
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
        ClientAttribute attribute = new ClientAttribute("attr", "initialValue");
        clientModelStore.registerAttribute(attribute);
        responseHandler.dispatchHandle(new ValueChangedCommand(attribute.getId(), "newValue"));
        Assert.assertEquals("newValue", attribute.getValue());
    }

    @Test
    public void testHandle_ValueChanged() {
        ClientAttribute attribute = new ClientAttribute("attr", "initialValue");
        clientModelStore.registerAttribute(attribute);

        responseHandler.dispatchHandle(new ValueChangedCommand(attribute.getId(), "newValue"));
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
        responseHandler.dispatchHandle(new CreatePresentationModelCommand("p1", "type", attributes));
        responseHandler.dispatchHandle(new CreatePresentationModelCommand("p1", "type", attributes));
    }

    @Test
    public void testHandle_CreatePresentationModel() {
        List<Map<String, Object>> attributes = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<>();
        map.put("propertyName", "attr");
        map.put("value", "initialValue");
        map.put("qualifier", "qualifier");
        ((ArrayList<Map<String, Object>>) attributes).add(map);

        responseHandler.dispatchHandle(new CreatePresentationModelCommand("p1", "type", attributes));
        Assert.assertNotNull(clientModelStore.findPresentationModelById("p1"));
        Assert.assertNotNull(clientModelStore.findPresentationModelById("p1").getAttribute("attr"));
        Assert.assertEquals("initialValue", clientModelStore.findPresentationModelById("p1").getAttribute("attr").getValue());
        Assert.assertEquals("qualifier", clientModelStore.findPresentationModelById("p1").getAttribute("attr").getQualifier());
        syncAndWaitUntilDone();
        assertCommandsTransmitted(2);
        Assert.assertFalse(clientConnector.getTransmittedCommands().isEmpty());
        Assert.assertEquals(CreatePresentationModelCommand.class, clientConnector.getTransmittedCommands().get(0).getClass());
    }

    @Test
    public void testHandle_CreatePresentationModel_ClientSideOnly() {
        List<Map<String, Object>> attributes = new ArrayList<Map<String, Object>>();
        Map map = new HashMap();
        map.put("propertyName", "attr");
        map.put("value", "initialValue");
        map.put("qualifier", "qualifier");
        ((ArrayList<Map<String, Object>>) attributes).add(map);
        responseHandler.dispatchHandle(new CreatePresentationModelCommand("p1", "type", attributes, true));
        Assert.assertNotNull(clientModelStore.findPresentationModelById("p1"));
        Assert.assertNotNull(clientModelStore.findPresentationModelById("p1").getAttribute("attr"));
        Assert.assertEquals("initialValue", clientModelStore.findPresentationModelById("p1").getAttribute("attr").getValue());
        Assert.assertEquals("qualifier", clientModelStore.findPresentationModelById("p1").getAttribute("attr").getQualifier());
        syncAndWaitUntilDone();
        assertOnlySyncCommandWasTransmitted();
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testHandle_CreatePresentationModel_MergeAttributesToExistingModel() {
        clientModelStore.createModel("p1", null);
        responseHandler.dispatchHandle(new CreatePresentationModelCommand("p1", "type", Collections.<Map<String, Object>>emptyList()));
    }

    @Test
    public void testHandle_DeletePresentationModel() {
        ClientPresentationModel p1 = clientModelStore.createModel("p1", null);
        p1.setClientSideOnly(true);
        ClientPresentationModel p2 = clientModelStore.createModel("p2", null);
        responseHandler.dispatchHandle(new DeletePresentationModelCommand(null));
        ClientPresentationModel model = new ClientPresentationModel("p3", Collections.<ClientAttribute>emptyList());
        responseHandler.dispatchHandle(new DeletePresentationModelCommand(model.getId()));
        responseHandler.dispatchHandle(new DeletePresentationModelCommand(p1.getId()));
        responseHandler.dispatchHandle(new DeletePresentationModelCommand(p2.getId()));
        Assert.assertNull(clientModelStore.findPresentationModelById(p1.getId()));
        Assert.assertNull(clientModelStore.findPresentationModelById(p2.getId()));
        syncAndWaitUntilDone();
        // 3 commands will have been transferred:
        // 1: delete of p1 (causes no DeletedPresentationModelNotification since client side only)
        // 2: delete of p2
        // 3: DeletedPresentationModelNotification caused by delete of p2
        assertCommandsTransmitted(4);

        int deletedPresentationModelNotificationCount = 0;
        for (Command c : clientConnector.getTransmittedCommands()) {
            if (c instanceof PresentationModelDeletedCommand) {
                deletedPresentationModelNotificationCount = deletedPresentationModelNotificationCount + 1;
            }

        }
        Assert.assertEquals(1, deletedPresentationModelNotificationCount);
    }

    public class TestClientConnector extends AbstractClientConnector {

        private final List<Command> transmittedCommands = new ArrayList<Command>();

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

        @Override
        public void connect() {
            connect();
        }
    }

    public class ExtendedAttribute extends ClientAttribute {
        public ExtendedAttribute(String propertyName, Object initialValue, String qualifier) {
            super(propertyName, initialValue, qualifier);
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
