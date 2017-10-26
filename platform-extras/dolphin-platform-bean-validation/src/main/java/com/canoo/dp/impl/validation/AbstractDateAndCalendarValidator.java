package com.canoo.dp.impl.validation;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidationException;
import java.lang.annotation.Annotation;
import java.util.Calendar;
import java.util.Date;

/**
 * Utility abstract class to minimize {@link Date}-{@link Calendar} type checking.
 * <p>
 * Note: using plain {@code Object} as data type because {@link Date} and {@link Calendar} have no common ancestor.
 *
 * @param <A> defines the annotation of the constraints
 */
public abstract class AbstractDateAndCalendarValidator<A extends Annotation> extends AbstractPropertyValidator<A, Object> {

    /**
     * Constructor
     */
    public AbstractDateAndCalendarValidator() {
        super(Object.class);
    }

    /**
     * A validation hook to mask type checks.
     *
     * @param date - the date, guaranteed non-null
     * @param context - validation context
     * @return true if {@code date} is valid, false otherwise
     */
    protected abstract boolean checkValidDate(Date date, ConstraintValidatorContext context);

    /**
     * Template method to mask type checks.
     *
     * @param calendar - the calendar object, guaranteed non-null
     * @param context - validation context
     * @return true if {@code calendar} is valid, false otherwise
     */
    protected abstract boolean checkValidCalendar(Calendar calendar, ConstraintValidatorContext context);

    @Override
    protected boolean checkValid(final Object value, final ConstraintValidatorContext context) {
        if (value instanceof Date) {
            return checkValidDate((Date) value, context);
        } else if (value instanceof Calendar) {
            return checkValidCalendar((Calendar) value, context);
        } else {
            throw new ValidationException("Property contains value of type " + value.getClass() + " whereas only java.util.Date and java.util.Calendar are supported.");
        }
    }
}
