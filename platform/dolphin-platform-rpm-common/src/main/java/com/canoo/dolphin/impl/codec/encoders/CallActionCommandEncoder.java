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

import com.canoo.dolphin.impl.commands.CallActionCommand;
import com.canoo.dp.impl.platform.core.Assert;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.Map;

import static com.canoo.dolphin.impl.codec.CommandConstants.*;

public class CallActionCommandEncoder extends AbstractCommandEncoder<CallActionCommand> {

    @Override
    public JsonObject encode(final CallActionCommand command) {
        Assert.requireNonNull(command, "command");
        final JsonObject jsonCommand = new JsonObject();
        jsonCommand.addProperty(CONTROLLER_ID, command.getControllerId());
        jsonCommand.addProperty(ACTION_NAME, command.getActionName());

        final JsonArray paramArray = new JsonArray();
        for(Map.Entry<String, Object> paramEntry : command.getParams().entrySet()) {
            final JsonObject paramObject = new JsonObject();
            paramObject.addProperty(PARAM_NAME, paramEntry.getKey());
            paramObject.add(PARAM_VALUE, ValueEncoder.encodeValue(paramEntry.getValue()));
            paramArray.add(paramObject);
        }
        jsonCommand.add(PARAMS, paramArray);

        jsonCommand.addProperty(ID, CALL_ACTION_COMMAND_ID);
        return jsonCommand;
    }

    @Override
    public CallActionCommand decode(JsonObject jsonObject) {
        Assert.requireNonNull(jsonObject, "jsonObject");
        try {
            final CallActionCommand command = new CallActionCommand();
            command.setControllerId(getStringElement(jsonObject, CONTROLLER_ID));
            command.setActionName(getStringElement(jsonObject, ACTION_NAME));

            final JsonArray jsonArray = jsonObject.getAsJsonArray(PARAMS);
            if(jsonArray != null) {
                for (final JsonElement jsonElement : jsonArray) {
                    final JsonObject paramObject = jsonElement.getAsJsonObject();
                    command.addParam(getStringElement(paramObject, PARAM_NAME), ValueEncoder.decodeValue(paramObject.get(PARAM_VALUE)));
                }
            }
            return command;
        } catch (Exception ex) {
            throw new JsonParseException("Illegal JSON detected", ex);
        }
    }
}
