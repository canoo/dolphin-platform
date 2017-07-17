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
package com.canoo.dolphin.integration;

import com.canoo.platform.client.ClientContext;
import com.canoo.platform.client.ControllerProxy;
import com.canoo.dolphin.integration.property.PropertyTestBean;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import static com.canoo.dolphin.integration.property.PropertyTestConstants.*;

public class PropertyControllerTest extends AbstractIntegrationTest {

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test if controller and model can be created")
    public void testCreateController(String containerType, String endpoint) {
        try {
            ClientContext context = connect(endpoint);
            ControllerProxy<PropertyTestBean> controller = createController(context, PROPERTY_CONTROLLER_NAME);

            Assert.assertNotNull(controller);
            Assert.assertNotNull(controller.getModel());
            Assert.assertEquals(controller.getModel().getClass(), PropertyTestBean.class);

            destroy(controller, endpoint);
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test if all property instances are created by default")
    public void testPropertyCreating(String containerType, String endpoint) {
        try {
            ClientContext context = connect(endpoint);
            ControllerProxy<PropertyTestBean> controller = createController(context, PROPERTY_CONTROLLER_NAME);

            Assert.assertNotNull(controller.getModel().uuidValueProperty());
            Assert.assertNotNull(controller.getModel().stringValueProperty());
            Assert.assertNotNull(controller.getModel().shortValueProperty());
            Assert.assertNotNull(controller.getModel().longValueProperty());
            Assert.assertNotNull(controller.getModel().integerValueProperty());
            Assert.assertNotNull(controller.getModel().bigDecimalValueProperty());
            Assert.assertNotNull(controller.getModel().bigIntegerValueProperty());
            Assert.assertNotNull(controller.getModel().booleanValueProperty());
            Assert.assertNotNull(controller.getModel().byteValueProperty());
            Assert.assertNotNull(controller.getModel().calendarValueProperty());
            Assert.assertNotNull(controller.getModel().dateValueProperty());
            Assert.assertNotNull(controller.getModel().doubleValueProperty());
            Assert.assertNotNull(controller.getModel().enumValueProperty());
            Assert.assertNotNull(controller.getModel().floatValueProperty());

            destroy(controller, endpoint);
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test if all property have an null value by default")
    public void testPropertyNullValueCreating(String containerType, String endpoint) {
        try {
            ClientContext context = connect(endpoint);
            ControllerProxy<PropertyTestBean> controller = createController(context, PROPERTY_CONTROLLER_NAME);

            Assert.assertNull(controller.getModel().getUuidValue());
            Assert.assertNull(controller.getModel().getStringValue());
            Assert.assertNull(controller.getModel().getShortValue());
            Assert.assertNull(controller.getModel().getLongValue());
            Assert.assertNull(controller.getModel().getIntegerValue());
            Assert.assertNull(controller.getModel().getBigDecimalValue());
            Assert.assertNull(controller.getModel().getBigIntegerValue());
            Assert.assertNull(controller.getModel().getBooleanValue());
            Assert.assertNull(controller.getModel().getByteValue());
            Assert.assertNull(controller.getModel().getCalendarValue());
            Assert.assertNull(controller.getModel().getDateValue());
            Assert.assertNull(controller.getModel().getDoubleValue());
            Assert.assertNull(controller.getModel().getEnumValue());
            Assert.assertNull(controller.getModel().getFloatValue());

            destroy(controller, endpoint);
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test if all property values are snychronized")
    public void testPropertyValueSet(String containerType, String endpoint) {
        try {
            ClientContext context = connect(endpoint);
            ControllerProxy<PropertyTestBean> controller = createController(context, PROPERTY_CONTROLLER_NAME);
            invoke(controller, SET_TO_DEFAULTS_ACTION, containerType);

            Assert.assertEquals(controller.getModel().getBigDecimalValue(), BIG_DECIMAL_VALUE);
            Assert.assertEquals(controller.getModel().getBigIntegerValue(), BIG_INTEGER_VALUE);
            Assert.assertEquals(controller.getModel().getBooleanValue(), BOOLEAN_VALUE);
            Assert.assertEquals(controller.getModel().getByteValue(), BYTE_VALUE);
            Assert.assertEquals(controller.getModel().getCalendarValue().getTimeInMillis(), CALENDAR_VALUE.getTimeInMillis());
            Assert.assertEquals(controller.getModel().getDateValue(), DATE_VALUE);
            Assert.assertEquals(controller.getModel().getDoubleValue(), DOUBLE_VALUE);
            Assert.assertEquals(controller.getModel().getEnumValue(), ENUM_VALUE);
            Assert.assertEquals(controller.getModel().getFloatValue(), FLOAT_VALUE);
            Assert.assertEquals(controller.getModel().getIntegerValue(), INTEGER_VALUE);
            Assert.assertEquals(controller.getModel().getLongValue(), LONG_VALUE);
            Assert.assertEquals(controller.getModel().getShortValue(), SHORT_VALUE);
            Assert.assertEquals(controller.getModel().getStringValue(), STRING_VALUE);
            Assert.assertEquals(controller.getModel().getUuidValue(), UUID_VALUE);

            destroy(controller, endpoint);
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test if all no property changed are fired at same side")
    public void testNoPropertyChangeOnSameSide(String containerType, String endpoint) {
        try {
            ClientContext context = connect(endpoint);
            ControllerProxy<PropertyTestBean> controller = createController(context, PROPERTY_CONTROLLER_NAME);
            invoke(controller, SET_TO_DEFAULTS_ACTION, containerType);
            invoke(controller, ADD_CHANGE_LISTENER, containerType);
            invoke(controller, SET_TO_DEFAULTS_ACTION, containerType);

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

            destroy(controller, endpoint);
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test if all property changed are fired")
    public void testPropertyChange(String containerType, String endpoint) {
        try {
            ClientContext context = connect(endpoint);
            ControllerProxy<PropertyTestBean> controller = createController(context, PROPERTY_CONTROLLER_NAME);
            invoke(controller, SET_TO_DEFAULTS_ACTION, containerType);
            invoke(controller, ADD_CHANGE_LISTENER, containerType);

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

            invoke(controller, PING, containerType);

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

            destroy(controller, endpoint);
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test if all unsubscribe for changes is working")
    public void testPropertyChangeUnsubscribe(String containerType, String endpoint) {
        try {
            ClientContext context = connect(endpoint);
            ControllerProxy<PropertyTestBean> controller = createController(context, PROPERTY_CONTROLLER_NAME);
            invoke(controller, ADD_CHANGE_LISTENER, containerType);
            invoke(controller, SET_TO_DEFAULTS_ACTION, containerType);
            invoke(controller, REMOVE_CHANGE_LISTENER, containerType);

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

            invoke(controller, PING, containerType);

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

            destroy(controller, endpoint);
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test if all unsubscribe for changes is working")
    public void testNoChangeForSameValue(final String containerType, final String endpoint) {
        try {
            ClientContext context = connect(endpoint);
            ControllerProxy<PropertyTestBean> controller = createController(context, PROPERTY_CONTROLLER_NAME);
            invoke(controller, SET_TO_DEFAULTS_ACTION, containerType);
            invoke(controller, ADD_CHANGE_LISTENER, containerType);

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

            invoke(controller, PING, containerType);

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

            destroy(controller, endpoint);
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER)
    public void testMaxValues(String containerType, String endpoint) {
        //given
        final ClientContext context = connect(endpoint);
        final ControllerProxy<PropertyTestBean> controller = createController(context, PROPERTY_CONTROLLER_NAME);


        //when
        invoke(controller, SET_TO_MAX_VALUES_ACTION, containerType);

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

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER)
    public void testMinValues(final String containerType, final String endpoint) {
        //given
        final ClientContext context = connect(endpoint);
        final ControllerProxy<PropertyTestBean> controller = createController(context, PROPERTY_CONTROLLER_NAME);


        //when
        invoke(controller, SET_TO_MIN_VALUES_ACTION, containerType);

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

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER)
    public void testInfinityValues(final String containerType, final String endpoint) {
        //given
        final ClientContext context = connect(endpoint);
        final ControllerProxy<PropertyTestBean> controller = createController(context, PROPERTY_CONTROLLER_NAME);


        //when
        invoke(controller, SET_TO_INFINITY_VALUES_ACTION, containerType);

        //then
        Assert.assertEquals(controller.getModel().getDoubleValue().doubleValue(), Double.POSITIVE_INFINITY);
        Assert.assertEquals(controller.getModel().getFloatValue().floatValue(), Float.POSITIVE_INFINITY);
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER)
    public void testNegativeInfinityValues(final String containerType, final String endpoint) {
        //given
        final ClientContext context = connect(endpoint);
        final ControllerProxy<PropertyTestBean> controller = createController(context, PROPERTY_CONTROLLER_NAME);


        //when
        invoke(controller, SET_TO_NEGATIVE_INFINITY_VALUES_ACTION, containerType);

        //then
        Assert.assertEquals(controller.getModel().getDoubleValue().doubleValue(), Double.NEGATIVE_INFINITY);
        Assert.assertEquals(controller.getModel().getFloatValue().floatValue(), Float.NEGATIVE_INFINITY);
    }

}
