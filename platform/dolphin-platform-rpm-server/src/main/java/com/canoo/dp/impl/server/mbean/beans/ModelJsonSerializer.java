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
package com.canoo.dp.impl.server.mbean.beans;

import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.impl.DolphinUtils;
import com.canoo.dp.impl.platform.core.ReflectionHelper;
import com.canoo.dolphin.mapping.Property;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Field;

/**
 * Helper class that exports the current state of a dolphin bean (see {@link com.canoo.dolphin.mapping.DolphinBean}) to JSON
 */
public class ModelJsonSerializer {

    public static JsonObject toJson(Object dolphinModel) {
        if(dolphinModel == null) {
            return null;
        }

        JsonObject jsonObject = new JsonObject();

        for(Field field : ReflectionHelper.getInheritedDeclaredFields(dolphinModel.getClass())) {
            if(DolphinUtils.isProperty(field.getType())) {
                Property property = (Property) ReflectionHelper.getPrivileged(field, dolphinModel);
                Object value = property.get();
                if(value == null) {
                    jsonObject.add(field.getName(), null);
                } else if(Number.class.isAssignableFrom(value.getClass()) || Double.TYPE.isAssignableFrom(value.getClass()) || Float.TYPE.isAssignableFrom(value.getClass()) || Long.TYPE.isAssignableFrom(value.getClass()) || Integer.TYPE.isAssignableFrom(value.getClass())) {
                    jsonObject.add(field.getName(), new JsonPrimitive((Number) value));
                } else if(String.class.isAssignableFrom(value.getClass())) {
                    jsonObject.add(field.getName(), new JsonPrimitive((String) value));
                } else if(Boolean.class.isAssignableFrom(value.getClass()) || Boolean.TYPE.isAssignableFrom(value.getClass())) {
                    jsonObject.add(field.getName(), new JsonPrimitive((Boolean) value));
                } else {
                    jsonObject.add(field.getName(), toJson(value));
                }
            } else if(DolphinUtils.isObservableList(field.getType())) {
                ObservableList list = (ObservableList) ReflectionHelper.getPrivileged(field, dolphinModel);
                JsonArray jsonArray = new JsonArray();
                for(Object value : list) {
                    if(value == null) {
                        //TODO
                        //jsonArray.add(null);
                    } else if(Number.class.isAssignableFrom(value.getClass()) || Double.TYPE.isAssignableFrom(value.getClass()) || Float.TYPE.isAssignableFrom(value.getClass()) || Long.TYPE.isAssignableFrom(value.getClass()) || Integer.TYPE.isAssignableFrom(value.getClass())) {
                        jsonArray.add(new JsonPrimitive((Number) value));
                    } else if(String.class.isAssignableFrom(value.getClass())) {
                        jsonArray.add(new JsonPrimitive((String) value));
                    } else if(Boolean.class.isAssignableFrom(value.getClass()) || Boolean.TYPE.isAssignableFrom(value.getClass())) {
                        jsonArray.add(new JsonPrimitive((Boolean) value));
                    } else {
                        jsonArray.add(toJson(value));
                    }
                }
                jsonObject.add(field.getName(), jsonArray);
            }

        }
        return jsonObject;
    }
}
