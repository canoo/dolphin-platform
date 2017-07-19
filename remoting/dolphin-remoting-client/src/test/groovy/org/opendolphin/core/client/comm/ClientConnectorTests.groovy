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
package org.opendolphin.core.client.comm

import com.canoo.dolphin.impl.commands.InterruptLongPollCommand
import com.canoo.dolphin.impl.commands.StartLongPollCommand
import org.junit.Assert
import org.opendolphin.core.Attribute
import org.opendolphin.core.client.*
import org.opendolphin.core.comm.*
import org.opendolphin.util.DirectExecutor
import org.opendolphin.util.Provider

import java.beans.PropertyChangeEvent
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

public class ClientConnectorTests extends GroovyTestCase {

    private TestClientConnector clientConnector;
    private ClientDolphin dolphin;
    private AttributeChangeListener attributeChangeListener;

    /**
     * Since command transmission is done in parallel to test execution thread the test method might finish
     * before the command processing is complete. Therefore {@link #tearDown()} waits for this CountDownLatch
     * (which btw. is initialized in {@link #setUp()} and decremented in the handler of a {@code dolphin.sync ( )} call).
     * Also putting asserts in the callback handler of a {@code dolphin.sync ( )} call seems not to be reliable since JUnit
     * seems not to be informed (reliably) of failing assertions.
     *
     * Therefore the following approach for the test methods has been taken to:
     * - initialize the CountDownLatch in {@code testBaseValueChange # setup ( )}
     * - after the "act" section of a test method: call {@code syncAndWaitUntilDone ( )} which releases the latch inside a dolphin.sync handler and then (in the main thread) waits for the latch
     * - performs all assertions
     */
    private CountDownLatch syncDone;


    @Override
    protected void setUp() {
        dolphin = new ClientDolphin();
        ModelSynchronizer defaultModelSynchronizer = new DefaultModelSynchronizer(new Provider<AbstractClientConnector>() {
            @Override
            AbstractClientConnector get() {
                return dolphin.clientConnector;
            }
        });
        ClientModelStore clientModelStore = new ClientModelStore(defaultModelSynchronizer);
        dolphin.setClientModelStore(clientModelStore);
        clientConnector = new TestClientConnector(clientModelStore, DirectExecutor.getInstance());
        dolphin.setClientConnector(clientConnector);

        attributeChangeListener = dolphin.getModelStore().@attributeChangeListener

        clientConnector.connect(false);

        initLatch();
    }

    private void initLatch() {
        syncDone = new CountDownLatch(1);
    }

    private boolean waitForLatch() {
        return syncDone.await(2, TimeUnit.SECONDS);
    }

    private void syncAndWaitUntilDone() {
        dolphin.sync {
            syncDone.countDown();
        }
        Assert.assertTrue(waitForLatch());
    }

    private void assertCommandsTransmitted(final int count) {
        Assert.assertEquals(count, clientConnector.getTransmitCount());
    }

    private void assertOnlySyncCommandWasTransmitted() {
        assertCommandsTransmitted(1)
        // 1 command was sent because of the sent sync (resulting in a EMPTY command):
        Assert.assertFalse(clientConnector.getTransmittedCommands().isEmpty());
        Assert.assertEquals(EmptyCommand.class, clientConnector.getTransmittedCommands().get(0).class);
    }

    public void testSevereLogWhenCommandNotFound() {
        clientConnector.dispatchHandle(new EmptyCommand());
        syncAndWaitUntilDone();
        assertOnlySyncCommandWasTransmitted();
    }

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

    //void testDefaultOnExceptionHandler() {
    //	clientConnector.uiThreadHandler = { it() } as UiThreadHandler
    //	String exceptionMessage = "TestException thrown on purpose"
    //	def msg = shouldFail(RuntimeException) {
    //		clientConnector.getOnException().handle(new RuntimeException(exceptionMessage))
    //	}
    //	assert msg == exceptionMessage
    //}

    public void testValueChange_OldAndNewValueSame() {
        attributeChangeListener.propertyChange(new PropertyChangeEvent("dummy", Attribute.VALUE_NAME, "sameValue", "sameValue"));
        syncAndWaitUntilDone();
        assertOnlySyncCommandWasTransmitted();
    }

    void testValueChange_noQualifier() {
        ClientAttribute attribute = new ClientAttribute("attr", "initialValue");
        dolphin.getModelStore().registerAttribute(attribute);
        attributeChangeListener.propertyChange(new PropertyChangeEvent(attribute, Attribute.VALUE_NAME, attribute.value, "newValue"));
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

    void testValueChange_withQualifier() {
        syncDone = new CountDownLatch(1);
        ClientAttribute attribute = new ClientAttribute("attr", "initialValue", "qualifier");
        dolphin.getModelStore().registerAttribute(attribute);
        attributeChangeListener.propertyChange(new PropertyChangeEvent(attribute, Attribute.VALUE_NAME, attribute.value, "newValue"));
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

    void testAddTwoAttributesInConstructorWithSameQualifierToSamePMIsNotAllowed() {
        shouldFail(IllegalStateException.class, {
            dolphin.getModelStore().createModel("1", null, new ClientAttribute("a", "0", "QUAL"), new ClientAttribute("b", "0", "QUAL"));
        });
    }

    void testMetaDataChange_UnregisteredAttribute() {
        ClientAttribute attribute = new ExtendedAttribute("attr", "initialValue", "qualifier");
        attribute.setAdditionalParam("oldValue");
        attributeChangeListener.propertyChange(new PropertyChangeEvent(attribute, "additionalParam", null, "newTag"));
        syncAndWaitUntilDone();
        assertCommandsTransmitted(2);
        Assert.assertFalse(clientConnector.getTransmittedCommands().isEmpty());
        Assert.assertEquals(ChangeAttributeMetadataCommand.class, clientConnector.getTransmittedCommands().get(0).getClass());
        Assert.assertEquals("oldValue", attribute.getAdditionalParam());
    }

    void testHandle_ValueChanged_AttrNotExists() {
        //TODO: How to convert this to Java?
        assert !clientConnector.dispatchHandle(new ValueChangedCommand("0", "newValue"));
    }

    void testHandle_ValueChangedWithBadBaseValueIgnoredInNonStrictMode() {
        ClientAttribute attribute = new ClientAttribute("attr", "initialValue");
        dolphin.getModelStore().registerAttribute(attribute);
        clientConnector.dispatchHandle(new ValueChangedCommand(attribute.getId(), "newValue"));
        Assert.assertEquals("newValue", attribute.getValue());
    }

    void testHandle_ValueChanged() {
        ClientAttribute attribute = new ClientAttribute("attr", "initialValue");
        dolphin.getModelStore().registerAttribute(attribute);

        clientConnector.dispatchHandle(new ValueChangedCommand(attribute.id, "newValue"));
        Assert.assertEquals("newValue", attribute.getValue());
    }

    void testHandle_CreatePresentationModelTwiceFails() {
        List<Map<String, Object>> attributes = new ArrayList<>();
        Map<String, Objects> map = new HashMap<String, Objects>();
        map.put("propertyName", "attr");
        map.put("value", "initialValue");
        map.put("qualifier", "qualifier");
        attributes.add(map);
        clientConnector.dispatchHandle(new CreatePresentationModelCommand("p1", "type", attributes));

        String msg = shouldFail {
            clientConnector.dispatchHandle(new CreatePresentationModelCommand("p1", "type", attributes));
        }
        Assert.assertEquals("There already is a presentation model with id 'p1' known to the client.", msg);
    }

    void testHandle_CreatePresentationModel() {
        List<Map<String, Object>> attributes = new ArrayList<>();
        Map<String, Objects> map = new HashMap<String, Objects>();
        map.put("propertyName", "attr");
        map.put("value", "initialValue");
        map.put("qualifier", "qualifier");
        attributes.add(map);

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

    void testHandle_CreatePresentationModel_ClientSideOnly() {
        List<Map<String, Object>> attributes = new ArrayList<>();
        Map map = new HashMap();
        map.put("propertyName", "attr");
        map.put("value", "initialValue");
        map.put("qualifier", "qualifier");
        attributes.add(map);
        clientConnector.dispatchHandle(new CreatePresentationModelCommand("p1", "type", attributes, true));
        Assert.assertNotNull(dolphin.getModelStore().findPresentationModelById("p1"));
        Assert.assertNotNull(dolphin.getModelStore().findPresentationModelById("p1").getAttribute("attr"));
        Assert.assertEquals("initialValue", dolphin.getModelStore().findPresentationModelById("p1").getAttribute("attr").getValue());
        Assert.assertEquals("qualifier", dolphin.getModelStore().findPresentationModelById("p1").getAttribute("attr").getQualifier());
        syncAndWaitUntilDone();
        assertOnlySyncCommandWasTransmitted();
    }

    void testHandle_CreatePresentationModel_MergeAttributesToExistingModel() {
        dolphin.getModelStore().createModel("p1", null);
        shouldFail(IllegalStateException) {
            clientConnector.dispatchHandle(new CreatePresentationModelCommand("p1", "type", Collections.emptyList()));
        }
    }

    void testHandle_DeletePresentationModel() {
        ClientPresentationModel p1 = dolphin.getModelStore().createModel("p1", null);
        p1.setClientSideOnly(true);
        ClientPresentationModel p2 = dolphin.getModelStore().createModel("p2", null);
        clientConnector.dispatchHandle(new DeletePresentationModelCommand(null));
        ClientPresentationModel model = new ClientPresentationModel("p3", Collections.emptyList());
        clientConnector.dispatchHandle(new DeletePresentationModelCommand(model.id));
        clientConnector.dispatchHandle(new DeletePresentationModelCommand(p1.id));
        clientConnector.dispatchHandle(new DeletePresentationModelCommand(p2.id));
        Assert.assertNull(dolphin.getModelStore().findPresentationModelById(p1.id));
        Assert.assertNull(dolphin.getModelStore().findPresentationModelById(p2.id));
        syncAndWaitUntilDone();
        // 3 commands will have been transferred:
        // 1: delete of p1 (causes no PresentationModelDeletedCommand since client side only)
        // 2: delete of p2
        // 3: PresentationModelDeletedCommand caused by delete of p2
        assertCommandsTransmitted(4);

        int deletedPresentationModelNotificationCount = 0;
        for(Command c : clientConnector.getTransmittedCommands()) {
            if(c instanceof PresentationModelDeletedCommand) {
                deletedPresentationModelNotificationCount = deletedPresentationModelNotificationCount + 1;
            }
        }

        assertEquals(1, deletedPresentationModelNotificationCount);
    }

    class TestClientConnector extends AbstractClientConnector {

        private List<Command> transmittedCommands = new ArrayList<>();

        public TestClientConnector(ClientModelStore modelStore, Executor uiExecutor) {
            super(modelStore, uiExecutor, new CommandBatcher(), new SimpleExceptionHandler(), Executors.newCachedThreadPool());
        }

        public int getTransmitCount() {
            return transmittedCommands.size();
        }

        public List<Command> transmit(List<Command> commands) {
            System.out.print("transmit: " + commands.size());
            LinkedList result = new LinkedList<Command>();
            for(Command cmd : commands) {
                result.addAll(transmitCommand(cmd));
            }
            return result;
        }

        @Override
        String getClientId() {
            return null;
        }

        public List<Command> transmitCommand(Command command) {
            System.out.print("transmitCommand: " + command);

            if (command != null && !(command instanceof StartLongPollCommand) && !(command instanceof InterruptLongPollCommand)) {
                transmittedCommands << command
            }
            return construct(command)
        }

        public List<Command> getTransmittedCommands() {
            return transmittedCommands
        }

        public List<AttributeMetadataChangedCommand> construct(ChangeAttributeMetadataCommand command) {
            return Collections.singletonList(new AttributeMetadataChangedCommand(command.getAttributeId(), command.getMetadataName(), command.getValue()));
        }

        public List construct(Command command) {
            return Collections.emptyList();
        }

    }

    class ExtendedAttribute extends ClientAttribute {

        private String additionalParam;

        ExtendedAttribute(String propertyName, Object initialValue, String qualifier) {
            super(propertyName, initialValue, qualifier);
        }

        String getAdditionalParam() {
            return additionalParam;
        }

        void setAdditionalParam(String additionalParam) {
            this.additionalParam = additionalParam;
        }
    }

}
