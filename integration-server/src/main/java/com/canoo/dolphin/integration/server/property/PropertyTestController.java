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

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.integration.property.PropertyTestBean;
import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;
import com.canoo.dolphin.server.DolphinModel;

import java.util.Objects;

import static com.canoo.dolphin.integration.IntegrationConstants.NOT_NULL;
import static com.canoo.dolphin.integration.property.PropertyTestConstants.*;

@DolphinController(PROPERTY_CONTROLLER_NAME)
public class PropertyTestController {

    @DolphinModel
    private PropertyTestBean model;

    private Subscription bigDecimalChangedSubscription;

    private Subscription bigIntegerChangedSubscription;

    private Subscription booleanChangedSubscription;

    private Subscription byteChangedSubscription;

    private Subscription calenderChangedSubscription;

    private Subscription dateChangedSubscription;

    private Subscription doubleChangedSubscription;

    private Subscription enumChangedSubscription;

    private Subscription floatChangedSubscription;

    private Subscription integerChangedSubscription;

    private Subscription longChangedSubscription;

    private Subscription shortChangedSubscription;

    private Subscription stringChangedSubscription;

    private Subscription uuidChangedSubscription;

    @DolphinAction(CHECK_MODEL_CREATION_ACTION)
    public void checkModelCreation() {
        Objects.requireNonNull(model, "Model should not be null");
        Objects.requireNonNull(model.bigDecimalValueProperty(), "big decimal property" + NOT_NULL);
        Objects.requireNonNull(model.bigIntegerValueProperty(), "big integer property" + NOT_NULL);
        Objects.requireNonNull(model.booleanValueProperty(), "boolean property" + NOT_NULL);
        Objects.requireNonNull(model.byteValueProperty(), "byte property" + NOT_NULL);
        Objects.requireNonNull(model.calendarValueProperty(), "calender property" + NOT_NULL);
        Objects.requireNonNull(model.dateValueProperty(), "date property" + NOT_NULL);
        Objects.requireNonNull(model.doubleValueProperty(), "double property" + NOT_NULL);
        Objects.requireNonNull(model.enumValueProperty(), "enum property" + NOT_NULL);
        Objects.requireNonNull(model.floatValueProperty(), "float property" + NOT_NULL);
        Objects.requireNonNull(model.integerValueProperty(), "integer property" + NOT_NULL);
        Objects.requireNonNull(model.longValueProperty(), "long property" + NOT_NULL);
        Objects.requireNonNull(model.shortValueProperty(), "short property" + NOT_NULL);
        Objects.requireNonNull(model.stringValueProperty(), "string property" + NOT_NULL);
        Objects.requireNonNull(model.uuidValueProperty(), "uuid property" + NOT_NULL);
    }

    @DolphinAction(SET_TO_DEFAULTS_ACTION)
    public void setToDefaults() {
        model.setBigDecimalValue(BIG_DECIMAL_VALUE);
        model.setBigIntegerValue(BIG_INTEGER_VALUE);
        model.setBooleanValue(BOOLEAN_VALUE);
        model.setByteValue(BYTE_VALUE);
        model.setCalendarValue(CALENDAR_VALUE);
        model.setDateValue(DATE_VALUE);
        model.setDoubleValue(DOUBLE_VALUE);
        model.setEnumValue(ENUM_VALUE);
        model.setFloatValue(FLOAT_VALUE);
        model.setIntegerValue(INTEGER_VALUE);
        model.setLongValue(LONG_VALUE);
        model.setShortValue(SHORT_VALUE);
        model.setStringValue(STRING_VALUE);
        model.setUuidValue(UUID_VALUE);
    }

    @DolphinAction(ADD_CHANGE_LISTENER)
    public void addChangeListener() {
        resetChangedFlags();
        bigDecimalChangedSubscription = model.bigDecimalValueProperty().onChanged(e -> model.setBigDecimalValueChanged(true));
        bigIntegerChangedSubscription = model.bigIntegerValueProperty().onChanged(e -> model.setBigIntegerValueChanged(true));
        booleanChangedSubscription = model.booleanValueProperty().onChanged(e -> model.setBooleanValueChanged(true));
        byteChangedSubscription = model.byteValueProperty().onChanged(e -> model.setByteValueChanged(true));
        calenderChangedSubscription = model.calendarValueProperty().onChanged(e -> model.setCalenderValueChanged(true));
        dateChangedSubscription = model.dateValueProperty().onChanged(e -> model.setDateValueChanged(true));
        doubleChangedSubscription = model.doubleValueProperty().onChanged(e -> model.setDoubleValueChanged(true));
        enumChangedSubscription = model.enumValueProperty().onChanged(e -> model.setEnumValueChanged(true));
        floatChangedSubscription = model.floatValueProperty().onChanged(e -> model.setFloatValueChanged(true));
        integerChangedSubscription = model.integerValueProperty().onChanged(e -> model.setIntegerValueChanged(true));
        longChangedSubscription = model.longValueProperty().onChanged(e -> model.setLongValueChanged(true));
        shortChangedSubscription = model.shortValueProperty().onChanged(e -> model.setShortValueChanged(true));
        stringChangedSubscription = model.stringValueProperty().onChanged(e -> model.setStringValueChanged(true));
        uuidChangedSubscription = model.uuidValueProperty().onChanged(e -> model.setUuidValueChanged(true));
    }

    @DolphinAction(REMOVE_CHANGE_LISTENER)
    public void removeChangeListener() {
        bigDecimalChangedSubscription.unsubscribe();
        bigDecimalChangedSubscription = null;

        bigIntegerChangedSubscription.unsubscribe();
        bigIntegerChangedSubscription = null;

        booleanChangedSubscription.unsubscribe();
        booleanChangedSubscription = null;

        byteChangedSubscription.unsubscribe();
        byteChangedSubscription = null;

        calenderChangedSubscription.unsubscribe();
        calenderChangedSubscription = null;

        dateChangedSubscription.unsubscribe();
        dateChangedSubscription = null;

        doubleChangedSubscription.unsubscribe();
        doubleChangedSubscription = null;

        enumChangedSubscription.unsubscribe();
        enumChangedSubscription = null;

        floatChangedSubscription.unsubscribe();
        floatChangedSubscription = null;

        integerChangedSubscription.unsubscribe();
        integerChangedSubscription = null;

        longChangedSubscription.unsubscribe();
        longChangedSubscription = null;

        shortChangedSubscription.unsubscribe();
        shortChangedSubscription = null;

        stringChangedSubscription.unsubscribe();
        stringChangedSubscription = null;

        uuidChangedSubscription.unsubscribe();
        uuidChangedSubscription = null;
        resetChangedFlags();
    }

    @DolphinAction(RESET_CHANGE_FLAGS)
    public void resetChangedFlags() {
        model.setBigDecimalValueChanged(false);
        model.setBigIntegerValueChanged(false);
        model.setBooleanValueChanged(false);
        model.setByteValueChanged(false);
        model.setCalenderValueChanged(false);
        model.setDateValueChanged(false);
        model.setDoubleValueChanged(false);
        model.setEnumValueChanged(false);
        model.setFloatValueChanged(false);
        model.setIntegerValueChanged(false);
        model.setLongValueChanged(false);
        model.setShortValueChanged(false);
        model.setStringValueChanged(false);
        model.setUuidValueChanged(false);
    }

    @DolphinAction(RESET_TO_NULL_ACTION)
    public void resetToNull() {
        model.setBigDecimalValue(null);
        model.setBigIntegerValue(null);
        model.setBooleanValue(null);
        model.setByteValue(null);
        model.setCalendarValue(null);
        model.setDateValue(null);
        model.setDoubleValue(null);
        model.setEnumValue(null);
        model.setFloatValue(null);
        model.setIntegerValue(null);
        model.setLongValue(null);
        model.setShortValue(null);
        model.setStringValue(null);
        model.setUuidValue(null);
    }

    @DolphinAction(PING)
    public void ping() {

    }
}
