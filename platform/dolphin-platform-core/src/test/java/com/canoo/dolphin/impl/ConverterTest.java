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
package com.canoo.dolphin.impl;

import com.canoo.dolphin.converter.Converter;
import com.canoo.dolphin.converter.ValueConverterException;
import com.canoo.implementation.dolphin.Converters;
import org.testng.annotations.Test;

import java.util.Date;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class ConverterTest {

    @Test
    public void testDateConversions() {
        Converters converters = new Converters(null);
        Converter converter = converters.getConverter(Date.class);

        Date testDate1 = new Date();
        checkConversion(converter, testDate1);

        Date testDate2 = new Date(0);
        checkConversion(converter, testDate2);

        Date testDate3 = new Date(Long.MAX_VALUE);
        checkConversion(converter, testDate3);

        Date testDate4 = null;
        checkConversion(converter, testDate4);

        //TODO: Not working based on date formate (yyyy), max year is 9999
        //Date testDate5 = new Date(Long.MIN_VALUE);
    }

    @Test
    public void testStringConversions() {
        Converters converters = new Converters(null);
        Converter converter = converters.getConverter(String.class);

        checkConversion(converter, "");
        checkConversion(converter, null);
        checkConversion(converter, "Hello");
    }

    @Test
    public void testDoubleConversions() {
        Converters converters = new Converters(null);
        Converter converter = converters.getConverter(Double.class);

        checkConversion(converter, 2.9d);
        checkConversion(converter, null);
        checkConversion(converter, 0.0);
        checkConversion(converter, Double.MAX_VALUE);
        checkConversion(converter, Double.MIN_VALUE);
    }

    private void checkConversion(Converter converter, Object val) {
        try {
            Object converted = converter.convertToDolphin(val);
            Object reconverted = converter.convertFromDolphin(converted);
            assertEquals(reconverted, val);
        } catch (ValueConverterException e) {
            fail("Error in conversion", e);
        }
    }

}
