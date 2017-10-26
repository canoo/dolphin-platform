package com.canoo.dp.impl.validation;

import javax.validation.ValidationException;
import javax.validation.constraints.Max;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Validator that adds Dolphin Platform property support for the {@link Max} annotation.
 */
public class MaxPropertyValidator extends AbstractNumberValidator<Max> {

    private long maxValue;

    @Override
    public void initialize(final Max constraint) {
        super.initialize(constraint);
        maxValue = constraint.value();
    }

    @Override
    protected boolean checkValidLong(final Long value) {
        return value <= maxValue;
    }

    @Override
    protected boolean checkValidCharSequence(final CharSequence value) {
        throw new ValidationException("Max constraint does not support CharSequence validation: " + value);
    }

    @Override
    protected boolean checkValidBigInteger(final BigInteger value) {
        return value.compareTo(BigInteger.valueOf(maxValue) ) != 1;
    }

    @Override
    protected boolean checkValidBigDecimal(final BigDecimal value) {
        return value.compareTo(BigDecimal.valueOf(maxValue) ) != 1;
    }
}
