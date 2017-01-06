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
public class LongDolphinBinder extends AbstractNumericDolphinBinder<Long> {

    public LongDolphinBinder(final Property<Long> property) {
        super(property);
    }

    @Override
    protected boolean equals(Number n, Long aLong) {
        if (n == null && aLong != null) {
            return false;
        }
        if (n != null && aLong == null) {
            return false;
        }
        if (n == null && aLong == null) {
            return true;
        }
        return  n.longValue() - aLong.longValue() == 0l;
    }

    @Override
    protected BidirectionalConverter<Number, Long> getConverter() {
        return new BidirectionalConverter<Number, Long>() {
            @Override
            public Number convertBack(Long value) {
                if (value == null) {
                    return 0l;
                }
                return value;
            }

            @Override
            public Long convert(Number value) {
                if (value == null) {
                    return 0l;
                }
                return value.longValue();
            }
        };
    }

}

