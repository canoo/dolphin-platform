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
package com.canoo.remoting.client.communication

import com.canoo.communication.common.commands.AttributeMetadataChangedCommand
import com.canoo.communication.common.commands.ChangeAttributeMetadataCommand
import com.canoo.communication.common.commands.Command
import com.canoo.communication.common.commands.CreatePresentationModelCommand
import com.canoo.communication.common.commands.DataCommand
import com.canoo.communication.common.commands.DeletePresentationModelCommand
import com.canoo.communication.common.commands.DeletedPresentationModelNotification
import com.canoo.communication.common.commands.EmptyNotification
import com.canoo.communication.common.commands.InitializeAttributeCommand
import com.canoo.communication.common.commands.ValueChangedCommand
import groovy.util.logging.Log
import com.canoo.communication.common.Attribute
import com.canoo.remoting.client.ClientAttribute
import com.canoo.remoting.client.ClientDolphin
import com.canoo.remoting.client.ClientModelStore
import com.canoo.remoting.client.ClientPresentationModel

import java.beans.PropertyChangeEvent
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ClientConnectorTests extends GroovyTestCase {

	TestClientConnector clientConnector
	ClientDolphin dolphin
	AttributeChangeListener attributeChangeListener

	/**
	 * Since command transmission is done in parallel to test execution thread the test method might finish
	 * before the command processing is complete. Therefore {@link #tearDown()} waits for this CountDownLatch
	 * (which btw. is initialized in {@link #setUp()} and decremented in the handler of a {@code dolphin.sync()} call).
	 * Also putting asserts in the callback handler of a {@code dolphin.sync()} call seems not to be reliable since JUnit
	 * seems not to be informed (reliably) of failing assertions.
	 *
	 * Therefore the following approach for the test methods has been taken to:
	 * - initialize the CountDownLatch in {@code testBaseValueChange#setup()}
	 * - after the 'act' section of a test method: call {@code syncAndWaitUntilDone()} which releases the latch inside a dolphin.sync handler and then (in the main thread) waits for the latch
	 * - performs all assertions
	 */
	CountDownLatch syncDone


	@Override
	protected void setUp() {

		dolphin = new ClientDolphin()
		clientConnector = new TestClientConnector(dolphin)
		clientConnector.uiThreadHandler = new RunLaterUiThreadHandler()
		dolphin.clientConnector = clientConnector
		dolphin.clientModelStore = new ClientModelStore(dolphin)
		attributeChangeListener = dolphin.clientModelStore.@attributeChangeListener

		initLatch()
	}

	private void initLatch() {
		syncDone = new CountDownLatch(1)
	}

	private boolean waitForLatch() {
		return syncDone.await(2, TimeUnit.SECONDS)
	}

	void syncAndWaitUntilDone() {
		dolphin.sync {
			syncDone.countDown()
		}
		assert waitForLatch()
	}

	void assertCommandsTransmitted(int count) {
		assert clientConnector.getTransmitCount() == count
	}

	void assertOnlySyncCommandWasTransmitted() {
		assertCommandsTransmitted(1)
		// 1 command was sent because of the sent sync (resulting in a EMPTY command):
		assert clientConnector.transmittedCommands[0] instanceof EmptyNotification
	}

	void testSevereLogWhenCommandNotFound() {
		clientConnector.dispatchHandle( new Command() )
		syncAndWaitUntilDone()
		assertOnlySyncCommandWasTransmitted()
	}

	void testHandleSimpleCreatePresentationModelCommand() {
		final myPmId = "myPmId"
		assert null == dolphin.getPresentationModel(myPmId)
		CreatePresentationModelCommand command = new CreatePresentationModelCommand()
		command.pmId = myPmId
		def result = clientConnector.dispatchHandle(command)
		assert myPmId == result.id
		assert dolphin.getPresentationModel(myPmId)
		syncAndWaitUntilDone()
		assertCommandsTransmitted(2)
	}

	//void testDefaultOnExceptionHandler() {
	//	clientConnector.uiThreadHandler = { it() } as UiThreadHandler
	//	String exceptionMessage = 'TestException thrown on purpose'
	//	def msg = shouldFail(RuntimeException) {
	//		clientConnector.getOnException().handle(new RuntimeException(exceptionMessage))
	//	}
	//	assert msg == exceptionMessage
	//}

	void testValueChange_OldAndNewValueSame() {
		attributeChangeListener.propertyChange(new PropertyChangeEvent("dummy", Attribute.VALUE_NAME, 'sameValue', 'sameValue'))
		syncAndWaitUntilDone()
		assertOnlySyncCommandWasTransmitted()
	}

	void testValueChange_noQualifier() {
		ClientAttribute attribute = new ClientAttribute('attr', 'initialValue')
		dolphin.clientModelStore.registerAttribute(attribute)
		attributeChangeListener.propertyChange(new PropertyChangeEvent(attribute, Attribute.VALUE_NAME, attribute.value, 'newValue'))
		syncAndWaitUntilDone()
		assertCommandsTransmitted(2)
		assert attribute.value == 'initialValue'
		assert clientConnector.transmittedCommands.any { it instanceof ValueChangedCommand }
	}

	void testValueChange_withQualifier() {
		syncDone = new CountDownLatch(1)

		ClientAttribute attribute = new ClientAttribute('attr', 'initialValue', 'qualifier')
		dolphin.clientModelStore.registerAttribute(attribute)
		attributeChangeListener.propertyChange(new PropertyChangeEvent(attribute, Attribute.VALUE_NAME, attribute.value, 'newValue'))
		syncAndWaitUntilDone()

		assertCommandsTransmitted(3)
		assert attribute.value == 'newValue'
		assert clientConnector.transmittedCommands.any { it instanceof ValueChangedCommand }
	}

	void testAddAttributeToPresentationModel_ClientSideOnly() {
		def clientPM = clientConnector.dispatchHandle(new CreatePresentationModelCommand(pmId: 'p1', pmType: 'type', clientSideOnly: true, attributes: [[propertyName: '1', value: 'initialValue1', qualifier: 'qualifier']]))
		clientConnector.clientDolphin.addAttributeToModel(clientPM, new ClientAttribute('2', 'initialValue2'))
		syncAndWaitUntilDone()
		assertOnlySyncCommandWasTransmitted()
	}

	void testAddTwoAttributesWithSameQualifierToSamePMIsNotAllowed() {
		shouldFail(IllegalStateException) {
			ClientPresentationModel presentationModel  = clientConnector.clientDolphin.presentationModel("1", new ClientAttribute("a", "0", "QUAL"))
			clientConnector.clientDolphin.addAttributeToModel(presentationModel, new ClientAttribute("c", "0", "QUAL"))
		}
	}

	void testAddTwoAttributesInConstructorWithSameQualifierToSamePMIsNotAllowed() {
		shouldFail(IllegalStateException) {
			clientConnector.clientDolphin.presentationModel("1", new ClientAttribute("a", "0", "QUAL"), new ClientAttribute("b", "0", "QUAL"))
		}
	}

	void testMetaDataChange_UnregisteredAttribute() {
		ClientAttribute attribute = new ExtendedAttribute('attr', 'initialValue', 'qualifier')
		attribute.additionalParam = 'oldValue'
		attributeChangeListener.propertyChange(new PropertyChangeEvent(attribute, 'additionalParam', null, 'newTag'))
		syncAndWaitUntilDone()
		assertCommandsTransmitted(2)
		assert ChangeAttributeMetadataCommand == clientConnector.transmittedCommands[0].class
		assert 'oldValue' == attribute.additionalParam
	}

	void testHandle_InitializeAttribute() {
		def syncedAttribute = new ClientAttribute('attr', 'initialValue', 'qualifier')
		dolphin.clientModelStore.registerAttribute(syncedAttribute)
		clientConnector.dispatchHandle(new InitializeAttributeCommand('p1', 'newProp', 'qualifier', 'newValue'))
		assert dolphin.getPresentationModel('p1')
		assert dolphin.getPresentationModel('p1').getAttribute('newProp')
		assert 'newValue' == dolphin.getPresentationModel('p1').getAttribute('newProp').value
		assert 'newValue' == syncedAttribute.value

	}

	void testHandle_InitializeAttribut_ExistingAttributeValueIsSet() {
		clientConnector.dispatchHandle(new InitializeAttributeCommand('p1', 'prop', null, 'initialValue'))
		clientConnector.dispatchHandle(new InitializeAttributeCommand('p1', 'prop', null, 'updatedValue'))
		assert dolphin.getPresentationModel('p1')
		assert dolphin.getPresentationModel('p1').getAttribute('prop')
		assert 'updatedValue' == dolphin.getPresentationModel('p1').getAttribute('prop').value
	}

	void testHandle_InitializeAttribute_NewValueNotSet() {
		def syncedAttribute = new ClientAttribute('attr', 'initialValue', 'qualifier')
		dolphin.clientModelStore.registerAttribute(syncedAttribute)
		clientConnector.dispatchHandle(new InitializeAttributeCommand('p1', 'newProp', 'qualifier', null))
		assert dolphin.getPresentationModel('p1')
		assert dolphin.getPresentationModel('p1').getAttribute('newProp')
		assert 'initialValue' == dolphin.getPresentationModel('p1').getAttribute('newProp').value
		assert 'initialValue' == syncedAttribute.value

	}
	void testHandle_InitializeAttribute_NewValueNotSet_and_firstOtherAttributeValueIsNull() {
		def syncedAttribute1 = new ClientAttribute('attr', null, 'qualifier')
		def syncedAttribute2 = new ClientAttribute('attr2', 'initialValue', 'qualifier')
		dolphin.clientModelStore.registerAttribute(syncedAttribute1)
		dolphin.clientModelStore.registerAttribute(syncedAttribute2)
		// null from 'syncedAttribute1' will be synchronized to other attributes since it is the first in the list of attributes with qualifier 'qualifier'
		clientConnector.dispatchHandle(new InitializeAttributeCommand('p1', 'newProp', 'qualifier', null))
		assert dolphin.getPresentationModel('p1')
		assert dolphin.getPresentationModel('p1').getAttribute('newProp')
		assert null == dolphin.getPresentationModel('p1').getAttribute('newProp').value
		assert null == syncedAttribute1.value
		assert null == syncedAttribute2.value
	}

	void testHandle_ValueChanged_AttrNotExists() {
		assert !clientConnector.dispatchHandle(new ValueChangedCommand(attributeId: 0, oldValue: 'oldValue', newValue: 'newValue'))
	}

	void testHandle_ValueChangedWithBadBaseValueIsIgnored() {
		def attribute = new ClientAttribute('attr', 'initialValue')
		dolphin.clientModelStore.registerAttribute(attribute)
		clientConnector.dispatchHandle(new ValueChangedCommand(attributeId: attribute.id, oldValue: 'no-such-base-value', newValue: 'newValue'))
		assert 'initialValue' == attribute.value
	}

	void testHandle_ValueChangedWithBadBaseValueIgnoredInNonStrictMode() {
		clientConnector.strictMode = false
		def attribute = new ClientAttribute('attr', 'initialValue')
		dolphin.clientModelStore.registerAttribute(attribute)
		clientConnector.dispatchHandle(new ValueChangedCommand(attributeId: attribute.id, oldValue: 'no-such-base-value', newValue: 'newValue'))
		assert 'newValue' == attribute.value
		clientConnector.strictMode = true // re-setting for later tests
	}

	void testHandle_ValueChanged() {
		def attribute = new ClientAttribute('attr', 'initialValue')
		dolphin.clientModelStore.registerAttribute(attribute)
		assert !clientConnector.dispatchHandle(new ValueChangedCommand(attributeId: attribute.id, oldValue: 'initialValue', newValue: 'newValue'))
		assert 'newValue' == attribute.value
	}

	void testHandle_CreatePresentationModelTwiceFails() {
		assert clientConnector.dispatchHandle(new CreatePresentationModelCommand(pmId: 'p1', pmType: 'type', attributes: [[propertyName: 'attr', value: 'initialValue', qualifier: 'qualifier']]))
		def msg = shouldFail {
			clientConnector.dispatchHandle(new CreatePresentationModelCommand(pmId: 'p1', pmType: 'type', attributes: [[propertyName: 'attr', value: 'initialValue', qualifier: 'qualifier']]))
		}
		assert "There already is a presentation model with id 'p1' known to the client." == msg
	}

	void testHandle_CreatePresentationModel() {
		assert clientConnector.dispatchHandle(new CreatePresentationModelCommand(pmId: 'p1', pmType: 'type', attributes: [[propertyName: 'attr', value: 'initialValue', qualifier: 'qualifier']]))
		assert dolphin.getPresentationModel('p1')
		assert dolphin.getPresentationModel('p1').getAttribute('attr')
		assert 'initialValue' == dolphin.getPresentationModel('p1').getAttribute('attr').value
		assert 'qualifier' == dolphin.getPresentationModel('p1').getAttribute('attr').qualifier
		syncAndWaitUntilDone()
		assertCommandsTransmitted(2)
		assert CreatePresentationModelCommand == clientConnector.transmittedCommands[0].class
	}

	void testHandle_CreatePresentationModel_ClientSideOnly() {
		assert clientConnector.dispatchHandle(new CreatePresentationModelCommand(pmId: 'p1', pmType: 'type', clientSideOnly: true, attributes: [[propertyName: 'attr', value: 'initialValue', qualifier: 'qualifier']]))
		assert dolphin.getPresentationModel('p1')
		assert dolphin.getPresentationModel('p1').getAttribute('attr')
		assert 'initialValue' == dolphin.getPresentationModel('p1').getAttribute('attr').value
		assert 'qualifier' == dolphin.getPresentationModel('p1').getAttribute('attr').qualifier
		syncAndWaitUntilDone()
		assertOnlySyncCommandWasTransmitted()
	}

	void testHandle_CreatePresentationModel_MergeAttributesToExistingModel() {
		dolphin.presentationModel('p1')
		shouldFail(IllegalStateException) {
			clientConnector.dispatchHandle(new CreatePresentationModelCommand(pmId: 'p1', pmType: 'type', attributes: []))
		}
	}

	void testHandle_DeletePresentationModel() {
		ClientPresentationModel p1 = dolphin.presentationModel('p1')
		p1.clientSideOnly = true
		ClientPresentationModel p2 = dolphin.presentationModel('p2')
		clientConnector.dispatchHandle(new DeletePresentationModelCommand(pmId: null))
		def model = new ClientPresentationModel('p3', [])
		clientConnector.dispatchHandle(new DeletePresentationModelCommand(pmId: model.id))
		clientConnector.dispatchHandle(new DeletePresentationModelCommand(pmId: p1.id))
		clientConnector.dispatchHandle(new DeletePresentationModelCommand(pmId: p2.id))
		assert !dolphin.getPresentationModel(p1.id)
		assert !dolphin.getPresentationModel(p2.id)
		syncAndWaitUntilDone()
		// 3 commands will have been transferred:
		// 1: delete of p1 (causes no DeletedPresentationModelNotification since client side only)
		// 2: delete of p2
		// 3: DeletedPresentationModelNotification caused by delete of p2
		assertCommandsTransmitted(4)
		assert 1 == clientConnector.transmittedCommands.findAll { it instanceof DeletedPresentationModelNotification }.size()
	}

	void testHandle_DataCommand() {
		def data = [k: 'v']
		assert data == clientConnector.dispatchHandle(new DataCommand(data))
	}


	@Log
	class TestClientConnector extends AbstractClientConnector {

		List<Command> transmittedCommands = []

		TestClientConnector(ClientDolphin clientDolphin) {
			super(clientDolphin)
		}

		int getTransmitCount() {
			transmittedCommands.size()
		}

		List<Command> transmit(List<Command> commands) {
			println "transmit: ${commands.size()}"
			def result = new LinkedList<Command>()
			commands.each() { Command cmd ->
				result.addAll(transmitCommand(cmd))
			}
			result
		}

		List<Command> transmitCommand(Command command) {
			println "transmitCommand: $command"
			transmittedCommands << command
			return construct(command)
		}

		List construct(ChangeAttributeMetadataCommand command) {
			[new AttributeMetadataChangedCommand(attributeId: command.attributeId, metadataName: command.metadataName, value: command.value)]
		}

		List construct(Command command) {
			[]
		}

	}

	class ExtendedAttribute extends ClientAttribute {
		String additionalParam

		ExtendedAttribute(String propertyName, Object initialValue, String qualifier) {
			super(propertyName, initialValue, qualifier)
		}
	}

}
