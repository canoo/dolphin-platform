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
package com.canoo.dp.impl.remoting.codec.encoders;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.remoting.legacy.commands.ValueChangedCommand;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import static com.canoo.dp.impl.remoting.legacy.commands.CommandConstants.ATTRIBUTE_ID;
import static com.canoo.dp.impl.remoting.legacy.commands.CommandConstants.ID;
import static com.canoo.dp.impl.remoting.legacy.commands.CommandConstants.VALUE;
import static com.canoo.dp.impl.remoting.legacy.commands.CommandConstants.VALUE_CHANGED_COMMAND_ID;

public class ValueChangedCommandEncoder extends AbstractCommandTranscoder<ValueChangedCommand> {

    @Override
    public JsonObject encode(ValueChangedCommand command) {
        Assert.requireNonNull(command, "command");
        final JsonObject jsonCommand = new JsonObject();
        jsonCommand.addProperty(ATTRIBUTE_ID, command.getAttributeId());
        if (command.getNewValue() != null) {
            jsonCommand.add(VALUE, ValueEncoder.encodeValue(command.getNewValue()));
        }
        jsonCommand.addProperty(ID, VALUE_CHANGED_COMMAND_ID);
        return jsonCommand;
    }

    @Override
    public ValueChangedCommand decode(JsonObject jsonObject) {
        Assert.requireNonNull(jsonObject, "jsonObject");
        try {
            final ValueChangedCommand command = new ValueChangedCommand();
            command.setNewValue(ValueEncoder.decodeValue(jsonObject.get(VALUE)));
            command.setAttributeId(getStringElement(jsonObject, ATTRIBUTE_ID));
            return command;
        } catch (IllegalStateException | ClassCastException | NullPointerException ex) {
            throw new JsonParseException("Illegal JSON detected", ex);
        }
    }
}
