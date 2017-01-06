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
import javax.validation.constraints.AssertTrue;

/**
 * Validator that adds Dolphin Platform property support for the {@link AssertTrue} annotation.
 */
public final class AssertTruePropertyValidator extends AbstractPropertyValidator<AssertTrue, Boolean> {

    /**
     * constructor
     */
    public AssertTruePropertyValidator() {
        super(Boolean.class);
    }

    @Override
    protected boolean checkValid(Boolean property, ConstraintValidatorContext context) {
        return property;
    }

}

