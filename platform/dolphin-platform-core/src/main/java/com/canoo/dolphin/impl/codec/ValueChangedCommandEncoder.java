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

import com.canoo.common.Assert;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.canoo.communication.common.commands.ValueChangedCommand;

import static com.canoo.dolphin.impl.codec.ValueEncoder.decodeValue;
import static com.canoo.dolphin.impl.codec.ValueEncoder.encodeValue;

public class ValueChangedCommandEncoder implements CommandEncoder<ValueChangedCommand> {

    @Override
    public JsonObject encode(ValueChangedCommand command) {
        Assert.requireNonNull(command, "command");

        final JsonObject jsonCommand = new JsonObject();
        jsonCommand.addProperty("a", command.getAttributeId());
        if (command.getOldValue() != null) {
            jsonCommand.add("o", encodeValue(command.getOldValue()));
        }
        if (command.getNewValue() != null) {
            jsonCommand.add("n", encodeValue(command.getNewValue()));
        }
        jsonCommand.addProperty("id", "ValueChanged");
        return jsonCommand;
    }

    @Override
    public ValueChangedCommand decode(JsonObject jsonObject) {
        Assert.requireNonNull(jsonObject, "jsonObject");

        try {
            final ValueChangedCommand command = new ValueChangedCommand();

            command.setNewValue(decodeValue(jsonObject.get("n")));
            command.setOldValue(decodeValue(jsonObject.get("o")));
            command.setAttributeId(jsonObject.get("a").getAsString());

            return command;
        } catch (IllegalStateException | ClassCastException | NullPointerException ex) {
            throw new JsonParseException("Illegal JSON detected", ex);
        }    }

}
