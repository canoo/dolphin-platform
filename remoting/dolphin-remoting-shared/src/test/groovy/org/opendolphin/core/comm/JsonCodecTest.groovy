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
package org.opendolphin.core.comm

public class JsonCodecTest extends GroovyTestCase {

    void testEmpty() {
        assertSoManyCommands(0)
    }

    void testOne() {
        assertSoManyCommands(1)
    }

    void testMany() {
        assertSoManyCommands(10)
    }

    void assertSoManyCommands(int count) {
        def codec = new JsonCodec()
        def commands = []
        count.times {
            commands << new EmptyNotification()
        }
        def coded = codec.encode(commands)
        def decoded = codec.decode(coded)
        assert commands.toString() == decoded.toString()
    }

    void testCodingCreatePresentationModelCommandWithDisallowedSelfReflectiveMapEntry() {
        def map = [propertyName: 'x', qualifier: null]
        map.value = map
        shouldFail {
            assertCodingCreatePresentationModel(map)
        }
    }

    void testCodingCreatePresentationModelWithStructuredEntry() {
        def map = [propertyName: 'x', qualifier: null]
        map.value = "ok"
        assertCodingCreatePresentationModel(map)
    }

    void testCodingCreatePresentationModelWithEmptyAttributes() {
        assertCodingCreatePresentationModel([:])
    }

    void assertCodingCreatePresentationModel(Map attributes) {
        def codec = new JsonCodec()
        def commands = []
        commands << new CreatePresentationModelCommand(pmId: "bla", attributes: [attributes])
        def coded = codec.encode(commands)
        def decoded = codec.decode(coded)

        println commands.toString().toList().sort()
        println decoded.toString().toList().sort()

        assert commands.toString().toList().sort() == decoded.toString().toList().sort() // ;-)
    }

    void testCodingSpecialCharacters() {
        def codec = new JsonCodec()
        def commands = []
        def specialChars = "äöüéèà ☺ "
        commands << new CreatePresentationModelCommand(pmId: specialChars, attributes: [[attr: specialChars]])
        def coded = codec.encode(commands)
        def decoded = codec.decode(coded)
        CreatePresentationModelCommand cmd = decoded.first()
        assert cmd.pmId == specialChars
        assert cmd.attributes.first().attr == specialChars
    }

    void testCodingCommands() {
        assertCodingCommand(new CreatePresentationModelCommand())
        assertCodingCommand(new QualifierChangedCommand("a", "c"))
        assertCodingCommand(new ValueChangedCommand())
    }

    void assertCodingCommand(Command command) {
        def codec = new JsonCodec()
        def commands = [command]
        def coded = codec.encode(commands)
        def decoded = codec.decode(coded)
        assert commands.toString().toList().sort() == decoded.toString().toList().sort()
    }

    void testProperTypeEnAndDecoding() {
        assertCorrectEnAndDecoding('n', 't')
        assertCorrectEnAndDecoding('\n', '\t')
        assertCorrectEnAndDecoding("String", "newString")
        assertCorrectEnAndDecoding(true, false)
        assertCorrectEnAndDecoding(Integer.MAX_VALUE, Integer.MIN_VALUE)
        assertCorrectEnAndDecoding(Long.MAX_VALUE, Long.MIN_VALUE)
        assertCorrectEnAndDecoding(new Date(1900, 0, 0), new Date(2000, 0, 0))
        assertCorrectEnAndDecoding(new BigDecimal(Double.MAX_VALUE), new BigDecimal(Double.MIN_VALUE))
        // is decoded as Long !
        assertCorrectEnAndDecoding(Double.MAX_VALUE, Double.MIN_VALUE)              // is decoded as BigDecimal !
        assertCorrectEnAndDecoding(Float.MAX_VALUE, Float.MIN_VALUE)                // is decoded as BigDecimal !
    }

    private void assertCorrectEnAndDecoding(Object oldValue, Object newValue) {
        def codec = new JsonCodec()
        def in_command = new ValueChangedCommand("bla", oldValue, newValue);
        def coded = codec.encode([in_command])

        def out_command = codec.decode(coded)[0];
        assert in_command != out_command;
        assert in_command.attributeId == out_command.attributeId
//        assert in_command.oldValue.class == out_command.oldValue.class
        assert in_command.oldValue == out_command.oldValue
  //      assert in_command.newValue.class == out_command.newValue.class
        assert in_command.newValue == out_command.newValue
    }

    /**
     * locale might be different for client and server
     */
    void testProperTypeEnAndDeAndEnAndDecodingADateDifferentLocale() {

        def cpmc = new CreatePresentationModelCommand();
        cpmc.attributes << [
                propertyName: "theDate",
                id          : "0",
                qualifier   : "1",
                value       : new Date(),
                baseValue   : new Date(),
        ]

        cpmc.pmId = "untilDate0"
        cpmc.pmType = "aDate"

        def theOriginalValue = cpmc.attributes.get(0).get("value")
        def codec = new JsonCodec()
        def locale = Locale.getDefault()

        try {
            Locale.setDefault(Locale.GERMAN)
            def encoded0 = codec.encode([cpmc])
            Locale.setDefault(Locale.US)
            def decoded0 = codec.decode(encoded0)[0]
            // without proper transport encoding, we would end up here with:
            // java.text.ParseException: Unparseable date: "27.02.2016"

            def encoded1 = codec.encode([decoded0])
            Locale.setDefault(Locale.GERMAN)
            def decoded1 = codec.decode(encoded1)[0]

            assert Date.class.cast(decoded1.attributes.get(0).get("value")).compareTo(Date.class.cast(theOriginalValue)) == 0

        } catch (Exception e) {
            Locale.setDefault(locale)
            throw e
        }
        Locale.setDefault(locale)
    }

    /**
     * this test works until 5ca3b2b but not beyond
     * crashes at [2] with "Attribute values of this type are not allowed: LazyMap"
     */
    void testProperTypeEnAndDeAndEnAndDecodingADate() {

        def cpmc = new CreatePresentationModelCommand();
        cpmc.attributes << [
                propertyName: "theDate",
                id          : "0",
                qualifier   : "1",
                value       : new Date(),
                baseValue   : new Date()
        ]
        cpmc.pmId = "untilDate0"
        cpmc.pmType = "aDate"

        def codec = new JsonCodec()

        // [0] from one end
        def encoded0 = codec.encode([cpmc])
        // [1] to the other
        def decoded0 = codec.decode(encoded0)[0];
        // [2] and back
        def encoded1 = codec.encode([decoded0])
        // should work too
        def decoded1 = codec.decode(encoded1)[0];

        assert decoded1 != null;
    }

    /**
     * this test works until 7fd89dd and beyond
     */
    void testProperTypeEnAndDeAndEnAndDecodingAString() {

        def cpmc = new CreatePresentationModelCommand();
        cpmc.attributes << [
                propertyName: "theMessage",
                id          : "0",
                qualifier   : "1",
                value       : "momo was here",
                baseValue   : "momo was here"
        ]
        cpmc.pmId = "aName"
        cpmc.pmType = "aString"

        def codec = new JsonCodec()

        // from one end
        def encoded0 = codec.encode([cpmc])
        // to the other
        def decoded0 = codec.decode(encoded0)[0];
        // and back
        def encoded1 = codec.encode([decoded0])
        // should work too
        def decoded1 = codec.decode(encoded1)[0];

        assert decoded1 != null;
    }
}
