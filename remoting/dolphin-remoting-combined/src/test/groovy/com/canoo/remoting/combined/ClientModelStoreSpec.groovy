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
import com.canoo.remoting.client.ClientModelStore
import com.canoo.remoting.client.ClientPresentationModel
import com.canoo.communication.common.ModelStoreEvent
import com.canoo.communication.common.ModelStoreListener
import com.canoo.remoting.server.ServerConnector
import spock.lang.Specification
/**
 * @author Dieter Holz
 */
class ClientModelStoreSpec extends Specification {
	def modelStore, pmType, pm, listener

	def setup(){
        def clientDolphin = new ClientDolphin()
		modelStore = new ClientModelStore(clientDolphin)
        clientDolphin.clientModelStore = modelStore
        clientDolphin.clientConnector = new InMemoryClientConnector(clientDolphin, [:] as ServerConnector)

		pmType = 'myType'
		pm = new ClientPresentationModel('myId', [])
		pm.setPresentationModelType(pmType)

		listener = Mock(ModelStoreListener)
		modelStore.addModelStoreListener(pmType, listener)
	}

	void "listeners are notified if PM is added to the clientModelStore"() {
		when:
		modelStore.add(pm)

		then:
		1 * listener.modelStoreChanged(new ModelStoreEvent(ModelStoreEvent.Type.ADDED, pm))
		0 * listener.modelStoreChanged(new ModelStoreEvent(ModelStoreEvent.Type.REMOVED, pm))
	}

	void "listeners are notified if PM is removed from clientModelStore"() {
		given:
		modelStore.add(pm)

		when:
		modelStore.remove(pm)

		then:
        0 * listener.modelStoreChanged(new ModelStoreEvent(ModelStoreEvent.Type.ADDED, pm))
        1 * listener.modelStoreChanged(new ModelStoreEvent(ModelStoreEvent.Type.REMOVED, pm))
	}

	void "listeners are not notified for different pmTypes"() {
		given:
		def otherPm = new ClientPresentationModel('otherId', [])
		otherPm.setPresentationModelType('otherType')

		when:
		modelStore.add(otherPm)
		modelStore.remove(otherPm)

		then:
        0 * listener.modelStoreChanged(new ModelStoreEvent(ModelStoreEvent.Type.ADDED, pm))
        0 * listener.modelStoreChanged(new ModelStoreEvent(ModelStoreEvent.Type.REMOVED, pm))
	}

    void "trying to delete a pm that is not known to the store is silently ignored"() {
        when:
        modelStore.delete(null)
        modelStore.delete(pm) // has not been added!
        then:
        0 * listener.modelStoreChanged(new ModelStoreEvent(ModelStoreEvent.Type.REMOVED, pm))
    }


}
