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
package com.canoo.dolphin.converters;

import com.canoo.dolphin.converter.Converter;
import com.canoo.dolphin.converter.ValueConverterException;
import com.canoo.dolphin.impl.converters.AbstractConverterFactory;
import com.canoo.dolphin.impl.converters.AbstractStringConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static com.canoo.dp.impl.platform.core.PlatformConstants.REMOTING_DATE_FORMAT_PATTERN;
import static com.canoo.dp.impl.platform.core.PlatformConstants.TIMEZONE_UTC;
import static com.canoo.dolphin.converters.ValueFieldTypes.ZONED_DATE_TIME_FIELD_TYPE;

public class ZonedDateTimeConverterFactory extends AbstractConverterFactory {

    private final static Converter CONVERTER = new ZonedDateTimeConverter();

    @Override
    public boolean supportsType(Class<?> cls) {
        return ZonedDateTime.class.isAssignableFrom(cls);
    }

    @Override
    public int getTypeIdentifier() {
        return ZONED_DATE_TIME_FIELD_TYPE;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }

    private static class ZonedDateTimeConverter extends AbstractStringConverter<ZonedDateTime> {

        private final DateFormat dateFormat;

        public ZonedDateTimeConverter(){
            dateFormat = new SimpleDateFormat(REMOTING_DATE_FORMAT_PATTERN);
            dateFormat.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC));
        }
        @Override
        public ZonedDateTime convertFromDolphin(String value) throws ValueConverterException {
            if (value == null) {
                return null;
            }
            try {
                final Calendar result = Calendar.getInstance();
                result.setTime(dateFormat.parse(value));
                return result.toInstant().atZone(ZoneId.systemDefault());
            } catch (Exception e) {
                throw new ValueConverterException("Can not convert to ZonedDateTime", e);
            }
        }

        @Override
        public String convertToDolphin(ZonedDateTime value) throws ValueConverterException {
            if (value == null) {
                return null;
            }
            try {
                Calendar calendar = GregorianCalendar.from(value);
                return dateFormat.format(calendar.getTime());
            } catch (Exception e) {
                throw new ValueConverterException("Can not convert from ZonedDateTime", e);
            }
        }
    }

}
