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

import com.canoo.dp.impl.platform.core.Assert;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Validator that adds Dolphin Platform property support for the {@link Pattern} annotation.
 */
public final class PatternPropertyValidator extends AbstractPropertyValidator<Pattern, CharSequence> {

    private java.util.regex.Pattern pattern;

    /**
     * constructor
     */
    public PatternPropertyValidator() {
        super(CharSequence.class);
    }

    @Override
    public void initialize(final Pattern annotation) {
        Assert.requireNonNull(annotation, "annotation");

        int flags = combineFlags(annotation.flags());
        pattern = java.util.regex.Pattern.compile(annotation.regexp(), flags);
    }

    /**
     * Combines a given set of javax.validation.constraints.Pattern.Flag instances into one bitmask suitable for
     * java.util.regex.Pattern consumption.
     *
     * @param flags - list of javax.validation.constraints.Pattern.Flag instances to combine
     * @return combined bitmask for regex flags
     */
    private int combineFlags(final Pattern.Flag[] flags) {
        int combined = 0;
        for (Pattern.Flag f : flags) {
            combined |= f.getValue();
        }
        return combined;
    }

    @Override
    protected boolean checkValid(
            @NotNull final CharSequence value,
            final ConstraintValidatorContext context) {
        return pattern.matcher(value).matches();
    }

}


