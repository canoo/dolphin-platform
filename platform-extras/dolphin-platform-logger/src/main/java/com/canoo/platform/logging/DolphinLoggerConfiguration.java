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
package com.canoo.platform.logging;

import com.canoo.impl.dp.logging.DolphinLoggerUtils;
import com.canoo.platform.core.SimpleConfiguration;
import org.slf4j.event.Level;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;

import static com.canoo.impl.dp.logging.LoggerConstants.DATE_FORMAT_DEFAULT;
import static com.canoo.impl.dp.logging.LoggerConstants.DATE_FORMAT_PROPERTY;
import static com.canoo.impl.dp.logging.LoggerConstants.GLOBAL_LEVEL_DEFAULT;
import static com.canoo.impl.dp.logging.LoggerConstants.GLOBAL_LEVEL_PROPERTY;

public class DolphinLoggerConfiguration extends SimpleConfiguration{

    public DolphinLoggerConfiguration(final Properties internalProperties) {
        super(internalProperties);
    }

    public Level getLevelFor(final String loggerName) {
        return getPropertyKeys().stream()
                .filter(k -> loggerName.startsWith(k))
                .sorted((a,b) -> b.compareTo(a))
                .findFirst()
                .map(k -> getProperty(k))
                .map(l -> DolphinLoggerUtils.getLevel(l))
                .orElse(getGlobalLevel());
    }

    public Level getGlobalLevel() {
        final String level = getProperty(GLOBAL_LEVEL_PROPERTY, GLOBAL_LEVEL_DEFAULT);
        return DolphinLoggerUtils.getLevel(level);
    }

    public DateFormat getDateFormat() {
        final String pattern = getProperty(DATE_FORMAT_PROPERTY, DATE_FORMAT_DEFAULT);
        return new SimpleDateFormat(pattern);
    }
}
