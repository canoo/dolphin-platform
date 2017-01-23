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

import spock.lang.Specification

class BasePresentationModelSpec extends Specification {

    def "get attributes for a given property name"() {
        given:
        def value = new MyAttribute("name",  null)
        def label = new MyAttribute("nameLabel",  null)
        def other = new MyAttribute("other", null)


        when:
        def fullModel  = new BasePresentationModel("1", [value, label, other])
        def emptyModel = new BasePresentationModel("2", [])

        then:

        fullModel.getAttribute("name")         == value
        fullModel.getAttribute("other")        == other
        fullModel.getAttribute("no-such-name") == null
        fullModel.getAttribute(null)           == null

        emptyModel.getAttribute("name")        == null

        value.getPresentationModel().getAttribute(value.getPropertyName()) == value

    }

    def "you can not add an attribute to two presentation models"() {
        given:
        def model1 = new BasePresentationModel("1", [])
        def model2 = new BasePresentationModel("2", [])
        def attribute = new MyAttribute("name")

        when:
        model1._internal_addAttribute(attribute)
        model2._internal_addAttribute(attribute)

        then:
        thrown IllegalStateException
    }

    def "you can not add two attributes with the same property name and tag"() {
        given:
        def model = new BasePresentationModel("1", [])
        def attribute1 = new MyAttribute("name", null)
        def attribute2 = new MyAttribute("name", null)

        when:
        model._internal_addAttribute(attribute1)
        model._internal_addAttribute(attribute2)

        then:
        thrown IllegalStateException
    }

    def "attributes are accessible as properties"() {
        given:

        def baseAttribute = new MyAttribute('myPropName')
        def pm = new BasePresentationModel('1',[baseAttribute])

        expect:

        pm.attributes.find { it.propertyName == 'myPropName' } == baseAttribute // old style
        pm.getAttribute("myPropName") == baseAttribute  // new style
    }

    def "missing attributes throw MissingPropertyException on access"() {
        given:
        def baseAttribute = new MyAttribute('myPropName')
        def pm = new BasePresentationModel('1',[baseAttribute])

        when:
        pm.noSuchAttributeName

        then:
        def exception = thrown(MissingPropertyException)
        exception.message.contains('noSuchAttributeName')
    }

    def "finder methods"() {
        given:
        def baseAttribute = new MyAttribute('myInt',1)
        baseAttribute.qualifier = 'myQualifier'
        def pm = new BasePresentationModel('1',[baseAttribute])

        expect:
        null == pm.getAttribute(null) // null safe

        null == pm.findAttributeByQualifier('no-such-qualifier')
        baseAttribute == pm.findAttributeByQualifier('myQualifier')

        null == pm.findAttributeById(Long.MAX_VALUE.toString())
        baseAttribute == pm.findAttributeById(baseAttribute.id)
    }
}