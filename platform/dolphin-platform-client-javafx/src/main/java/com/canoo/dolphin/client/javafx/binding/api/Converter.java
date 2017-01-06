/*
 * Copyright 2015-2016 Canoo Engineering AG.
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
package com.canoo.dolphin.client.javafx.binding.api;

/**
 * Defines a converter that can convert a data type into a different data type. This converters are normally used to bind
 * JavaFX properties unidirectional to Dolphin Platform properties of a different type. For bidiertional bindings see {@link BidirectionalConverter}
 * @param <T> type of the first data type
 * @param <U> type of the second data type
 */
public interface Converter<T, U> {

    /**
     * Converts the given value to another data gtype
     * @param value the value that should be converted
     * @return the converted value
     */
    U convert(T value);

}
