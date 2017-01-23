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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.canoo.communication.common.commands.CreatePresentationModelCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.canoo.dolphin.impl.codec.CommandConstants.ATTRIBUTE_ID;
import static com.canoo.dolphin.impl.codec.CommandConstants.ATTRIBUTE_NAME;
import static com.canoo.dolphin.impl.codec.CommandConstants.ATTRIBUTE_VALUE;
import static com.canoo.dolphin.impl.codec.CommandConstants.ID;
import static com.canoo.dolphin.impl.codec.CommandConstants.PM_ATTRIBUTES;
import static com.canoo.dolphin.impl.codec.CommandConstants.PM_ID;
import static com.canoo.dolphin.impl.codec.CommandConstants.PM_TYPE;
import static com.canoo.dolphin.impl.codec.ValueEncoder.decodeValue;
import static com.canoo.dolphin.impl.codec.ValueEncoder.encodeValue;

public class  CreatePresentationModelEncoder implements CommandEncoder<CreatePresentationModelCommand> {

    @Override
    public JsonObject encode(CreatePresentationModelCommand command) {
        Assert.requireNonNull(command, "command");

        final JsonObject jsonCommand = new JsonObject();
        jsonCommand.addProperty(PM_ID, command.getPmId());
        jsonCommand.addProperty(PM_TYPE, command.getPmType());

        final JsonArray jsonArray = new JsonArray();
        for (final Map<String, Object> attribute : command.getAttributes()) {
            final JsonObject jsonAttribute = new JsonObject();
            jsonAttribute.addProperty(ATTRIBUTE_NAME, String.valueOf(attribute.get("propertyName")));
            jsonAttribute.addProperty(ATTRIBUTE_ID, String.valueOf(attribute.get("id")));
            final Object value = attribute.get("value");
            if (value != null) {
                jsonAttribute.add(ATTRIBUTE_VALUE, encodeValue(attribute.get("value")));
            }
            jsonArray.add(jsonAttribute);
        }
        jsonCommand.add(PM_ATTRIBUTES, jsonArray);
        jsonCommand.addProperty(ID, "CreatePresentationModel");

        return jsonCommand;
    }

    @Override
    public CreatePresentationModelCommand decode(JsonObject jsonObject) {
        Assert.requireNonNull(jsonObject, "jsonObject");

        try {
            final CreatePresentationModelCommand command = new CreatePresentationModelCommand();

            command.setPmId(jsonObject.getAsJsonPrimitive(PM_ID).getAsString());
            command.setPmType(jsonObject.getAsJsonPrimitive(PM_TYPE).getAsString());
            command.setClientSideOnly(false);

            final JsonArray jsonArray = jsonObject.getAsJsonArray(PM_ATTRIBUTES);
            final List<Map<String, Object>> attributes = new ArrayList<>();
            for (final JsonElement jsonElement : jsonArray) {
                final JsonObject attribute = jsonElement.getAsJsonObject();
                final HashMap<String, Object> map = new HashMap<>();
                map.put("propertyName", attribute.getAsJsonPrimitive(ATTRIBUTE_NAME).getAsString());
                map.put("id", attribute.getAsJsonPrimitive(ATTRIBUTE_ID).getAsString());
                final Object value = attribute.has(ATTRIBUTE_VALUE)? decodeValue(attribute.get(ATTRIBUTE_VALUE)) : null;
                map.put("value", value);
                map.put("baseValue", value);
                map.put("qualifier", null);
                attributes.add(map);
            }
            command.setAttributes(attributes);

            return command;
        } catch (IllegalStateException | ClassCastException | NullPointerException ex) {
            throw new JsonParseException("Illegal JSON detected", ex);
        }
    }
}
