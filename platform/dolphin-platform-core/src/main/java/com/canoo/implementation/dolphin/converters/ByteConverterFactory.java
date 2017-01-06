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

public class ByteConverterFactory extends AbstractConverterFactory {

    public final static int FIELD_TYPE_BYTE = 1;

    private final static Converter<Byte, Number> CONVERTER = new AbstractNumberConverter<Byte>() {

        @Override
        public Byte convertFromDolphin(Number value) {
            return value == null ? null : value.byteValue();
        }

        @Override
        public Number convertToDolphin(Byte value) {
            return value;
        }
    };


    @Override
    public boolean supportsType(Class<?> cls) {
        return byte.class.equals(cls) || Byte.class.equals(cls);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_TYPE_BYTE;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }
}
