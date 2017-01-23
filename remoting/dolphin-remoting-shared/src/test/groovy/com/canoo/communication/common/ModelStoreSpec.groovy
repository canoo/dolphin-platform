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

import com.canoo.communication.common.AbstractDolphin
import com.canoo.communication.common.Attribute
import com.canoo.communication.common.BaseAttribute
import com.canoo.communication.common.BasePresentationModel
import com.canoo.communication.common.ModelStore
import spock.lang.Specification

class ModelStoreSpec extends Specification {
    def "pmid is unique"() {
        given:
        def pmA = new BasePresentationModel('myId', [])
        def pmB = new BasePresentationModel('myId', [])

		def modelStore = new ModelStore()

		when:
		modelStore.add(pmA)
		modelStore.add(pmB)

        then:
		thrown(IllegalArgumentException)
    }

    def "a pm can be retrieved by type: empty"() {
        given:
        def modelStore = new ModelStore()
        def dolphin = new AbstractDolphin() {
            ModelStore getModelStore() { modelStore}
        }
        expect:
        [] == dolphin.findAllPresentationModelsByType("no such type")
    }

    def "a pm can be retrieved by type: one"() {
        given:
        def modelStore = new ModelStore()
        def dolphin = new AbstractDolphin() {
            ModelStore getModelStore() { modelStore }
        }
        def bpm = new BasePresentationModel(null,[])
        bpm.presentationModelType = "type"
        modelStore.add bpm
        expect:
        [bpm] == dolphin.findAllPresentationModelsByType("type")
    }

    def "a pm can be retrieved by type: many"() {
        given:
        def modelStore = new ModelStore()
        def dolphin = new AbstractDolphin() {
            ModelStore getModelStore() { modelStore }
        }
        def bpm1 = new BasePresentationModel("1",[])
        def bpm2 = new BasePresentationModel("2",[])
        def bpm3 = new BasePresentationModel("3",[])
        bpm1.presentationModelType = "type"
        bpm2.presentationModelType = "type"
        bpm3.presentationModelType = "some other type"
        modelStore.add bpm1
        modelStore.add bpm2
        modelStore.add bpm3

        def result = dolphin.findAllPresentationModelsByType("type")
        expect:
        2 == result.size()
        bpm1 in result
        bpm2 in result
    }

    def "add and remove a pm including type and qualifier"() {
        given:
        def modelStore = new ModelStore()
        def bpm1 = new BasePresentationModel("pm1",[new BaseAttribute('attr','val','quali') {
            @Override
            String getOrigin() {
                return "B"
            }
        } ])
        bpm1.presentationModelType = "type"

        when:
        modelStore.add bpm1
        then:
        'val' == modelStore.findPresentationModelById('pm1').getAttribute("attr").value

        when:
        modelStore.remove bpm1
        then:
        null == modelStore.findPresentationModelById('pm1')
    }

    def "try to register an attribute twice"() {
        given:
        def modelStore = new ModelStore()
        def attr = new BaseAttribute('attr','val','quali') {
            @Override
            String getOrigin() {
                return "B"
            }
        }

        when:
        modelStore.registerAttribute(attr)
        modelStore.registerAttribute(attr)
        then:
        attr.getPropertyChangeListeners(Attribute.QUALIFIER_NAME).size() == 1
    }
}
