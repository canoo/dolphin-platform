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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static com.canoo.dp.impl.platform.core.PlatformConstants.REMOTING_DATE_FORMAT_PATTERN;
import static com.canoo.dp.impl.platform.core.PlatformConstants.TIMEZONE_UTC;
import static com.canoo.dolphin.converters.ValueFieldTypes.LOCAL_DATE_FIELD_TYPE;

public class LocalDateConverterFactory extends AbstractConverterFactory {

    private final static Converter<?, ?> CONVERTER = new LocalDateConverter();

    @Override
    public boolean supportsType(Class<?> cls) {
        return LocalDate.class.isAssignableFrom(cls);
    }

    @Override
    public int getTypeIdentifier() {
        return LOCAL_DATE_FIELD_TYPE;
    }

    @Override
    public Converter<?, ?> getConverterForType(Class<?> cls) {
        return CONVERTER;
    }

    private static class LocalDateConverter
            extends AbstractStringConverter<LocalDate> {
        private final DateFormat dateFormat;

        public LocalDateConverter(){
            dateFormat = new SimpleDateFormat(REMOTING_DATE_FORMAT_PATTERN);
            dateFormat.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC));
        }
        @Override
        public LocalDate convertFromDolphin(String value)
                throws ValueConverterException {
            if (value == null) {
                return null;
            }
            try {
                final Calendar result = Calendar.getInstance(TimeZone.getDefault());
                result.setTime(dateFormat.parse(value));
                return result.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } catch (Exception e) {
                throw new ValueConverterException(
                        "Can not convert to LocalDate", e);
            }
        }

        @Override
        public String convertToDolphin(LocalDate value)
                throws ValueConverterException {
            if (value == null) {
                return null;
            }
            try {
                Date date = Date.from(value.atStartOfDay().toInstant(ZoneOffset.ofHours(0)));
                return dateFormat.format(date);
            } catch (Exception e) {
                throw new ValueConverterException(
                        "Can not convert from LocalDate", e);
            }
        }
    }

}
