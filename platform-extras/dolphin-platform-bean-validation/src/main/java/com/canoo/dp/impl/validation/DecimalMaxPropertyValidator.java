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
package com.canoo.dp.impl.validation;

import javax.validation.constraints.DecimalMax;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Validator that adds Dolphin Platform property support for the {@link DecimalMax} annotation.
 */
public final class DecimalMaxPropertyValidator extends AbstractNumberValidator<DecimalMax> {

    private BigDecimal maxValue;

    private boolean inclusive;

    @Override
    public void initialize(final DecimalMax maxValue) {
        this.maxValue = new BigDecimal(maxValue.value() );
        this.inclusive = maxValue.inclusive();
    }

    @Override
    protected boolean checkValidLong(Long value) {
        return checkValidBigDecimal(BigDecimal.valueOf(value) );
    }

    @Override
    protected boolean checkValidCharSequence(CharSequence value) {
        return checkValidBigDecimal(new BigDecimal(value.toString() ) );
    }

    @Override
    protected boolean checkValidBigInteger(BigInteger value) {
        return checkValidBigDecimal(new BigDecimal(value) );
    }

    @Override
    protected boolean checkValidBigDecimal(BigDecimal value) {
        return inclusive ? value.compareTo(maxValue) != 1 : value.compareTo(maxValue) == -1;
    }

}
