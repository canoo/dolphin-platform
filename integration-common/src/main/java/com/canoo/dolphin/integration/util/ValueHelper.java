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
package com.canoo.dolphin.integration.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class ValueHelper {

    public static TimeZone getUtcZone() {
        return TimeZone.getTimeZone("UTC");
    }

    public static Calendar createInZone(int year, int month, int dayOfMonth, int hourOfDay, int minute,
                                        TimeZone timeZone) {
        return createInZone(year, month, dayOfMonth, hourOfDay, minute, 0, timeZone);
    }

    public static Calendar createInZone(int year, int month, int dayOfMonth, int hourOfDay,
                                        int minute, int second, TimeZone timeZone) {
        Calendar ret = GregorianCalendar.getInstance(timeZone);
        ret.set(year, month, dayOfMonth,hourOfDay, minute, second);
        ret.set(Calendar.MILLISECOND, 0);
        return ret;
    }

}
