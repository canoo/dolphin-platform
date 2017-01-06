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
package com.canoo.implementation.dolphin.converters;

import com.canoo.dolphin.converter.Converter;

public class IntegerConverterFactory extends AbstractConverterFactory {

    public final static int FIELD_TYPE_INT = 3;

    private final static Converter CONVERTER = new AbstractNumberConverter<Integer>() {

        @Override
        public Integer convertFromDolphin(Number value) {
            return value == null ? null : value.intValue();
        }

        @Override
        public Number convertToDolphin(Integer value) {
            return value;
        }
    };


    @Override
    public boolean supportsType(Class<?> cls) {
        return int.class.equals(cls) || Integer.class.equals(cls);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_TYPE_INT;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }
}
