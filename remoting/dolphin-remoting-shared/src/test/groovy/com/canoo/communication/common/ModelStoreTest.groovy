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
package com.canoo.communication.common

class ModelStoreTest extends GroovyTestCase {

    void testSimpleAccessAndStoreEventListening() {
        PresentationModel parent = new BasePresentationModel("0", [])
        parent.presentationModelType = 'parent'
        PresentationModel child1 = new BasePresentationModel("1", [])

        TestStoreListener storeListener = new TestStoreListener()
        TestStoreListener parentStoreListener = new TestStoreListener()

        ModelStore modelStore = new ModelStore()
        modelStore.addModelStoreListener(storeListener)
        modelStore.addModelStoreListener('parent', parentStoreListener)

        modelStore.add(parent)

        assert storeListener.event
        assert storeListener.event.toString()
        assert storeListener.event.hashCode()
        assert storeListener.event == storeListener.event
        assert storeListener.event != null
        assert storeListener.event != new Object()
        assert storeListener.event.presentationModel == parent
        assert storeListener.event.type == ModelStoreEvent.Type.ADDED
        assert parentStoreListener.event
        assert parentStoreListener.event.presentationModel == parent
        assert parentStoreListener.event.type == ModelStoreEvent.Type.ADDED

        storeListener.event = null
        parentStoreListener.event = null

        modelStore.add(child1)

        assert storeListener.event
        assert storeListener.event.presentationModel == child1
        assert storeListener.event.type == ModelStoreEvent.Type.ADDED
        assert !parentStoreListener.event
    }
}


class TestStoreListener implements ModelStoreListener {
    ModelStoreEvent event

    @Override
    void modelStoreChanged(ModelStoreEvent event) {
        this.event = event
    }
}

