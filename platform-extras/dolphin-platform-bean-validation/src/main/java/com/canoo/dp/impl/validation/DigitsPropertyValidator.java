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

import java.math.BigDecimal;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Digits;

/**
 * Validator that adds Dolphin Platform property support for the {@link Digits} annotation.
 */
public class DigitsPropertyValidator extends AbstractPropertyValidator<Digits, CharSequence> {

	private int maxIntegerLength;
	
	private int maxFractionLength;

	/**
	 * constructor
	 */
	public DigitsPropertyValidator() {
		super(CharSequence.class);
	}

	@Override
	public void initialize(final Digits digitsAnnotation) {
		this.maxIntegerLength = digitsAnnotation.integer();
		this.maxFractionLength = digitsAnnotation.fraction();
		validateParameters();
	}
	
	private BigDecimal getBigDecimalValue(final CharSequence charSequence) {
	    BigDecimal bd;
	    try {
	        bd = new BigDecimal( charSequence.toString() );
	    }
	    catch ( NumberFormatException nfe ) {
	        return null;
	    }
	    return bd;
	}

	@Override
	protected boolean checkValid(final CharSequence charSequence, final ConstraintValidatorContext context) {
		//null values are valid
	    if ( charSequence == null ) {
	        return true;
	    }

	    final BigDecimal bigNum = getBigDecimalValue( charSequence );
	    if ( bigNum == null ) {
	        return false;
	    }

	    final int integerPartLength = bigNum.precision() - bigNum.scale();
	    final int fractionPartLength = bigNum.scale() < 0 ? 0 : bigNum.scale();

	    return ( maxIntegerLength >= integerPartLength && maxFractionLength >= fractionPartLength );

	}
	
	private void validateParameters() {
		if ( maxIntegerLength < 0 ) {
			throw new IllegalArgumentException("The length of the integer part cannot be negative.");
		}
		if ( maxFractionLength < 0 ) {
			throw new IllegalArgumentException("The length of the fraction part cannot be negative.");
		}
	}
}
