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
package com.canoo.impl.dp.logging.bridges;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.platform.core.PlatformConstants;
import com.canoo.dp.impl.platform.core.ansi.AnsiOut;
import com.canoo.platform.logging.DolphinLoggerConfiguration;
import com.canoo.platform.logging.spi.DolphinLoggerBridge;
import com.canoo.platform.logging.spi.LogMessage;
import org.slf4j.event.Level;

import java.text.DateFormat;
import java.util.Date;
import java.util.Optional;

public class SimpleDolphinLogger implements DolphinLoggerBridge {

    private final DateFormat dateFormat;

    private final DolphinLoggerConfiguration configuration;

    private final boolean ansiSupport;

    public SimpleDolphinLogger(final DolphinLoggerConfiguration configuration) {
        this.configuration = Assert.requireNonNull(configuration, "configuration");
        this.dateFormat = Assert.requireNonNull(configuration.getDateFormat(), "dateFormat");
        ansiSupport = configuration.getBooleanProperty(PlatformConstants.ANSI_PROPERTY, PlatformConstants.ANSI_DEFAULT_VALUE);
    }

    @Override
    public void log(final LogMessage logMessage) {
        final String textColor = Optional.ofNullable(logMessage.getLevel()).
                map(l -> {
                    if (l.equals(Level.ERROR)) {
                        return AnsiOut.ANSI_RED;
                    }
                    if (l.equals(Level.WARN)) {
                        return AnsiOut.ANSI_YELLOW;
                    }
                    if (l.equals(Level.INFO)) {
                        return AnsiOut.ANSI_BLUE;
                    }
                    return AnsiOut.ANSI_CYAN;
                }).orElse(AnsiOut.ANSI_CYAN);

        final StringBuilder buf = new StringBuilder();
        buf.append(AnsiOut.ANSI_WHITE);
        final Date timestamp = Date.from(logMessage.getTimestamp().toInstant());
        buf.append(dateFormat.format(timestamp));
        buf.append(AnsiOut.ANSI_RESET);

        buf.append(" ");

        buf.append(AnsiOut.ANSI_BOLD);
        buf.append(textColor);
        buf.append(logMessage.getLevel());

        buf.append(" - ");

        buf.append(logMessage.getMessage());
        buf.append(AnsiOut.ANSI_RESET);

        buf.append(AnsiOut.ANSI_WHITE);
        buf.append(" - ");

        buf.append(logMessage.getLoggerName());


        if (!logMessage.getMarker().isEmpty()) {
            buf.append(" - [");
            for (String marker : logMessage.getMarker()) {
                buf.append(marker);
                if (logMessage.getMarker().indexOf(marker) < logMessage.getMarker().size() - 1) {
                    buf.append(", ");
                }
            }
            buf.append("]");
        }
        buf.append(" - ");
        buf.append(logMessage.getThreadName());
        buf.append(AnsiOut.ANSI_RESET);

        if (logMessage.getThrowable() != null) {
            buf.append(AnsiOut.ANSI_RED);
            buf.append(System.lineSeparator());
            buf.append(logMessage.getExceptionDetail());
            buf.append(AnsiOut.ANSI_RESET);
        }
        print(buf.toString());
    }


    private synchronized void print(final String message) {
        AnsiOut.print(message, ansiSupport);
    }
}
