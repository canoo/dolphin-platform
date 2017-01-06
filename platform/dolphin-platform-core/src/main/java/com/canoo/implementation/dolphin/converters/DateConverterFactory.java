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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateConverterFactory extends AbstractConverterFactory {

    public final static int FIELD_TYPE_DATE = 9;

    private final static Converter CONVERTER = new DateConverter();

    @Override
    public boolean supportsType(Class<?> cls) {
        return Date.class.isAssignableFrom(cls);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_TYPE_DATE;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }

    private static class DateConverter extends AbstractStringConverter<Date> {

        private static final Logger LOG = LoggerFactory.getLogger(DateConverter.class);

        private final DateFormat dateFormat;

        public DateConverter() {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        @Override
        public Date convertFromDolphin(String value) throws ValueConverterException{
            if (value == null) {
                return null;
            }
            try {
                return dateFormat.parse(value);
            } catch (ParseException e) {
                throw new ValueConverterException("Unable to parse the date: " + value, e);
            }
        }

        @Override
        public String convertToDolphin(Date value) throws ValueConverterException{
            if (value == null) {
                return null;
            }
            try {
                return dateFormat.format(value);
            } catch (IllegalArgumentException e) {
                throw new ValueConverterException("Unable to format the date: " + value, e);
            }
        }
    }
}
