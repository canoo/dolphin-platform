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
package com.canoo.dolphin.integration.property;

import java.lang.annotation.ElementType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static com.canoo.dolphin.integration.util.ValueHelper.createInZone;
import static com.canoo.dolphin.integration.util.ValueHelper.getUtcZone;

public interface PropertyTestConstants {

    String PROPERTY_CONTROLLER_NAME = "PropertyController";

    String PROPERTY_CHANGE_CONTROLLER_NAME = "PropertyChangeController";


    String CHECK_MODEL_CREATION_ACTION = "checkModelCreation";

    String SET_TO_DEFAULTS_ACTION = "setToDefaults";

    String REMOVE_CHANGE_LISTENER = "removeChangeListener";

    String RESET_CHANGE_FLAGS = "resetChangeFlags";

    String ADD_CHANGE_LISTENER = "addChangeListener";

    String RESET_TO_NULL_ACTION = "resetToNull";

    String PING = "ping";


    String STRING_VALUE = "Hello World!";

    Boolean BOOLEAN_VALUE = true;

    Integer INTEGER_VALUE = 4711;

    Long LONG_VALUE = new Long(Integer.MAX_VALUE * 2);

    Float FLOAT_VALUE = new Float(1.01);

    Double DOUBLE_VALUE = new Double(1.00001);

    Number NUMBER_VALUE = Double.MAX_VALUE;

    BigDecimal BIG_DECIMAL_VALUE = new BigDecimal(12.23);

    BigInteger BIG_INTEGER_VALUE= BigInteger.valueOf(12475);

    Byte BYTE_VALUE = new Byte((byte) 12);

    Calendar CALENDAR_VALUE = createInZone(2017, 2, 3,4, 8, getUtcZone());

    Date DATE_VALUE = createInZone(2017, 2, 3,4, 5, getUtcZone()).getTime();

    Short SHORT_VALUE = new Short((short) 3);

    UUID UUID_VALUE = new UUID(1235, 43567);

    ElementType ENUM_VALUE = ElementType.ANNOTATION_TYPE;

}
