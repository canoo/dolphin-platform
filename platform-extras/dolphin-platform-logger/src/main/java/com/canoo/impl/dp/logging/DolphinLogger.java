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

import com.canoo.dp.impl.platform.core.context.ContextManagerImpl;
import com.canoo.platform.logging.spi.DolphinLoggerBridge;
import com.canoo.platform.logging.spi.LogMessage;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.event.Level;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DolphinLogger implements Logger {

    private final String name;

    private final List<DolphinLoggerBridge> bridges = new CopyOnWriteArrayList<>();

    private final Lock bridgesLock = new ReentrantLock();

    private final DolphinLoggerFactory loggerFactory;

    private final CopyOnWriteArrayList<String> markers = new CopyOnWriteArrayList<>();

    private Level level;

    public DolphinLogger(final DolphinLoggerFactory loggerFactory, final String name, final List<DolphinLoggerBridge> bridges, final Level level) {
        this.loggerFactory = Objects.requireNonNull(loggerFactory);
        this.name = Objects.requireNonNull(name);
        this.bridges.addAll(Objects.requireNonNull(bridges));
        this.level = Objects.requireNonNull(level);
    }

    @Override
    public boolean isTraceEnabled() {
        return DolphinLoggerUtils.isLevelEnabled(this.level, Level.TRACE);
    }

    @Override
    public boolean isTraceEnabled(final Marker marker) {
        return isTraceEnabled();
    }

    @Override
    public void trace(final String message) {
        log(Level.TRACE, null, message, null);
    }

    @Override
    public void trace(final String message, final Object arg) {
        log(Level.TRACE, null, message, null, arg);
    }

    @Override
    public void trace(final String message, final Object arg1, final Object arg2) {
        log(Level.TRACE, null, message, null, arg1, arg2);
    }

    @Override
    public void trace(final String message, final Object... arguments) {
        log(Level.TRACE, null, message, null, arguments);
    }

    @Override
    public void trace(final String message, final Throwable throwable) {
        log(Level.TRACE, null, message, throwable);
    }

    @Override
    public void trace(final Marker marker, final String message) {
        log(Level.TRACE, marker, message, null);
    }

    @Override
    public void trace(final Marker marker, final String message, final Object arg) {
        log(Level.TRACE, marker, message, null, arg);
    }

    @Override
    public void trace(final Marker marker, final String message, final Object arg1, final Object arg2) {
        log(Level.TRACE, marker, message, null, arg1, arg2);
    }

    @Override
    public void trace(final Marker marker, final String message, final Object... arguments) {
        log(Level.TRACE, marker, message, null, arguments);
    }

    @Override
    public void trace(final Marker marker, final String message, final Throwable t) {
        log(Level.TRACE, marker, message, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return DolphinLoggerUtils.isLevelEnabled(this.level, Level.DEBUG);
    }

    @Override
    public boolean isDebugEnabled(final Marker marker) {
        return isDebugEnabled();
    }

    @Override
    public void debug(final String message) {
        log(Level.DEBUG, null, message, null);
    }

    @Override
    public void debug(final String message, final Object arg) {
        log(Level.DEBUG, null, message, null, arg);
    }

    @Override
    public void debug(final String message, final Object arg1, final Object arg2) {
        log(Level.DEBUG, null, message, null, arg1, arg2);
    }

    @Override
    public void debug(final String message, final Object... arguments) {
        log(Level.DEBUG, null, message, null, arguments);
    }

    @Override
    public void debug(final String message, final Throwable throwable) {
        log(Level.DEBUG, null, message, throwable);
    }

    @Override
    public void debug(final Marker marker, final String message) {
        log(Level.DEBUG, marker, message, null);
    }

    @Override
    public void debug(final Marker marker, final String message, final Object arg) {
        log(Level.DEBUG, marker, message, null, arg);
    }

    @Override
    public void debug(final Marker marker, final String message, final Object arg1, final Object arg2) {
        log(Level.DEBUG, marker, message, null, arg1, arg2);
    }

    @Override
    public void debug(final Marker marker, final String message, final Object... arguments) {
        log(Level.DEBUG, marker, message, null, arguments);
    }

    @Override
    public void debug(Marker marker, String message, Throwable throwable) {
        log(Level.DEBUG, marker, message, throwable);
    }

    @Override
    public boolean isInfoEnabled() {
        return DolphinLoggerUtils.isLevelEnabled(this.level, Level.INFO);
    }

    @Override
    public boolean isInfoEnabled(final Marker marker) {
        return isInfoEnabled();
    }

    @Override
    public void info(final String message) {
        log(Level.INFO, null, message, null);
    }

    @Override
    public void info(final String message, final Object arg) {
        log(Level.INFO, null, message, null, arg);
    }

    @Override
    public void info(final String message, final Object arg1, final Object arg2) {
        log(Level.INFO, null, message, null, arg1, arg2);
    }

    @Override
    public void info(final String message, final Object... arguments) {
        log(Level.INFO, null, message, null, arguments);
    }

    @Override
    public void info(final String message, final Throwable throwable) {
        log(Level.INFO, null, message, throwable);
    }

    @Override
    public void info(final Marker marker, final String message) {
        log(Level.INFO, marker, message, null);
    }

    @Override
    public void info(final Marker marker, final String message, final Object arg) {
        log(Level.INFO, marker, message, null, arg);
    }

    @Override
    public void info(final Marker marker, final String message, final Object arg1, final Object arg2) {
        log(Level.INFO, marker, message, null, arg1, arg2);
    }

    @Override
    public void info(final Marker marker, final String message, final Object... arguments) {
        log(Level.INFO, marker, message, null, arguments);
    }

    @Override
    public void info(final Marker marker, final String message, final Throwable throwable) {
        log(Level.INFO, marker, message, throwable);
    }

    @Override
    public boolean isWarnEnabled() {
        return DolphinLoggerUtils.isLevelEnabled(this.level, Level.WARN);
    }

    @Override
    public boolean isWarnEnabled(final Marker marker) {
        return isWarnEnabled();
    }

    @Override
    public void warn(final String message) {
        log(Level.WARN, null, message, null);
    }

    @Override
    public void warn(final String message, final Object arg) {
        log(Level.WARN, null, message, null, arg);
    }

    @Override
    public void warn(final String message, final Object arg1, final Object arg2) {
        log(Level.WARN, null, message, null, arg1, arg2);
    }

    @Override
    public void warn(final String message, final Object... arguments) {
        log(Level.WARN, null, message, null, arguments);
    }

    @Override
    public void warn(final String message, final Throwable throwable) {
        log(Level.WARN, null, message, throwable);
    }

    @Override
    public void warn(final Marker marker, final String message) {
        log(Level.WARN, marker, message, null);
    }

    @Override
    public void warn(final Marker marker, final String message, final Object arg) {
        log(Level.WARN, marker, message, null, arg);
    }

    @Override
    public void warn(final Marker marker, final String message, final Object arg1, final Object arg2) {
        log(Level.WARN, marker, message, null, arg1, arg2);
    }

    @Override
    public void warn(final Marker marker, final String message, final Object... arguments) {
        log(Level.WARN, marker, message, null, arguments);
    }

    @Override
    public void warn(final Marker marker, final String message, final Throwable throwable) {
        log(Level.WARN, marker, message, throwable);
    }

    @Override
    public boolean isErrorEnabled() {
        return DolphinLoggerUtils.isLevelEnabled(this.level, Level.ERROR);
    }

    @Override
    public boolean isErrorEnabled(final Marker marker) {
        return isErrorEnabled();
    }

    @Override
    public void error(final String message) {
        log(Level.ERROR, null, message, null);
    }

    @Override
    public void error(final String message, final Object arg) {
        log(Level.ERROR, null, message, null, arg);
    }

    @Override
    public void error(final String message, final Object arg1, final Object arg2) {
        log(Level.ERROR, null, message, null, arg1, arg2);
    }

    @Override
    public void error(final String message, final Object... arguments) {
        log(Level.ERROR, null, message, null, arguments);
    }

    @Override
    public void error(final String message, final Throwable throwable) {
        log(Level.ERROR, null, message, throwable);
    }

    @Override
    public void error(final Marker marker, final String message) {
        log(Level.ERROR, marker, message, null);
    }

    @Override
    public void error(final Marker marker, final String message, final Object arg) {
        log(Level.ERROR, marker, message, null, arg);
    }

    @Override
    public void error(final Marker marker, final String message, final Object arg1, final Object arg2) {
        log(Level.ERROR, marker, message, null, arg1, arg2);
    }

    @Override
    public void error(final Marker marker, final String message, final Object... arguments) {
        log(Level.ERROR, marker, message, null, arguments);
    }

    @Override
    public void error(final Marker marker, final String message, final Throwable throwable) {
        log(Level.ERROR, marker, message, throwable);
    }

    @Override
    public String getName() {
        return name;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(final Level level) {
        this.level = Objects.requireNonNull(level);
    }

    private synchronized List<String> addMarker(final String marker) {
        markers.add(Objects.requireNonNull(marker));
        return Collections.unmodifiableList(this.markers);
    }

    private synchronized List<String> removeMarker(final String marker) {
        markers.remove(Objects.requireNonNull(marker));
        return Collections.unmodifiableList(this.markers);
    }

    private synchronized List<String> addMarkers(final Collection<String> markers) {
        this.markers.addAll(Objects.requireNonNull(markers));
        return Collections.unmodifiableList(this.markers);
    }

    private synchronized List<String> removeMarkers(final Collection<String> markers) {
        this.markers.removeAll(Objects.requireNonNull(markers));
        return Collections.unmodifiableList(this.markers);
    }

    private List<String> convert(final Marker marker) {
        Objects.requireNonNull(marker);

        List<String> ret = new ArrayList<>();
        ret.add(marker.getName());
        Iterator<Marker> children = marker.iterator();
        while (children.hasNext()) {
            ret.addAll(convert(children.next()));
        }
        return ret;
    }

    public void updateBridges(List<DolphinLoggerBridge> bridges) {
        //TODO: Sync this with log method
        synchronized (bridges) {
            this.bridges.clear();
            this.bridges.addAll(bridges);
        }
    }

    private void log(final Level level, final Marker marker, final String msg, final Throwable throwable, final Object... arguments) {
        Objects.requireNonNull(level);

        if (DolphinLoggerUtils.isLevelEnabled(this.level, level)) {
            final List<String> tempMarkers = new ArrayList<>();
            if(marker != null) {
                tempMarkers.addAll(convert(marker));
            }
            final List<String> currentMarkers = new ArrayList<>();
            currentMarkers.addAll(addMarkers(tempMarkers));
            try {
                final LogMessage logMessage = new LogMessage();
                logMessage.setLoggerName(name);
                logMessage.setLevel(level);
                logMessage.setMessage(msg);
                logMessage.setTimestamp(ZonedDateTime.now());
                logMessage.setThreadName(Thread.currentThread().getName());


                final Map<String, String> context = new HashMap<>();
                ContextManagerImpl.getInstance().getGlobalContexts().forEach(c -> context.put(c.getType(), c.getValue()));
                ContextManagerImpl.getInstance().getThreadContexts().forEach(c -> context.put(c.getType(), c.getValue()));
                logMessage.setContext(Collections.unmodifiableMap(context));

                logMessage.setMarker(currentMarkers);

                if (throwable != null) {
                    logMessage.setThrowable(throwable);
                }

                if (arguments != null && arguments.length > 0) {
                    final FormattingTuple tp = MessageFormatter.arrayFormat(msg, arguments);
                    logMessage.setMessage(tp.getMessage());
                    if(logMessage.getThrowable() == null && tp.getThrowable() != null) {
                        logMessage.setThrowable(tp.getThrowable());
                    }
                }

                loggerFactory.addToCache(logMessage);

                for(DolphinLoggerBridge bridge : bridges) {
                    try {
                        bridge.log(logMessage);
                    } catch (Exception e) {
                        System.err.println("Error in Logger: " + e.getMessage());
                    }
                }

            } finally {
                removeMarkers(tempMarkers);
            }
        }
    }


    public static Marker createMarker(final String name) {
        return MarkerFactory.getMarker(name);
    }
}
