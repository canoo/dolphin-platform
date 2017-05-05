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
import com.canoo.dolphin.impl.Converters;
import com.canoo.dolphin.internal.BeanRepository;
import mockit.Mocked;
import org.testng.annotations.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

public class ZonedDateTimeConverterFactoryTest {

    @Test
    public void testFactoryFieldType(@Mocked BeanRepository beanRepository) {
        //Given
        Converters converters = new Converters(beanRepository);

        //When
        int type = converters.getFieldType(ZonedDateTime.class);

        //Then
        assertEquals(type, ValueFieldTypes.ZONED_DATE_TIME_FIELD_TYPE);
    }

    @Test
    public void testConverterCreation(@Mocked BeanRepository beanRepository) {
        //Given
        Converters converters = new Converters(beanRepository);

        //When
        Converter converter = converters.getConverter(ZonedDateTime.class);

        //Then
        assertNotNull(converter);
    }

    @Test
    public void testBasicConversions(@Mocked BeanRepository beanRepository) {
        //Given
        Converters converters = new Converters(beanRepository);

        //When
        Converter converter = converters.getConverter(ZonedDateTime.class);

        //Then
        testReconversion(converter, ZonedDateTime.now());
        testReconversion(converter, ZonedDateTime.now(ZoneId.of(ZoneId.getAvailableZoneIds().iterator().next())));
        testReconversion(converter, ZonedDateTime.now(ZoneId.of("GMT")));
        testReconversion(converter, ZonedDateTime.now(ZoneId.of("Z")));
        testReconversion(converter, ZonedDateTime.now(ZoneId.of("UTC+6")));
    }

    @Test
    public void testNullValues(@Mocked BeanRepository beanRepository) {
        //Given
        Converters converters = new Converters(beanRepository);

        //When
        Converter converter = converters.getConverter(ZonedDateTime.class);

        //Then
        try {
            assertEquals(converter.convertFromDolphin(null), null);
            assertEquals(converter.convertToDolphin(null), null);
        } catch (ValueConverterException e) {
            fail("Error in conversion", e);
        }
    }

    @Test(expectedExceptions = ClassCastException.class)
    public void testWrongDolphinValues(@Mocked BeanRepository beanRepository) throws ValueConverterException {
        //Given
        Converters converters = new Converters(beanRepository);

        //When
        Converter converter = converters.getConverter(ZonedDateTime.class);

        //Then
        converter.convertFromDolphin(7);
    }

    @Test(expectedExceptions = ClassCastException.class)
    public void testWrongBeanValues(@Mocked BeanRepository beanRepository) throws ValueConverterException {
        //Given
        Converters converters = new Converters(beanRepository);

        //When
        Converter converter = converters.getConverter(ZonedDateTime.class);

        //Then
        converter.convertToDolphin(7);
    }

    private void testReconversion(Converter converter, ZonedDateTime time) {
        try {
            Object dolphinObject = converter.convertToDolphin(time);
            assertNotNull(dolphinObject);
            TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("UTC-3")));
            Object reconvertedObject = converter.convertFromDolphin(dolphinObject);
            assertNotNull(reconvertedObject);
            assertEquals(reconvertedObject.getClass(), ZonedDateTime.class);
            ZonedDateTime reverted = (ZonedDateTime) reconvertedObject;
            assertEquals(reverted.withZoneSameInstant(ZoneId.of("UTC")), time.withZoneSameInstant(ZoneId.of("UTC")));
        } catch (ValueConverterException e) {
            fail("Error in converter", e);
        }
    }
}
