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
package com.canoo.implementation.dolphin.codec;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

class ValueEncoder {

    private ValueEncoder() {}

    static JsonElement encodeValue(Object value) {
        if (value instanceof String) {
            return new JsonPrimitive((String) value);
        }
        if (value instanceof Number) {
            return new JsonPrimitive((Number) value);
        }
        if (value instanceof Boolean) {
            return new JsonPrimitive((Boolean) value);
        }
        throw new IllegalStateException("Only String, Number, and Boolean are allowed currently");
    }

    static Object decodeValue(JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return null;
        }
        if (! jsonElement.isJsonPrimitive()) {
            throw new JsonParseException("Illegal JSON detected");
        }
        final JsonPrimitive value = (JsonPrimitive) jsonElement;

        if (value.isString()) {
            return value.getAsString();
        } else if (value.isBoolean()) {
            return value.getAsBoolean();
        } else if (value.isNumber()) {
            return value.getAsNumber();
        }
        throw new JsonParseException("Currently only String, Boolean, or Number are allowed as primitives");
    }
}
