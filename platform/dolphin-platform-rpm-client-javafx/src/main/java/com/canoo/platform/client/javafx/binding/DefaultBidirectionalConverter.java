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
package com.canoo.platform.client.javafx.binding;

import com.canoo.dp.impl.platform.core.Assert;

public class DefaultBidirectionalConverter<T, U> implements BidirectionalConverter<T, U> {

    private Converter<T, U> converter;

    private Converter<U, T> backConverter;

    public DefaultBidirectionalConverter(Converter<T, U> converter, Converter<U, T> backConverter) {
        this.converter = Assert.requireNonNull(converter, "converter");
        this.backConverter = Assert.requireNonNull(backConverter, "backConverter");
    }

    @Override
    public T convertBack(U value) {
        return backConverter.convert(value);
    }

    @Override
    public U convert(T value) {
        return converter.convert(value);
    }
}
