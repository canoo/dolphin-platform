package com.canoo.dp.impl.validation;

import com.canoo.dp.impl.platform.core.Assert;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Past;
import java.util.Calendar;
import java.util.Date;

/**
 * Validator that adds Dolphin Platform property support for the {@link Past} annotation.
 *
 */
public final class PastPropertyValidator extends AbstractDateAndCalendarValidator<Past> {

    @Override
    protected boolean checkValidDate(final Date date, final ConstraintValidatorContext context) {
        return date.before(new Date() );
    }

    @Override
    protected boolean checkValidCalendar(final Calendar calendar, final ConstraintValidatorContext context) {
        return calendar.before(Calendar.getInstance() );
    }
}
