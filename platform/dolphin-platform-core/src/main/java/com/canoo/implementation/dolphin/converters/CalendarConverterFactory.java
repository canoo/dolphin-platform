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
import java.util.Calendar;
import java.util.TimeZone;

public class CalendarConverterFactory extends AbstractConverterFactory {

    private final static Converter CONVERTER = new CalendarConverter();

    public final static int FIELD_TYPE_CALENDAR = 11;

    @Override
    public boolean supportsType(Class<?> cls) {
        return Calendar.class.isAssignableFrom(cls);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_TYPE_CALENDAR;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }

    private static class CalendarConverter extends AbstractStringConverter<Calendar> {

        private static final Logger LOG = LoggerFactory.getLogger(CalendarConverter.class);

        private final DateFormat dateFormat;

        public CalendarConverter() {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        @Override
        public Calendar convertFromDolphin(String value) throws ValueConverterException {
            if (value == null) {
                return null;
            }
            try {
                final Calendar result = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                result.setTime(dateFormat.parse(value));
                return result;
            } catch (ParseException e) {
                throw new ValueConverterException("Unable to parse the date: " + value, e);
            }
        }

        @Override
        public String convertToDolphin(Calendar value) throws ValueConverterException {
            if (value == null) {
                return null;
            }
            try {
                return dateFormat.format(((Calendar)value).getTime());
            } catch (IllegalArgumentException e) {
                throw new ValueConverterException("Unable to format the date: " + value, e);
            }
        }
    }
}
