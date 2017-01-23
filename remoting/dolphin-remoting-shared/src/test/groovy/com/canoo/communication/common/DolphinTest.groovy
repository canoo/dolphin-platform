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
import com.canoo.communication.common.ModelStore
import spock.lang.Specification

class DolphinTest extends Specification {

    def "finding all attributes per qualifier must delegate to the model store"() {
        given:
        def attributeMock = Mock(Attribute)
        def storeStub = new ModelStore() {
            @Override
            List<Attribute> findAllAttributesByQualifier(String qualifier) {
                return [attributeMock]
            }
        }
        def dolphin = new AbstractDolphin() {
            @Override
            public ModelStore getModelStore() {
                return storeStub
            }
        }
        when:
        def result = dolphin.findAllAttributesByQualifier("whatever")

        then:
        result.size() == 1
        result[0] == attributeMock
    }
}
