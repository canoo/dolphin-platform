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
import com.canoo.platform.core.functional.Subscription;
import com.canoo.platform.logging.DolphinLoggerConfiguration;
import com.canoo.platform.logging.spi.DolphinLoggerBridge;
import com.canoo.platform.logging.spi.DolphinLoggerBridgeFactory;
import com.canoo.platform.logging.spi.LogMessage;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class DolphinLoggerFactory implements ILoggerFactory {

    private static final List<LogMessage> messageCache = new CopyOnWriteArrayList<>();

    private static final List<Consumer<List<LogMessage>>> listeners = new CopyOnWriteArrayList<>();

    private static final int maxCacheSize = 10_000;

    private final AtomicBoolean configured = new AtomicBoolean(false);

    private final ConcurrentMap<String, DolphinLogger> loggerMap = new ConcurrentHashMap();

    private final List<DolphinLoggerBridge> bridges = new CopyOnWriteArrayList<>();

    private DolphinLoggerConfiguration configuration;

    @Override
    public synchronized Logger getLogger(final String name) {
        if (!configured.get()) {
            configure();
        }
        final DolphinLogger logger = this.loggerMap.get(name);
        if (logger != null) {
            return logger;
        } else {
            final Level loggerLevel = configuration.getLevelFor(name);
            final DolphinLogger newInstance = new DolphinLogger(this, name, bridges, loggerLevel);
            final DolphinLogger oldInstance = this.loggerMap.putIfAbsent(name, newInstance);
            return oldInstance == null ? newInstance : oldInstance;
        }
    }

    private synchronized void configure() {
        this.configuration = LoggingConfigurationFileLoader.loadConfiguration();
        Assert.requireNonNull(configuration, "configuration");
        bridges.clear();

        final Iterator<DolphinLoggerBridgeFactory> iterator = ServiceLoader.load(DolphinLoggerBridgeFactory.class).iterator();
        while (iterator.hasNext()) {
            final DolphinLoggerBridge bridge = iterator.next().create(configuration);
            if(bridge != null) {
                bridges.add(bridge);
            }
        }

        for(final DolphinLogger logger : loggerMap.values()) {
            logger.updateBridges(Collections.unmodifiableList(bridges));
            final Level level = configuration.getLevelFor(logger.getName());
            logger.setLevel(level);
        }
        configured.set(true);
    }

    public void addToCache(final LogMessage logMessage) {
        //TODO: Must be done in background thread...
        while(messageCache.size() > maxCacheSize) {
            messageCache.remove(0);
        }
        messageCache.add(logMessage);

        listeners.forEach(l -> {
            try {
                l.accept(messageCache);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static List<LogMessage> getLogCache() {
        return Collections.unmodifiableList(messageCache);
    }

    public static Subscription addListener(final Consumer<List<LogMessage>> listener) {
        Assert.requireNonNull(listener, "listener");
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }
}
