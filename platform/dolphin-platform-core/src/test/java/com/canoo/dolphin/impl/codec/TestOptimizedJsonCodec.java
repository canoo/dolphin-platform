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
package com.canoo.dolphin.impl.codec;

import com.canoo.implementation.dolphin.codec.OptimizedJsonCodec;
import org.hamcrest.Matchers;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.CreatePresentationModelCommand;
import org.opendolphin.core.comm.NamedCommand;
import org.opendolphin.core.comm.ValueChangedCommand;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class TestOptimizedJsonCodec {

    @Test
    public void shouldEncodeEmptyList() {
        final String actual = new OptimizedJsonCodec().encode(Collections.<Command>emptyList());
        assertThat(actual, is("[]"));
    }

    @Test
    public void shouldEncodeSingleCreatePresentationModelCommand() {
        final Command command = createCPMCommand();
        final String actual = new OptimizedJsonCodec().encode(Collections.singletonList(command));
        assertThat(actual, is("[" + createCPMCommandString() + "]"));
    }

    @Test
    public void shouldEncodeValueChangedCommandWithNulls() {
        final ValueChangedCommand command = new ValueChangedCommand();
        command.setOldValue(null);
        command.setNewValue(null);
        command.setAttributeId("3357S");
        final String actual = new OptimizedJsonCodec().encode(Collections.<Command>singletonList(command));
        assertThat(actual, is("[{\"a\":\"3357S\",\"id\":\"ValueChanged\"}]"));
    }

    @Test
    public void shouldEncodeValueChangedCommandWithStrings() {
        final ValueChangedCommand command = new ValueChangedCommand();
        command.setOldValue("Hello World");
        command.setNewValue("Good Bye");
        command.setAttributeId("3357S");
        final String actual = new OptimizedJsonCodec().encode(Collections.<Command>singletonList(command));
        assertThat(actual, is("[{\"a\":\"3357S\",\"o\":\"Hello World\",\"n\":\"Good Bye\",\"id\":\"ValueChanged\"}]"));
    }

    @Test
    public void shouldEncodeValueChangedCommandWithIntegers() {
        final ValueChangedCommand command = new ValueChangedCommand();
        command.setOldValue(41);
        command.setNewValue(42);
        command.setAttributeId("3357S");
        final String actual = new OptimizedJsonCodec().encode(Collections.<Command>singletonList(command));
        assertThat(actual, is("[{\"a\":\"3357S\",\"o\":41,\"n\":42,\"id\":\"ValueChanged\"}]"));
    }

    @Test
    public void shouldEncodeValueChangedCommandWithLong() {
        final ValueChangedCommand command = new ValueChangedCommand();
        command.setOldValue(1234567890987654321L);
        command.setNewValue(987654321234567890L);
        command.setAttributeId("3357S");
        final String actual = new OptimizedJsonCodec().encode(Collections.<Command>singletonList(command));
        assertThat(actual, is("[{\"a\":\"3357S\",\"o\":1234567890987654321,\"n\":987654321234567890,\"id\":\"ValueChanged\"}]"));
    }

    @Test
    public void shouldEncodeValueChangedCommandWithFloats() {
        final ValueChangedCommand command = new ValueChangedCommand();
        command.setOldValue(3.1415f);
        command.setNewValue(2.7182f);
        command.setAttributeId("3357S");
        final String actual = new OptimizedJsonCodec().encode(Collections.<Command>singletonList(command));
        assertThat(actual, is("[{\"a\":\"3357S\",\"o\":3.1415,\"n\":2.7182,\"id\":\"ValueChanged\"}]"));
    }

    @Test
    public void shouldEncodeValueChangedCommandWithDoubles() {
        final ValueChangedCommand command = new ValueChangedCommand();
        command.setOldValue(3.1415);
        command.setNewValue(2.7182);
        command.setAttributeId("3357S");
        final String actual = new OptimizedJsonCodec().encode(Collections.<Command>singletonList(command));
        assertThat(actual, is("[{\"a\":\"3357S\",\"o\":3.1415,\"n\":2.7182,\"id\":\"ValueChanged\"}]"));
    }

    @Test
    public void shouldEncodeValueChangedCommandWithBooleans() {
        final ValueChangedCommand command = new ValueChangedCommand();
        command.setOldValue(true);
        command.setNewValue(false);
        command.setAttributeId("3357S");
        final String actual = new OptimizedJsonCodec().encode(Collections.<Command>singletonList(command));
        assertThat(actual, is("[{\"a\":\"3357S\",\"o\":true,\"n\":false,\"id\":\"ValueChanged\"}]"));
    }

    @Test
    public void shouldEncodeSingleNamedCommand() {
        final Command command = createNamedCommand();
        final String actual = new OptimizedJsonCodec().encode(Collections.singletonList(command));
        assertThat(actual, is("[" + createNamedCommandString() + "]"));
    }

    @Test
    public void shouldEncodeTwoCustomCodecCommands() {
        final Command command = createCPMCommand();
        final String actual = new OptimizedJsonCodec().encode(Arrays.asList(command, command));
        final String expected = createCPMCommandString();
        assertThat(actual, is("[" + expected + "," + expected + "]"));
    }

    @Test
    public void shouldEncodeTwoStandardCodecCommands() {
        final Command command = createNamedCommand();
        final String actual = new OptimizedJsonCodec().encode(Arrays.asList(command, command));
        final String expected = createNamedCommandString();
        assertThat(actual, is("[" + expected + "," + expected + "]"));
    }

    @Test
    public void shouldEncodeCustomCodecCommandAndStandardCodecCommand() {
        final Command customCodecCommand = createCPMCommand();
        final Command standardCodecCommand = createNamedCommand();
        final String actual = new OptimizedJsonCodec().encode(Arrays.asList(customCodecCommand, standardCodecCommand));
        final String customCodecCommandString = createCPMCommandString();
        final String standardCodecCommandString = createNamedCommandString();
        assertThat(actual, is("[" + customCodecCommandString + "," + standardCodecCommandString + "]"));
    }

    @Test
    public void shouldEncodeStandardCodecCommandAndCustomCodecCommand() {
        final Command standardCodecCommand = createNamedCommand();
        final Command customCodecCommand = createCPMCommand();
        final String actual = new OptimizedJsonCodec().encode(Arrays.asList(standardCodecCommand, customCodecCommand));
        final String standardCodecCommandString = createNamedCommandString();
        final String customCodecCommandString = createCPMCommandString();
        assertThat(actual, is("[" + standardCodecCommandString + "," + customCodecCommandString + "]"));
    }



    @Test
    public void shouldDecodeEmptyList() {
        final List<Command> commands = new OptimizedJsonCodec().decode("[]");
        assertThat(commands, Matchers.<Command>empty());
    }

    @Test
    public void shouldDecodeValueChangedCommandWithNulls() {
        final List<Command> commands = new OptimizedJsonCodec().decode("[{\"a\":\"3357S\",\"id\":\"ValueChanged\"}]");

        final ValueChangedCommand command = new ValueChangedCommand();
        command.setOldValue(null);
        command.setNewValue(null);
        command.setAttributeId("3357S");
        assertThat(commands, hasSize(1));
        assertThat(commands.get(0), Matchers.<Command>samePropertyValuesAs(command));
    }

    @Test
    public void shouldDecodeValueChangedCommandWithStrings() {
        final List<Command> commands = new OptimizedJsonCodec().decode("[{\"a\":\"3357S\",\"o\":\"Hello World\",\"n\":\"Good Bye\",\"id\":\"ValueChanged\"}]");

        final ValueChangedCommand command = new ValueChangedCommand();
        command.setOldValue("Hello World");
        command.setNewValue("Good Bye");
        command.setAttributeId("3357S");
        assertThat(commands, hasSize(1));
        assertThat(commands.get(0), Matchers.<Command>samePropertyValuesAs(command));
    }

    @Test
    public void shouldDecodeValueChangedCommandWithIntegers() {
        final List<Command> commands = new OptimizedJsonCodec().decode("[{\"a\":\"3357S\",\"o\":41,\"n\":42,\"id\":\"ValueChanged\"}]");

        final ValueChangedCommand command = (ValueChangedCommand) commands.get(0);
        assertThat(command.getAttributeId(), is("3357S"));
        assertThat(((Number)command.getOldValue()).intValue(), is(41));
        assertThat(((Number)command.getNewValue()).intValue(), is(42));
    }

    @Test
    public void shouldDecodeValueChangedCommandWithLong() {
        final List<Command> commands = new OptimizedJsonCodec().decode("[{\"a\":\"3357S\",\"o\":1234567890987654321,\"n\":987654321234567890,\"id\":\"ValueChanged\"}]");

        final ValueChangedCommand command = (ValueChangedCommand) commands.get(0);
        assertThat(command.getAttributeId(), is("3357S"));
        assertThat(((Number)command.getOldValue()).longValue(), is(1234567890987654321L));
        assertThat(((Number)command.getNewValue()).longValue(), is(987654321234567890L));
    }

    @Test
    public void shouldDecodeValueChangedCommandWithDoubles() {
        final List<Command> commands = new OptimizedJsonCodec().decode("[{\"a\":\"3357S\",\"o\":3.1415,\"n\":2.7182,\"id\":\"ValueChanged\"}]");

        final ValueChangedCommand command = (ValueChangedCommand) commands.get(0);
        assertThat(command.getAttributeId(), is("3357S"));
        assertThat(((Number)command.getOldValue()).doubleValue(), closeTo(3.1415, 1e-6));
        assertThat(((Number)command.getNewValue()).doubleValue(), closeTo(2.7182, 1e-6));
    }

    @Test
    public void shouldDecodeValueChangedCommandWithBooleans() {
        final List<Command> commands = new OptimizedJsonCodec().decode("[{\"a\":\"3357S\",\"o\":true,\"n\":false,\"id\":\"ValueChanged\"}]");

        final ValueChangedCommand command = new ValueChangedCommand();
        command.setOldValue(true);
        command.setNewValue(false);
        command.setAttributeId("3357S");
        assertThat(commands, hasSize(1));
        assertThat(commands.get(0), Matchers.<Command>samePropertyValuesAs(command));
    }

    @Test
    public void shouldDecodeSingleNamedCommand() {
        final List<Command> commands = new OptimizedJsonCodec().decode("[" + createNamedCommandString() + "]");

        assertThat(commands, hasSize(1));
        assertThat(commands.get(0), Matchers.<Command>samePropertyValuesAs(createNamedCommand()));
    }



    private static CreatePresentationModelCommand createCPMCommand() {
        final CreatePresentationModelCommand command = new CreatePresentationModelCommand();
        command.setPmId("05ee43b7-a884-4d42-9fc5-00b083664eed");
        command.setClientSideOnly(false);
        command.setPmType("com.canoo.icos.casemanager.model.casedetails.CaseInfoBean");

        final Map<String, Object> sourceSystem = new HashMap<>();
        sourceSystem.put("propertyName", "@@@ SOURCE_SYSTEM @@@");
        sourceSystem.put("id", "3204S");
        sourceSystem.put("qualifier", null);
        sourceSystem.put("value", "server");
        sourceSystem.put("baseValue", "server");
        sourceSystem.put("tag", "VALUE");

        final Map<String, Object> caseDetailsLabel = new HashMap<>();
        caseDetailsLabel.put("propertyName", "caseDetailsLabel");
        caseDetailsLabel.put("id", "3205S");
        caseDetailsLabel.put("qualifier", null);
        caseDetailsLabel.put("value", null);
        caseDetailsLabel.put("baseValue", null);
        caseDetailsLabel.put("tag", "VALUE");

        final Map<String, Object> caseIdLabel = new HashMap<>();
        caseIdLabel.put("propertyName", "caseIdLabel");
        caseIdLabel.put("id", "3206S");
        caseIdLabel.put("qualifier", null);
        caseIdLabel.put("value", null);
        caseIdLabel.put("baseValue", null);
        caseIdLabel.put("tag", "VALUE");

        final Map<String, Object> statusLabel = new HashMap<>();
        statusLabel.put("propertyName", "statusLabel");
        statusLabel.put("id", "3207S");
        statusLabel.put("qualifier", null);
        statusLabel.put("value", null);
        statusLabel.put("baseValue", null);
        statusLabel.put("tag", "VALUE");

        final Map<String, Object> status = new HashMap<>();
        status.put("propertyName", "status");
        status.put("id", "3208S");
        status.put("qualifier", null);
        status.put("value", null);
        status.put("baseValue", null);
        status.put("tag", "VALUE");

        command.setAttributes(Arrays.asList(sourceSystem, caseDetailsLabel, caseIdLabel, statusLabel, status));

        return command;
    }

    private static String createCPMCommandString() {
        return
            "{" +
                "\"p\":\"05ee43b7-a884-4d42-9fc5-00b083664eed\"," +
                "\"t\":\"com.canoo.icos.casemanager.model.casedetails.CaseInfoBean\"," +
                "\"a\":[" +
                    "{" +
                        "\"n\":\"@@@ SOURCE_SYSTEM @@@\"," +
                        "\"i\":\"3204S\"," +
                        "\"v\":\"server\"" +
                    "},{" +
                        "\"n\":\"caseDetailsLabel\"," +
                        "\"i\":\"3205S\"" +
                    "},{" +
                        "\"n\":\"caseIdLabel\"," +
                        "\"i\":\"3206S\"" +
                    "},{" +
                        "\"n\":\"statusLabel\"," +
                        "\"i\":\"3207S\"" +
                    "},{" +
                        "\"n\":\"status\"," +
                        "\"i\":\"3208S\"" +
                    "}" +
                "]," +
                "\"id\":\"CreatePresentationModel\"" +
            "}";
    }

    private static NamedCommand createNamedCommand() {
        return new NamedCommand("dolphin_platform_intern_registerController");
    }

    private static String createNamedCommandString() {
        return "{\"id\":\"dolphin_platform_intern_registerController\",\"className\":\"org.opendolphin.core.comm.NamedCommand\"}";
    }
}
