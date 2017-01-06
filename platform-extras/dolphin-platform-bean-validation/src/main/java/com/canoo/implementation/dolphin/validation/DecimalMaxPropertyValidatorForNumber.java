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
import javax.validation.ValidationException;
import javax.validation.constraints.DecimalMax;

/**
 * Validator that adds Dolphin Platform property support for the {@link DecimalMax} annotation.
 */
public final class DecimalMaxPropertyValidatorForNumber extends AbstractPropertyValidator<DecimalMax, Number> {

    private double maxValue;

    private boolean inclusive;

    /**
     * constructor
     */
    public DecimalMaxPropertyValidatorForNumber() {
        super(Number.class);
    }

    @Override
    public void initialize(DecimalMax maxValue) {
        try {
            this.maxValue = Double.parseDouble(maxValue.value());
        } catch (NumberFormatException nfe) {
            throw new ValidationException("Not a number!", nfe);
        }
        this.inclusive = maxValue.inclusive();
    }

    @Override
    protected boolean checkValid(Number value, ConstraintValidatorContext context) {
        if (inclusive) {
            return value.doubleValue() <= maxValue;
        } else {
            return value.doubleValue() < maxValue;
        }
    }

}

