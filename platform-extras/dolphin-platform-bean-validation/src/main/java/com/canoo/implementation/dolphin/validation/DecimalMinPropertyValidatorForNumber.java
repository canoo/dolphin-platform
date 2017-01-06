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
package com.canoo.implementation.dolphin.validation;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.DecimalMin;

/**
 * Validator that adds Dolphin Platform property support for the {@link DecimalMin} annotation.
 */
public final class DecimalMinPropertyValidatorForNumber extends AbstractPropertyValidator<DecimalMin, Number> {

    private double minValue;

    private boolean inclusive;

    /**
     * constructor
     */
    public DecimalMinPropertyValidatorForNumber() {
        super(Number.class);
    }

    @Override
    public void initialize(DecimalMin minValue) {
        try {
            this.minValue = Double.parseDouble(minValue.value());
        } catch (NumberFormatException nfe) {
            throw new RuntimeException("TODO");
        }
        this.inclusive = minValue.inclusive();
    }

    @Override
    protected boolean checkValid(Number value, ConstraintValidatorContext context) {
        if (inclusive) {
            return value.doubleValue() >= minValue;
        } else {
            return value.doubleValue() > minValue;
        }
    }

}


