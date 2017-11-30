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
package com.canoo.platform.remoting.spi.converter;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * Entry point of the data converter SPI. Custom implementations can be provided by the default Java SPI (see {@link java.util.ServiceLoader}) and will be used at runtime to convert custom data types that are used in Dolphin Platform beans (see {@link com.canoo.platform.remoting.DolphinBean}) to internally supported data types.
 *
 * @author Hendrik Ebbers
 */
@API(since = "0.x", status = MAINTAINED)
public interface ConverterFactory {

    /**
     * Init method that will be automatically called after the converter factory instance has been created.
     * @param beanRepository the internally used bean repository.
     */
    void init(DolphinBeanRepo beanRepository);

    /**
     * This method will be called to check if the converter supports the given custom data type
     * @param cls class of the custom data type that should be converted
     * @return true if this factory supports to convert the custom data type
     */
    boolean supportsType(Class<?> cls);

    /**
     * Returns a unique identifier.
     * @return a unique identifier
     */
    int getTypeIdentifier();

    /**
     * Returns a converter that can be used to convert a dolphin data type to a custom data type.
     * @param cls the dolphin data type
     * @return the converter
     */
    Converter getConverterForType(Class<?> cls);
}
