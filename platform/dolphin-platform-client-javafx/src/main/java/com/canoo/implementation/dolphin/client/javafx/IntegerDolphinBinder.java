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
public class IntegerDolphinBinder extends AbstractNumericDolphinBinder<Integer> {

    public IntegerDolphinBinder(final Property<Integer> property) {
        super(property);
    }

    @Override
    protected boolean equals(Number n, Integer aInteger) {
        if (n == null && aInteger != null) {
            return false;
        }
        if (n != null && aInteger == null) {
            return false;
        }
        if (n == null && aInteger == null) {
            return true;
        }
        return  n.intValue() - aInteger.intValue() == 0;
    }

    @Override
    protected BidirectionalConverter<Number, Integer> getConverter() {
        return new BidirectionalConverter<Number, Integer>() {
            @Override
            public Number convertBack(Integer value) {
                if (value == null) {
                    return 0;
                }
                return value;
            }

            @Override
            public Integer convert(Number value) {
                if (value == null) {
                    return 0;
                }
                return value.intValue();
            }
        };
    }

}


