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
import com.canoo.dolphin.converter.ValueConverterException;

import java.time.Period;

/**
 * Created by hendrikebbers on 25.10.16.
 */
public class PeriodConverterFactory extends AbstractConverterFactory {

    private final static Converter CONVERTER = new PeriodConverter();

    @Override
    public boolean supportsType(Class<?> cls) {
        return Period.class.isAssignableFrom(cls);
    }

    @Override
    public int getTypeIdentifier() {
        return ValueFieldTypes.PERIODE_FIELD_TYPE;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }

    private static class PeriodConverter extends AbstractStringConverter<Period> {

        @Override
        public Period convertFromDolphin(String value) throws ValueConverterException {
            if(value == null) {
                return null;
            }
            try {
                return Period.parse(value);
            } catch (Exception e) {
                throw new ValueConverterException("Can not convert to Periode", e);
            }
        }

        @Override
        public String convertToDolphin(Period value) throws ValueConverterException{
            if(value == null) {
                return null;
            }
            try {
                return value.toString();
            } catch (Exception e) {
                throw new ValueConverterException("Can not convert from Periode", e);
            }
        }
    }

}
