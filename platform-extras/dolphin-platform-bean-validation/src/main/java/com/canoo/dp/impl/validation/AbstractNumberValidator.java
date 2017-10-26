package com.canoo.dp.impl.validation;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidationException;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Utility abstract class to minimize {@link Number} type checking. Provides template methods for its implementations.
 * Override this abstract class when implementing number validators.
 * <p>
 *
 * @param <A> defines the annotation of the constraints
 */
public abstract class AbstractNumberValidator<A extends Annotation> extends AbstractPropertyValidator<A, Number> {

    public AbstractNumberValidator() {
        super(Number.class);
    }

    @Override
    protected boolean checkValid(final Number value, final ConstraintValidatorContext context) {
        if (value instanceof BigDecimal) {
            return checkValidBigDecimal((BigDecimal) value);
        } else if (value instanceof BigInteger) {
            return checkValidBigInteger((BigInteger) value);
        } else if (value instanceof CharSequence) {
            return checkValidCharSequence((CharSequence) value);
        } else if (value instanceof Byte ||
                value instanceof Short ||
                value instanceof Integer ||
                value instanceof Long) {
            return checkValidLong(value.longValue() );
        } else {
            throw new ValidationException("Property contains value of type " +
                    value.getClass() +
                    " whereas only BigDecimal, BigInteger, CharSequence, long, integer, " +
                    "short, byte and their wrappers are supported."
            );
        }
    }

    /**
     * Template method hook for number validations.
     * <p>
     * NOTE: all byte, short, integer, long and their respective wrappers are handled by long for convenience,
     * they are all embraced by long.
     *
     * @param value - the value, guaranteed not-null
     * @return true if the value passes validation, false otherwise
     */
    protected abstract boolean checkValidLong(Long value);

    /**
     * Template method hook for number validations.
     *
     * @param value - the value, guaranteed not-null
     * @return true if the value passes validation, false otherwise
     */
    protected abstract boolean checkValidCharSequence(CharSequence value);

    /**
     * Template method hook for number validations.
     * NOTE: casting types with {@code Big} prefix to {@code long} is not desired, because
     * it will retain only low order digits.
     *
     * @param value - the value, guaranteed not-null
     * @return true if the value passes validation, false otherwise
     */
    protected abstract boolean checkValidBigInteger(BigInteger value);

    /**
     * Template method hook for number validations.
     * NOTE: casting types with {@code Big} prefix to {@code long} is not desired, because
     * it will retain only low order digits.
     *
     * @param value - the value, guaranteed not-null
     * @return true if the value passes validation, false otherwise
     */
    protected abstract boolean checkValidBigDecimal(BigDecimal value);
}
