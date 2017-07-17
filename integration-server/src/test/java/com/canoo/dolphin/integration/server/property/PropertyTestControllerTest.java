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
package com.canoo.dolphin.integration.server.property;


import com.canoo.dolphin.integration.property.PropertyTestBean;
import com.canoo.dolphin.integration.server.TestConfiguration;
import com.canoo.platform.spring.test.ControllerUnderTest;
import com.canoo.platform.spring.test.SpringTestNGControllerTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import static com.canoo.dolphin.integration.property.PropertyTestConstants.*;

@SpringBootTest(classes = TestConfiguration.class)
public class PropertyTestControllerTest extends SpringTestNGControllerTest {

    private ControllerUnderTest<PropertyTestBean> controller;

    @BeforeMethod
    public void init() {
        controller = createController(PROPERTY_CONTROLLER_NAME);
    }

    @AfterMethod
    public void destroy() {
        controller.destroy();
    }

    @Test
    public void testControllerCreation() {
        Assert.assertNotNull(controller);
    }

    @Test
    public void testModelCreation() {
        Assert.assertNotNull(controller.getModel());
        Assert.assertNotNull(controller.getModel().bigDecimalValueProperty());
        Assert.assertNotNull(controller.getModel().bigIntegerValueProperty());
        Assert.assertNotNull(controller.getModel().booleanValueProperty());
        Assert.assertNotNull(controller.getModel().byteValueProperty());
        Assert.assertNotNull(controller.getModel().calendarValueProperty());
        Assert.assertNotNull(controller.getModel().dateValueProperty());
        Assert.assertNotNull(controller.getModel().doubleValueProperty());
        Assert.assertNotNull(controller.getModel().enumValueProperty());
        Assert.assertNotNull(controller.getModel().floatValueProperty());
        Assert.assertNotNull(controller.getModel().integerValueProperty());
        Assert.assertNotNull(controller.getModel().longValueProperty());
        Assert.assertNotNull(controller.getModel().shortValueProperty());
        Assert.assertNotNull(controller.getModel().stringValueProperty());
        Assert.assertNotNull(controller.getModel().uuidValueProperty());
    }

    @Test
    public void testModelIsEmptyAfterCreation() {
        Assert.assertNull(controller.getModel().getBigDecimalValue());
        Assert.assertNull(controller.getModel().getBigIntegerValue());
        Assert.assertNull(controller.getModel().getBooleanValue());
        Assert.assertNull(controller.getModel().getByteValue());
        Assert.assertNull(controller.getModel().getCalendarValue());
        Assert.assertNull(controller.getModel().getDateValue());
        Assert.assertNull(controller.getModel().getDoubleValue());
        Assert.assertNull(controller.getModel().getEnumValue());
        Assert.assertNull(controller.getModel().getFloatValue());
        Assert.assertNull(controller.getModel().getIntegerValue());
        Assert.assertNull(controller.getModel().getLongValue());
        Assert.assertNull(controller.getModel().getShortValue());
        Assert.assertNull(controller.getModel().getStringValue());
        Assert.assertNull(controller.getModel().getUuidValue());

        controller.invoke(CHECK_MODEL_CREATION_ACTION);
    }

    @Test
    public void testModelNewValues() {
        controller.invoke(SET_TO_DEFAULTS_ACTION);

        Assert.assertEquals(controller.getModel().getBigDecimalValue(), BIG_DECIMAL_VALUE);
        Assert.assertEquals(controller.getModel().getBigIntegerValue(), BIG_INTEGER_VALUE);
        Assert.assertEquals(controller.getModel().getBooleanValue(), BOOLEAN_VALUE);
        Assert.assertEquals(controller.getModel().getByteValue(), BYTE_VALUE);
        Assert.assertEquals(controller.getModel().getCalendarValue().getTime(), CALENDAR_VALUE.getTime());
        Assert.assertEquals(controller.getModel().getDateValue(), DATE_VALUE);
        Assert.assertEquals(controller.getModel().getDoubleValue(), DOUBLE_VALUE);
        Assert.assertEquals(controller.getModel().getEnumValue(), ENUM_VALUE);
        Assert.assertEquals(controller.getModel().getFloatValue(), FLOAT_VALUE);
        Assert.assertEquals(controller.getModel().getIntegerValue(), INTEGER_VALUE);
        Assert.assertEquals(controller.getModel().getLongValue(), LONG_VALUE);
        Assert.assertEquals(controller.getModel().getShortValue(), SHORT_VALUE);
        Assert.assertEquals(controller.getModel().getStringValue(), STRING_VALUE);
        Assert.assertEquals(controller.getModel().getUuidValue(), UUID_VALUE);
    }

    @Test
    public void testValueChangeInModel() {
        controller.invoke(SET_TO_DEFAULTS_ACTION);

        Assert.assertEquals(controller.getModel().getBigDecimalValue(), BIG_DECIMAL_VALUE);
        Assert.assertEquals(controller.getModel().getBigIntegerValue(), BIG_INTEGER_VALUE);
        Assert.assertEquals(controller.getModel().getBooleanValue(), BOOLEAN_VALUE);
        Assert.assertEquals(controller.getModel().getByteValue(), BYTE_VALUE);
        Assert.assertEquals(controller.getModel().getCalendarValue().getTime(), CALENDAR_VALUE.getTime());
        Assert.assertEquals(controller.getModel().getDateValue(), DATE_VALUE);
        Assert.assertEquals(controller.getModel().getDoubleValue(), DOUBLE_VALUE);
        Assert.assertEquals(controller.getModel().getEnumValue(), ENUM_VALUE);
        Assert.assertEquals(controller.getModel().getFloatValue(), FLOAT_VALUE);
        Assert.assertEquals(controller.getModel().getIntegerValue(), INTEGER_VALUE);
        Assert.assertEquals(controller.getModel().getLongValue(), LONG_VALUE);
        Assert.assertEquals(controller.getModel().getShortValue(), SHORT_VALUE);
        Assert.assertEquals(controller.getModel().getStringValue(), STRING_VALUE);
        Assert.assertEquals(controller.getModel().getUuidValue(), UUID_VALUE);

        controller.invoke(RESET_TO_NULL_ACTION);

        Assert.assertNull(controller.getModel().getBigDecimalValue());
        Assert.assertNull(controller.getModel().getBigIntegerValue());
        Assert.assertNull(controller.getModel().getBooleanValue());
        Assert.assertNull(controller.getModel().getByteValue());
        Assert.assertNull(controller.getModel().getCalendarValue());
        Assert.assertNull(controller.getModel().getDateValue());
        Assert.assertNull(controller.getModel().getDoubleValue());
        Assert.assertNull(controller.getModel().getEnumValue());
        Assert.assertNull(controller.getModel().getFloatValue());
        Assert.assertNull(controller.getModel().getIntegerValue());
        Assert.assertNull(controller.getModel().getLongValue());
        Assert.assertNull(controller.getModel().getShortValue());
        Assert.assertNull(controller.getModel().getStringValue());
        Assert.assertNull(controller.getModel().getUuidValue());

        controller.invoke(SET_TO_DEFAULTS_ACTION);

        Assert.assertEquals(controller.getModel().getBigDecimalValue(), BIG_DECIMAL_VALUE);
        Assert.assertEquals(controller.getModel().getBigIntegerValue(), BIG_INTEGER_VALUE);
        Assert.assertEquals(controller.getModel().getBooleanValue(), BOOLEAN_VALUE);
        Assert.assertEquals(controller.getModel().getByteValue(), BYTE_VALUE);
        Assert.assertEquals(controller.getModel().getCalendarValue().getTime(), CALENDAR_VALUE.getTime());
        Assert.assertEquals(controller.getModel().getDateValue(), DATE_VALUE);
        Assert.assertEquals(controller.getModel().getDoubleValue(), DOUBLE_VALUE);
        Assert.assertEquals(controller.getModel().getEnumValue(), ENUM_VALUE);
        Assert.assertEquals(controller.getModel().getFloatValue(), FLOAT_VALUE);
        Assert.assertEquals(controller.getModel().getIntegerValue(), INTEGER_VALUE);
        Assert.assertEquals(controller.getModel().getLongValue(), LONG_VALUE);
        Assert.assertEquals(controller.getModel().getShortValue(), SHORT_VALUE);
        Assert.assertEquals(controller.getModel().getStringValue(), STRING_VALUE);
        Assert.assertEquals(controller.getModel().getUuidValue(), UUID_VALUE);
    }

    @Test
    public void testNoPropertyChangeOnSameSide() {
        controller.invoke(SET_TO_DEFAULTS_ACTION);
        controller.invoke(ADD_CHANGE_LISTENER);
        controller.invoke(SET_TO_DEFAULTS_ACTION);

        Assert.assertFalse(controller.getModel().getBigDecimalValueChanged());
        Assert.assertFalse(controller.getModel().getBigIntegerValueChanged());
        Assert.assertFalse(controller.getModel().getBooleanValueChanged());
        Assert.assertFalse(controller.getModel().getByteValueChanged());
        Assert.assertFalse(controller.getModel().getCalenderValueChanged());
        Assert.assertFalse(controller.getModel().getDateValueChanged());
        Assert.assertFalse(controller.getModel().getDoubleValueChanged());
        Assert.assertFalse(controller.getModel().getEnumValueChanged());
        Assert.assertFalse(controller.getModel().getFloatValueChanged());
        Assert.assertFalse(controller.getModel().getIntegerValueChanged());
        Assert.assertFalse(controller.getModel().getLongValueChanged());
        Assert.assertFalse(controller.getModel().getShortValueChanged());
        Assert.assertFalse(controller.getModel().getStringValueChanged());
        Assert.assertFalse(controller.getModel().getUuidValueChanged());
    }

    @Test
    public void testPropertyChange() {
        controller.invoke(SET_TO_DEFAULTS_ACTION);
        controller.invoke(ADD_CHANGE_LISTENER);

        controller.getModel().setBigDecimalValue(null);
        controller.getModel().setBigIntegerValue(null);
        controller.getModel().setBooleanValue(null);
        controller.getModel().setByteValue(null);
        controller.getModel().setCalendarValue(null);
        controller.getModel().setDateValue(null);
        controller.getModel().setDoubleValue(null);
        controller.getModel().setEnumValue(null);
        controller.getModel().setFloatValue(null);
        controller.getModel().setIntegerValue(null);
        controller.getModel().setLongValue(null);
        controller.getModel().setShortValue(null);
        controller.getModel().setStringValue(null);
        controller.getModel().setUuidValue(null);

        controller.invoke(PING);

        Assert.assertTrue(controller.getModel().getBigDecimalValueChanged());
        Assert.assertTrue(controller.getModel().getBigIntegerValueChanged());
        Assert.assertTrue(controller.getModel().getBooleanValueChanged());
        Assert.assertTrue(controller.getModel().getByteValueChanged());
        Assert.assertTrue(controller.getModel().getCalenderValueChanged());
        Assert.assertTrue(controller.getModel().getDateValueChanged());
        Assert.assertTrue(controller.getModel().getDoubleValueChanged());
        Assert.assertTrue(controller.getModel().getEnumValueChanged());
        Assert.assertTrue(controller.getModel().getFloatValueChanged());
        Assert.assertTrue(controller.getModel().getIntegerValueChanged());
        Assert.assertTrue(controller.getModel().getLongValueChanged());
        Assert.assertTrue(controller.getModel().getShortValueChanged());
        Assert.assertTrue(controller.getModel().getStringValueChanged());
        Assert.assertTrue(controller.getModel().getUuidValueChanged());
    }

    @Test
    public void testPropertyChangeUnsubscribe() {
        controller.invoke(ADD_CHANGE_LISTENER);
        controller.invoke(SET_TO_DEFAULTS_ACTION);
        controller.invoke(REMOVE_CHANGE_LISTENER);

        controller.getModel().setBigDecimalValue(null);
        controller.getModel().setBigIntegerValue(null);
        controller.getModel().setBooleanValue(null);
        controller.getModel().setByteValue(null);
        controller.getModel().setCalendarValue(null);
        controller.getModel().setDateValue(null);
        controller.getModel().setDoubleValue(null);
        controller.getModel().setEnumValue(null);
        controller.getModel().setFloatValue(null);
        controller.getModel().setIntegerValue(null);
        controller.getModel().setLongValue(null);
        controller.getModel().setShortValue(null);
        controller.getModel().setStringValue(null);
        controller.getModel().setUuidValue(null);

        controller.invoke(PING);

        Assert.assertFalse(controller.getModel().getBigDecimalValueChanged());
        Assert.assertFalse(controller.getModel().getBigIntegerValueChanged());
        Assert.assertFalse(controller.getModel().getBooleanValueChanged());
        Assert.assertFalse(controller.getModel().getByteValueChanged());
        Assert.assertFalse(controller.getModel().getCalenderValueChanged());
        Assert.assertFalse(controller.getModel().getDateValueChanged());
        Assert.assertFalse(controller.getModel().getDoubleValueChanged());
        Assert.assertFalse(controller.getModel().getEnumValueChanged());
        Assert.assertFalse(controller.getModel().getFloatValueChanged());
        Assert.assertFalse(controller.getModel().getIntegerValueChanged());
        Assert.assertFalse(controller.getModel().getLongValueChanged());
        Assert.assertFalse(controller.getModel().getShortValueChanged());
        Assert.assertFalse(controller.getModel().getStringValueChanged());
        Assert.assertFalse(controller.getModel().getUuidValueChanged());
    }

    @Test
    public void testNoChangeForSameValue() {
        controller.invoke(SET_TO_DEFAULTS_ACTION);
        controller.invoke(ADD_CHANGE_LISTENER);

        controller.getModel().setBigDecimalValue(BIG_DECIMAL_VALUE);
        controller.getModel().setBigIntegerValue(BIG_INTEGER_VALUE);
        controller.getModel().setBooleanValue(BOOLEAN_VALUE);
        controller.getModel().setByteValue(BYTE_VALUE);
        controller.getModel().setCalendarValue(CALENDAR_VALUE);
        controller.getModel().setDateValue(DATE_VALUE);
        controller.getModel().setDoubleValue(DOUBLE_VALUE);
        controller.getModel().setEnumValue(ENUM_VALUE);
        controller.getModel().setFloatValue(FLOAT_VALUE);
        controller.getModel().setIntegerValue(INTEGER_VALUE);
        controller.getModel().setLongValue(LONG_VALUE);
        controller.getModel().setShortValue(SHORT_VALUE);
        controller.getModel().setStringValue(STRING_VALUE);
        controller.getModel().setUuidValue(UUID_VALUE);

        controller.invoke(PING);

        Assert.assertFalse(controller.getModel().getBigDecimalValueChanged());
        Assert.assertFalse(controller.getModel().getBigIntegerValueChanged());
        Assert.assertFalse(controller.getModel().getBooleanValueChanged());
        Assert.assertFalse(controller.getModel().getByteValueChanged());
        Assert.assertFalse(controller.getModel().getCalenderValueChanged());
        Assert.assertFalse(controller.getModel().getDateValueChanged());
        Assert.assertFalse(controller.getModel().getDoubleValueChanged());
        Assert.assertFalse(controller.getModel().getEnumValueChanged());
        Assert.assertFalse(controller.getModel().getFloatValueChanged());
        Assert.assertFalse(controller.getModel().getIntegerValueChanged());
        Assert.assertFalse(controller.getModel().getLongValueChanged());
        Assert.assertFalse(controller.getModel().getShortValueChanged());
        Assert.assertFalse(controller.getModel().getStringValueChanged());
        Assert.assertFalse(controller.getModel().getUuidValueChanged());
    }

    @Test
    public void testMaxValues() {
        //when
        controller.invoke(SET_TO_MAX_VALUES_ACTION);

        //then
        Assert.assertEquals(controller.getModel().getBigDecimalValue(), BigDecimal.valueOf(Double.MAX_VALUE));
        Assert.assertEquals(controller.getModel().getBigIntegerValue(), BigInteger.valueOf(Integer.MAX_VALUE));
        Assert.assertEquals(controller.getModel().getByteValue().byteValue(), Byte.MAX_VALUE);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.MAX_VALUE);
        Assert.assertEquals(controller.getModel().getCalendarValue().getTime(), calendar.getTime());
        Assert.assertEquals(controller.getModel().getDateValue(), new Date(Long.MAX_VALUE));
        Assert.assertEquals(controller.getModel().getDoubleValue().doubleValue(), Double.MAX_VALUE);
        Assert.assertEquals(controller.getModel().getFloatValue().floatValue(), Float.MAX_VALUE);
        Assert.assertEquals(controller.getModel().getIntegerValue().intValue(), Integer.MAX_VALUE);
        Assert.assertEquals(controller.getModel().getLongValue().longValue(), Long.MAX_VALUE);
        Assert.assertEquals(controller.getModel().getShortValue().shortValue(), Short.MAX_VALUE);
    }

    @Test
    public void testMinValues() {
        //when
        controller.invoke(SET_TO_MIN_VALUES_ACTION);

        //then
        Assert.assertEquals(controller.getModel().getBigDecimalValue(), BigDecimal.valueOf(Double.MIN_VALUE));
        Assert.assertEquals(controller.getModel().getBigIntegerValue(), BigInteger.valueOf(Integer.MIN_VALUE));
        Assert.assertEquals(controller.getModel().getByteValue().byteValue(), Byte.MIN_VALUE);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);
        Assert.assertEquals(controller.getModel().getCalendarValue().getTime(), calendar.getTime());
        Assert.assertEquals(controller.getModel().getDateValue(), new Date(0));
        Assert.assertEquals(controller.getModel().getDoubleValue().doubleValue(), Double.MIN_VALUE);
        Assert.assertEquals(controller.getModel().getFloatValue().floatValue(), Float.MIN_VALUE);
        Assert.assertEquals(controller.getModel().getIntegerValue().intValue(), Integer.MIN_VALUE);
        Assert.assertEquals(controller.getModel().getLongValue().longValue(), Long.MIN_VALUE);
        Assert.assertEquals(controller.getModel().getShortValue().shortValue(), Short.MIN_VALUE);
    }

    @Test
    public void testInfinityValues() {
        //when
        controller.invoke(SET_TO_INFINITY_VALUES_ACTION);

        //then
        Assert.assertEquals(controller.getModel().getDoubleValue().doubleValue(), Double.POSITIVE_INFINITY);
        Assert.assertEquals(controller.getModel().getFloatValue().floatValue(), Float.POSITIVE_INFINITY);
    }

    @Test
    public void testNegativeInfinityValues() {
        //when
        controller.invoke(SET_TO_NEGATIVE_INFINITY_VALUES_ACTION);

        //then
        Assert.assertEquals(controller.getModel().getDoubleValue().doubleValue(), Double.NEGATIVE_INFINITY);
        Assert.assertEquals(controller.getModel().getFloatValue().floatValue(), Float.NEGATIVE_INFINITY);
    }
}
