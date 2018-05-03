/*
 * Copyright 2015-2018 Canoo Engineering AG.
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
package com.canoo.impl.dp.logging;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.core.DolphinRuntimeException;
import org.slf4j.event.Level;

public class DolphinLoggerUtils {

    private DolphinLoggerUtils() {}

    public static boolean isLevelEnabled(final Level baseLevel, final Level level) {
        Assert.requireNonNull(baseLevel, "baseLevel");
        Assert.requireNonNull(level, "level");
        return baseLevel.toInt() <= level.toInt();
    }

    public static boolean isLevelEnabled(final Level baseLevel, final String level) {
        Assert.requireNonNull(level, "level");
        return isLevelEnabled(baseLevel, Level.valueOf(level));
    }

    public static Level getLevel(final String name) {
        Assert.requireNonNull(name, "name");
        if(name.trim().toLowerCase().equals("error")) {
            return Level.ERROR;
        }
        if(name.trim().toLowerCase().equals("warn")) {
            return Level.WARN;
        }
        if(name.trim().toLowerCase().equals("info")) {
            return Level.INFO;
        }
        if(name.trim().toLowerCase().equals("debug")) {
            return Level.DEBUG;
        }
        if(name.trim().toLowerCase().equals("trace")) {
            return Level.TRACE;
        }
        throw new DolphinRuntimeException("'" + name + "' is not a logging level");
    }
}
