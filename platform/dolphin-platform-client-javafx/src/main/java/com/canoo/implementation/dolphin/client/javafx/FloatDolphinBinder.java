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
package com.canoo.implementation.dolphin.client.javafx;

import com.canoo.dolphin.client.javafx.binding.api.BidirectionalConverter;
import com.canoo.dolphin.mapping.Property;

/**
 * Created by hendrikebbers on 29.09.15.
 */
public class FloatDolphinBinder extends AbstractNumericDolphinBinder<Float> {

    private final static double EPSILON = 1e-10f;

    public FloatDolphinBinder(final Property<Float> property) {
        super(property);
    }

    @Override
    protected boolean equals(Number n, Float aFloat) {
        if (n == null && aFloat != null) {
            return false;
        }
        if (n != null && aFloat == null) {
            return false;
        }
        if (n == null && aFloat == null) {
            return true;
        }
        return Math.abs(n.floatValue() - aFloat.floatValue()) < EPSILON;
    }

    @Override
    protected BidirectionalConverter<Number, Float> getConverter() {
        return new BidirectionalConverter<Number, Float>() {
            @Override
            public Number convertBack(Float value) {
                if (value == null) {
                    return 0.0f;
                }
                return value;
            }

            @Override
            public Float convert(Number value) {
                if (value == null) {
                    return 0.0f;
                }
                return value.floatValue();
            }
        };
    }

}

