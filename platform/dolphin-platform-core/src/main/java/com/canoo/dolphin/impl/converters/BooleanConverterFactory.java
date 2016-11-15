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
package com.canoo.dolphin.impl.converters;

import com.canoo.dolphin.converter.Converter;

public class BooleanConverterFactory extends AbstractConverterFactory {

    public final static int FIELD_TYPE_BOOLEAN = 7;

    private final static Converter<Boolean, Boolean> CONVERTER = new AbstractBooleanConverter<Boolean>() {

        @Override
        public Boolean convertFromDolphin(Boolean value) {
            return value;
        }

        @Override
        public Boolean convertToDolphin(Boolean value) {
            return value;
        }
    };

    @Override
    public boolean supportsType(Class<?> cls) {
        return boolean.class.equals(cls) || Boolean.class.equals(cls);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_TYPE_BOOLEAN;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }
}
