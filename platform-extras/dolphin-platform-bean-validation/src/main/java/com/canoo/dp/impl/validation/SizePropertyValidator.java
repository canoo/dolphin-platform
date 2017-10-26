package com.canoo.dp.impl.validation;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Size;

public class SizePropertyValidator extends AbstractPropertyValidator<Size, String> {

    private int maxSizeValue;
    private int minSizeValue;

    public SizePropertyValidator() {
        super(String.class);
    }

    @Override
    public void initialize(Size constraintAnnotation) {
        this.minSizeValue = constraintAnnotation.min();
        this.maxSizeValue = constraintAnnotation.max();
    }

    @Override
    protected boolean checkValid(String value, ConstraintValidatorContext context) {
        return (value != null && value.length() >= minSizeValue && value.length() <= maxSizeValue);
    }
}
