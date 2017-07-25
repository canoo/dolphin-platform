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
package com.canoo.dolphin.impl;

import com.canoo.dolphin.converter.Converter;
import com.canoo.dolphin.converter.ConverterFactory;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dp.impl.platform.core.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * The class {@code Converters} contains all {@link Converter} that are used in the Dolphin Platform.
 */
public class Converters {

    private static final Logger LOG = LoggerFactory.getLogger(Converters.class);

    private final List<ConverterFactory> converterFactories;

    public Converters(final BeanRepository beanRepository) {
        converterFactories = new ArrayList<>();
        ServiceLoader<ConverterFactory> loader = ServiceLoader.load(ConverterFactory.class);
        loader.reload();
        Iterator<ConverterFactory> iterator = loader.iterator();
        while (iterator.hasNext()) {
            ConverterFactory factory = iterator.next();
            LOG.trace("Found converter factory {} with type identifier {}", factory.getClass(), factory.getTypeIdentifier());
            factory.init(beanRepository);
            converterFactories.add(factory);
        }
    }

    public int getFieldType(Class<?> clazz) {
        return getFactory(clazz).getTypeIdentifier();
    }

    public Converter getConverter(Class<?> clazz) {
        return getFactory(clazz).getConverterForType(clazz);
    }

    private ConverterFactory getFactory(Class<?> clazz) {
        Assert.requireNonNull(clazz, "clazz");
        List<ConverterFactory> foundConverters = new ArrayList<>();
        for (ConverterFactory factory : converterFactories) {
            if (factory.supportsType(clazz)) {
                foundConverters.add(factory);
            }
        }
        if (foundConverters.size() > 1) {
            throw new RuntimeException("More than 1 converter instance found to convert " + clazz);
        }
        if (foundConverters.isEmpty()) {
            throw new RuntimeException("No converter instance found to convert " + clazz);
        }
        return foundConverters.get(0);
    }
}
