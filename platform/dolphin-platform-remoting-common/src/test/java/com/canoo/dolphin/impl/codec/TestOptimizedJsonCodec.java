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
package com.canoo.dolphin.impl.codec;

import com.canoo.dp.impl.remoting.codec.OptimizedJsonCodec;
import com.canoo.dp.impl.remoting.commands.CallActionCommand;
import com.canoo.dp.impl.remoting.legacy.core.Attribute;
import org.hamcrest.Matchers;
import com.canoo.dp.impl.remoting.legacy.commands.Command;
import com.canoo.dp.impl.remoting.legacy.commands.CreatePresentationModelCommand;
import com.canoo.dp.impl.remoting.legacy.commands.EmptyCommand;
import com.canoo.dp.impl.remoting.legacy.commands.ValueChangedCommand;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TestOptimizedJsonCodec {

    @Test
    public void shouldEncodeEmptyList() {
        final String actual = OptimizedJsonCodec.getInstance().encode(Collections.<Command>emptyList());
        assertThat(actual, is("[]"));
    }

    @Test
    public void shouldEncodeSingleCreatePresentationModelCommand() {
        final Command command = createCPMCommand();
        final String actual = OptimizedJsonCodec.getInstance().encode(Collections.singletonList(command));
        assertThat(actual, is("[" + createCPMCommandString() + "]"));
    }

    @Test
    public void shouldEncodeCallActionCommand() {
        final CallActionCommand command = new CallActionCommand();
        command.setControllerId("4711");
        command.setActionName("action");
        final String actual = OptimizedJsonCodec.getInstance().encode(Collections.<Command>singletonList(command));
        assertThat(actual, is("[{\"c_id\":\"4711\",\"n\":\"action\",\"p\":[],\"id\":\"CallAction\"}]"));
    }

    @Test
    public void shouldEncodeCallActionWithParamsCommand() {
        final CallActionCommand command = new CallActionCommand();
        command.setControllerId("4711");
        command.setActionName("action");
        command.addParam("A", 1);
        command.addParam("B", 7.6);
        command.addParam("C", true);
        command.addParam("D", null);
        command.addParam("E", "Hello");
        final String actual = OptimizedJsonCodec.getInstance().encode(Collections.<Command>singletonList(command));
        assertThat(actual, is("[{\"c_id\":\"4711\",\"n\":\"action\",\"p\":[{\"n\":\"A\",\"v\":1},{\"n\":\"B\",\"v\":7.6},{\"n\":\"C\",\"v\":true},{\"n\":\"D\",\"v\":null},{\"n\":\"E\",\"v\":\"Hello\"}],\"id\":\"CallAction\"}]"));
    }

    @Test
    public void shouldEncodeValueChangedCommandWithNulls() {
        final ValueChangedCommand command = new ValueChangedCommand();
        command.setNewValue(null);
        command.setAttributeId("3357S");
        final String actual = OptimizedJsonCodec.getInstance().encode(Collections.<Command>singletonList(command));
        assertThat(actual, is("[{\"a_id\":\"3357S\",\"id\":\"ValueChanged\"}]"));
    }

    @Test
    public void shouldEncodeValueChangedCommandWithStrings() {
        final ValueChangedCommand command = new ValueChangedCommand();
        command.setNewValue("Good Bye");
        command.setAttributeId("3357S");
        final String actual = OptimizedJsonCodec.getInstance().encode(Collections.<Command>singletonList(command));
        assertThat(actual, is("[{\"a_id\":\"3357S\",\"v\":\"Good Bye\",\"id\":\"ValueChanged\"}]"));
    }

    @Test
    public void shouldEncodeValueChangedCommandWithIntegers() {
        final ValueChangedCommand command = new ValueChangedCommand();
        command.setNewValue(42);
        command.setAttributeId("3357S");
        final String actual = OptimizedJsonCodec.getInstance().encode(Collections.<Command>singletonList(command));
        assertThat(actual, is("[{\"a_id\":\"3357S\",\"v\":42,\"id\":\"ValueChanged\"}]"));
    }

    @Test
    public void shouldEncodeValueChangedCommandWithLong() {
        final ValueChangedCommand command = new ValueChangedCommand();
        command.setNewValue(987654321234567890L);
        command.setAttributeId("3357S");
        final String actual = OptimizedJsonCodec.getInstance().encode(Collections.<Command>singletonList(command));
        assertThat(actual, is("[{\"a_id\":\"3357S\",\"v\":987654321234567890,\"id\":\"ValueChanged\"}]"));
    }

    @Test
    public void shouldEncodeValueChangedCommandWithFloats() {
        final ValueChangedCommand command = new ValueChangedCommand();
        command.setNewValue(2.7182f);
        command.setAttributeId("3357S");
        final String actual = OptimizedJsonCodec.getInstance().encode(Collections.<Command>singletonList(command));
        assertThat(actual, is("[{\"a_id\":\"3357S\",\"v\":2.7182,\"id\":\"ValueChanged\"}]"));
    }

    @Test
    public void shouldEncodeValueChangedCommandWithDoubles() {
        final ValueChangedCommand command = new ValueChangedCommand();
        command.setNewValue(2.7182);
        command.setAttributeId("3357S");
        final String actual = OptimizedJsonCodec.getInstance().encode(Collections.<Command>singletonList(command));
        assertThat(actual, is("[{\"a_id\":\"3357S\",\"v\":2.7182,\"id\":\"ValueChanged\"}]"));
    }

    @Test
    public void shouldEncodeValueChangedCommandWithBooleans() {
        final ValueChangedCommand command = new ValueChangedCommand();
        command.setNewValue(false);
        command.setAttributeId("3357S");
        final String actual = OptimizedJsonCodec.getInstance().encode(Collections.<Command>singletonList(command));
        assertThat(actual, is("[{\"a_id\":\"3357S\",\"v\":false,\"id\":\"ValueChanged\"}]"));
    }

    @Test
    public void shouldEncodeSingleNamedCommand() {
        final Command command = createCommand();
        final String actual = OptimizedJsonCodec.getInstance().encode(Collections.singletonList(command));
        assertThat(actual, is("[" + createCommandJsonString() + "]"));
    }

    @Test
    public void shouldEncodeTwoCustomCodecCommands() {
        final Command command = createCPMCommand();
        final String actual = OptimizedJsonCodec.getInstance().encode(Arrays.asList(command, command));
        final String expected = createCPMCommandString();
        assertThat(actual, is("[" + expected + "," + expected + "]"));
    }

    @Test
    public void shouldEncodeTwoStandardCodecCommands() {
        final Command command = createCommand();
        final String actual = OptimizedJsonCodec.getInstance().encode(Arrays.asList(command, command));
        final String expected = createCommandJsonString();
        assertThat(actual, is("[" + expected + "," + expected + "]"));
    }

    @Test
    public void shouldEncodeCustomCodecCommandAndStandardCodecCommand() {
        final Command customCodecCommand = createCPMCommand();
        final Command standardCodecCommand = createCommand();
        final String actual = OptimizedJsonCodec.getInstance().encode(Arrays.asList(customCodecCommand, standardCodecCommand));
        final String customCodecCommandString = createCPMCommandString();
        final String standardCodecCommandString = createCommandJsonString();
        assertThat(actual, is("[" + customCodecCommandString + "," + standardCodecCommandString + "]"));
    }

    @Test
    public void shouldEncodeStandardCodecCommandAndCustomCodecCommand() {
        final Command standardCodecCommand = createCommand();
        final Command customCodecCommand = createCPMCommand();
        final String actual = OptimizedJsonCodec.getInstance().encode(Arrays.asList(standardCodecCommand, customCodecCommand));
        final String standardCodecCommandString = createCommandJsonString();
        final String customCodecCommandString = createCPMCommandString();
        assertThat(actual, is("[" + standardCodecCommandString + "," + customCodecCommandString + "]"));
    }



    @Test
    public void shouldDecodeEmptyList() {
        final List<Command> commands = OptimizedJsonCodec.getInstance().decode("[]");
        assertThat(commands, Matchers.<Command>empty());
    }

    @Test
    public void shouldDecodeValueChangedCommandWithNulls() {
        final List<Command> commands = OptimizedJsonCodec.getInstance().decode("[{\"a_id\":\"3357S\",\"id\":\"ValueChanged\"}]");

        final ValueChangedCommand command = new ValueChangedCommand();
        command.setNewValue(null);
        command.setAttributeId("3357S");
        assertThat(commands, hasSize(1));
        assertThat(commands.get(0), Matchers.<Command>samePropertyValuesAs(command));
    }

    @Test
    public void shouldDecodeValueChangedCommandWithStrings() {
        final List<Command> commands = OptimizedJsonCodec.getInstance().decode("[{\"a_id\":\"3357S\",\"v\":\"Good Bye\",\"id\":\"ValueChanged\"}]");

        final ValueChangedCommand command = new ValueChangedCommand();
        command.setNewValue("Good Bye");
        command.setAttributeId("3357S");
        assertThat(commands, hasSize(1));
        assertThat(commands.get(0), Matchers.<Command>samePropertyValuesAs(command));
    }

    @Test
    public void shouldDecodeValueChangedCommandWithIntegers() {
        final List<Command> commands = OptimizedJsonCodec.getInstance().decode("[{\"a_id\":\"3357S\",\"v\":42,\"id\":\"ValueChanged\"}]");

        final ValueChangedCommand command = (ValueChangedCommand) commands.get(0);
        assertThat(command.getAttributeId(), is("3357S"));
        assertThat(((Number)command.getNewValue()).intValue(), is(42));
    }

    @Test
    public void shouldDecodeValueChangedCommandWithLong() {
        final List<Command> commands = OptimizedJsonCodec.getInstance().decode("[{\"a_id\":\"3357S\",\"v\":987654321234567890,\"id\":\"ValueChanged\"}]");

        final ValueChangedCommand command = (ValueChangedCommand) commands.get(0);
        assertThat(command.getAttributeId(), is("3357S"));
        assertThat(((Number)command.getNewValue()).longValue(), is(987654321234567890L));
    }

    @Test
    public void shouldDecodeValueChangedCommandWithDoubles() {
        final List<Command> commands = OptimizedJsonCodec.getInstance().decode("[{\"a_id\":\"3357S\",\"v\":2.7182,\"id\":\"ValueChanged\"}]");

        final ValueChangedCommand command = (ValueChangedCommand) commands.get(0);
        assertThat(command.getAttributeId(), is("3357S"));
        assertThat(((Number)command.getNewValue()).doubleValue(), closeTo(2.7182, 1e-6));
    }

    @Test
    public void shouldDecodeValueChangedCommandWithBigDecimal() {
        final List<Command> commands = OptimizedJsonCodec.getInstance().decode("[{\"a_id\":\"3357S\",\"v\":2.7182,\"id\":\"ValueChanged\"}]");

        final ValueChangedCommand command = (ValueChangedCommand) commands.get(0);
        assertThat(command.getAttributeId(), is("3357S"));
        assertThat(((Number) command.getNewValue()).doubleValue(), is(2.7182));
    }

    @Test
    public void shouldDecodeValueChangedCommandWithBigInteger() {
        final List<Command> commands = OptimizedJsonCodec.getInstance().decode("[{\"a_id\":\"3357S\",\"v\":987654321234567890,\"id\":\"ValueChanged\"}]");

        final ValueChangedCommand command = (ValueChangedCommand) commands.get(0);
        assertThat(command.getAttributeId(), is("3357S"));
        assertThat(((Number) command.getNewValue()).longValue(), is(987654321234567890L));
    }

    @Test
    public void shouldDecodeValueChangedCommandWithUuid() {
        final List<Command> commands = OptimizedJsonCodec.getInstance().decode("[{\"a_id\":\"3357S\",\"v\":\"{4b9e93fd-3738-4fe6-b2a4-1fea8d2e0dc4}\",\"id\":\"ValueChanged\"}]");

        final ValueChangedCommand command = (ValueChangedCommand) commands.get(0);
        assertThat(command.getAttributeId(), is("3357S"));
        assertThat(command.getNewValue().toString(), is("{4b9e93fd-3738-4fe6-b2a4-1fea8d2e0dc4}"));
    }

    @Test
    public void shouldDecodeValueChangedCommandWithBooleans() {
        final List<Command> commands = OptimizedJsonCodec.getInstance().decode("[{\"a_id\":\"3357S\",\"v\":false,\"id\":\"ValueChanged\"}]");

        final ValueChangedCommand command = new ValueChangedCommand();
        command.setNewValue(false);
        command.setAttributeId("3357S");
        assertThat(commands, hasSize(1));
        assertThat(commands.get(0), Matchers.<Command>samePropertyValuesAs(command));
    }

    @Test
    public void shouldDecodeSingleNamedCommand() {
        final List<Command> commands = OptimizedJsonCodec.getInstance().decode("[" + createCommandJsonString() + "]");

        assertThat(commands, hasSize(1));
        assertThat(commands.get(0), Matchers.<Command>samePropertyValuesAs(createCommand()));
    }

    @Test
    public void shouldDecodeCallActionCommand() {
        //given:
        final String json = "[{\"c_id\":\"4711\",\"n\":\"action\",\"p\":[],\"id\":\"CallAction\"}]";

        //when:
        final List<Command> commands = OptimizedJsonCodec.getInstance().decode(json);

        Assert.assertNotNull(commands);
        Assert.assertEquals(commands.size(), 1);
        Assert.assertNotNull(commands.get(0));
        Assert.assertEquals(commands.get(0).getClass(), CallActionCommand.class);
        Assert.assertEquals(((CallActionCommand)commands.get(0)).getActionName(), "action");
        Assert.assertEquals(((CallActionCommand)commands.get(0)).getControllerId(), "4711");
        Assert.assertNotNull(((CallActionCommand)commands.get(0)).getParams());
        Assert.assertEquals(((CallActionCommand)commands.get(0)).getParams().size(), 0);

    }

    @Test
    public void shouldDecodeCallActionWithParamsCommand() {
        //given:
        final String json = "[{\"c_id\":\"4711\",\"n\":\"action\",\"p\":[{\"n\":\"A\",\"v\":1},{\"n\":\"B\",\"v\":7.6},{\"n\":\"C\",\"v\":true},{\"n\":\"D\",\"v\":null},{\"n\":\"E\",\"v\":\"Hello\"}],\"id\":\"CallAction\"}]";

        //when:
        final List<Command> commands = OptimizedJsonCodec.getInstance().decode(json);

        Assert.assertNotNull(commands);
        Assert.assertEquals(commands.size(), 1);
        Assert.assertNotNull(commands.get(0));
        Assert.assertEquals(commands.get(0).getClass(), CallActionCommand.class);
        Assert.assertEquals(((CallActionCommand)commands.get(0)).getActionName(), "action");
        Assert.assertEquals(((CallActionCommand)commands.get(0)).getControllerId(), "4711");
        Assert.assertNotNull(((CallActionCommand)commands.get(0)).getParams());
        Assert.assertEquals(((CallActionCommand)commands.get(0)).getParams().size(), 5);
        Assert.assertTrue(((CallActionCommand)commands.get(0)).getParams().containsKey("A"));
        Assert.assertTrue(((CallActionCommand)commands.get(0)).getParams().containsKey("B"));
        Assert.assertTrue(((CallActionCommand)commands.get(0)).getParams().containsKey("C"));
        Assert.assertTrue(((CallActionCommand)commands.get(0)).getParams().containsKey("D"));
        Assert.assertTrue(((CallActionCommand)commands.get(0)).getParams().containsKey("E"));
        Assert.assertTrue(Number.class.isAssignableFrom(((CallActionCommand)commands.get(0)).getParams().get("A").getClass()));
        Assert.assertEquals(((Number)((CallActionCommand)commands.get(0)).getParams().get("A")).intValue(), 1);
        Assert.assertTrue(Number.class.isAssignableFrom(((CallActionCommand)commands.get(0)).getParams().get("B").getClass()));
        Assert.assertEquals(((Number)((CallActionCommand)commands.get(0)).getParams().get("B")).doubleValue(), 7.6);
        Assert.assertEquals(((CallActionCommand)commands.get(0)).getParams().get("C"), true);
        Assert.assertEquals(((CallActionCommand)commands.get(0)).getParams().get("D"), null);
        Assert.assertEquals(((CallActionCommand)commands.get(0)).getParams().get("E"), "Hello");
    }



    private static CreatePresentationModelCommand createCPMCommand() {
        final CreatePresentationModelCommand command = new CreatePresentationModelCommand();
        command.setPmId("05ee43b7-a884-4d42-9fc5-00b083664eed");
        command.setClientSideOnly(false);
        command.setPmType("com.canoo.icos.casemanager.model.casedetails.CaseInfoBean");

        final Map<String, Object> sourceSystem = new HashMap<>();
        sourceSystem.put(Attribute.PROPERTY_NAME, "@@@ SOURCE_SYSTEM @@@");
        sourceSystem.put(Attribute.ID, "3204S");
        sourceSystem.put(Attribute.QUALIFIER_NAME, null);
        sourceSystem.put(Attribute.VALUE_NAME, "server");

        final Map<String, Object> caseDetailsLabel = new HashMap<>();
        caseDetailsLabel.put(Attribute.PROPERTY_NAME, "caseDetailsLabel");
        caseDetailsLabel.put(Attribute.ID, "3205S");
        caseDetailsLabel.put(Attribute.QUALIFIER_NAME, null);
        caseDetailsLabel.put(Attribute.VALUE_NAME, null);

        final Map<String, Object> caseIdLabel = new HashMap<>();
        caseIdLabel.put(Attribute.PROPERTY_NAME, "caseIdLabel");
        caseIdLabel.put(Attribute.ID, "3206S");
        caseIdLabel.put(Attribute.QUALIFIER_NAME, null);
        caseIdLabel.put(Attribute.VALUE_NAME, null);

        final Map<String, Object> statusLabel = new HashMap<>();
        statusLabel.put(Attribute.PROPERTY_NAME, "statusLabel");
        statusLabel.put(Attribute.ID, "3207S");
        statusLabel.put(Attribute.QUALIFIER_NAME, null);
        statusLabel.put(Attribute.VALUE_NAME, null);

        final Map<String, Object> status = new HashMap<>();
        status.put(Attribute.PROPERTY_NAME, "status");
        status.put(Attribute.ID, "3208S");
        status.put(Attribute.QUALIFIER_NAME, null);
        status.put(Attribute.VALUE_NAME, null);

        command.setAttributes(Arrays.asList(sourceSystem, caseDetailsLabel, caseIdLabel, statusLabel, status));

        return command;
    }

    private static String createCPMCommandString() {
        return
            "{" +
                "\"p_id\":\"05ee43b7-a884-4d42-9fc5-00b083664eed\"," +
                "\"t\":\"com.canoo.icos.casemanager.model.casedetails.CaseInfoBean\"," +
                "\"a\":[" +
                    "{" +
                        "\"n\":\"@@@ SOURCE_SYSTEM @@@\"," +
                        "\"a_id\":\"3204S\"," +
                        "\"v\":\"server\"" +
                    "},{" +
                        "\"n\":\"caseDetailsLabel\"," +
                        "\"a_id\":\"3205S\"," +
                    "\"v\":null" +
                    "},{" +
                        "\"n\":\"caseIdLabel\"," +
                        "\"a_id\":\"3206S\"," +
                    "\"v\":null" +
                    "},{" +
                        "\"n\":\"statusLabel\"," +
                        "\"a_id\":\"3207S\"," +
                    "\"v\":null" +
                    "},{" +
                        "\"n\":\"status\"," +
                        "\"a_id\":\"3208S\"," +
                    "\"v\":null" +
                    "}" +
                "]," +
                "\"id\":\"CreatePresentationModel\"" +
            "}";
    }

    private static Command createCommand() {
        return new EmptyCommand();
    }

    private static String createCommandJsonString() {
        return "{\"id\":\"Empty\"}";
    }
}
