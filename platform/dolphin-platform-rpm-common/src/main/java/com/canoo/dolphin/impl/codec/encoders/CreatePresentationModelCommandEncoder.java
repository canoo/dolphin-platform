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
package com.canoo.dolphin.impl.codec.encoders;

import com.canoo.impl.platform.core.Assert;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.comm.CreatePresentationModelCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.opendolphin.core.comm.CommandConstants.*;

public class CreatePresentationModelCommandEncoder extends AbstractCommandTranscoder<CreatePresentationModelCommand> {

    @Override
    public JsonObject encode(CreatePresentationModelCommand command) {
        Assert.requireNonNull(command, "command");

        final JsonObject jsonCommand = new JsonObject();
        jsonCommand.addProperty(PM_ID, command.getPmId());
        jsonCommand.addProperty(PM_TYPE, command.getPmType());

        final JsonArray jsonArray = new JsonArray();
        for (final Map<String, Object> attribute : command.getAttributes()) {
            final JsonObject jsonAttribute = new JsonObject();
            jsonAttribute.addProperty(NAME, String.valueOf(attribute.get(Attribute.PROPERTY_NAME)));
            jsonAttribute.addProperty(ATTRIBUTE_ID, String.valueOf(attribute.get(Attribute.ID)));
            jsonAttribute.add(VALUE, ValueEncoder.encodeValue(attribute.get(Attribute.VALUE_NAME)));
            jsonArray.add(jsonAttribute);
        }
        jsonCommand.add(PM_ATTRIBUTES, jsonArray);
        jsonCommand.addProperty(ID, CREATE_PRESENTATION_MODEL_COMMAND_ID);

        return jsonCommand;
    }

    @Override
    public CreatePresentationModelCommand decode(JsonObject jsonObject) {
        Assert.requireNonNull(jsonObject, "jsonObject");

        try {
            final CreatePresentationModelCommand command = new CreatePresentationModelCommand();

            command.setPmId(getStringElement(jsonObject, PM_ID));
            command.setPmType(getStringElement(jsonObject, PM_TYPE));
            command.setClientSideOnly(false);

            final JsonArray jsonArray = jsonObject.getAsJsonArray(PM_ATTRIBUTES);
            final List<Map<String, Object>> attributes = new ArrayList<>();
            for (final JsonElement jsonElement : jsonArray) {
                final JsonObject attribute = jsonElement.getAsJsonObject();
                final HashMap<String, Object> map = new HashMap<>();
                map.put(Attribute.PROPERTY_NAME, getStringElement(attribute, NAME));
                map.put(Attribute.ID, getStringElement(attribute, ATTRIBUTE_ID));
                final Object value = attribute.has(VALUE) ? ValueEncoder.decodeValue(attribute.get(VALUE)) : null;
                map.put(Attribute.VALUE_NAME, value);
                attributes.add(map);
            }
            command.setAttributes(attributes);

            return command;
        } catch (IllegalStateException | ClassCastException | NullPointerException ex) {
            throw new JsonParseException("Illegal JSON detected", ex);
        }
    }
}
