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

import com.canoo.dp.impl.remoting.MockedProperty;
import com.canoo.platform.remoting.DolphinBean;
import com.canoo.platform.remoting.Property;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.validation.Configuration;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.*;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ValidationTest {

    private Validator validator;

    @BeforeClass
    public void setup() {
        Configuration<?> validationConf = Validation.byDefaultProvider().configure();
        validator = validationConf.buildValidatorFactory().getValidator();
    }

    @Test
    public void testPatternValidator() {
        @DolphinBean
        class TestedClass {
            @Pattern(regexp = "^a.*b$",flags = Pattern.Flag.CASE_INSENSITIVE)
            private Property<CharSequence> value = new MockedProperty<>();
        }

        TestedClass bean = new TestedClass();
        Set<ConstraintViolation<TestedClass>> violations;
        ConstraintViolation<TestedClass> violation;

        bean.value.set(null);
        violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "Null value should not trigger validation error");

        bean.value.set("a_valid_B");
        violations = validator.validate(bean);
        assertTrue(violations.isEmpty(), "'a_valid_B' should match pattern '^a.*b$'");

        bean.value.set("a_not_valid_c");
        violations = validator.validate(bean);
        assertEquals(violations.size(), 1, "'a_valid_c' should not match pattern '^a.*b$'");
        violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().iterator().next().getName(), "value");
    }

    @Test
    public void testNotNullValidator() {
        @DolphinBean
        class TestedClass {
            @NotNull
            private Property<Object> value = new MockedProperty<>();
        }

        TestedClass bean = new TestedClass();
        Set<ConstraintViolation<TestedClass>> violations;
        ConstraintViolation<TestedClass> violation;

        bean.value.set(null);
        violations = validator.validate(bean);
        assertEquals(violations.size(), 1);
        violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().iterator().next().getName(), "value");

        bean.value.set("YEAH!");
        violations = validator.validate(bean);
        assertEquals(violations.size(), 0);
    }

    @Test
    public void testNullValidator() {
        @DolphinBean
        class TestedClass {
            @Null
            private Property<Object> value = new MockedProperty<>();
        }

        TestedClass bean = new TestedClass();
        Set<ConstraintViolation<TestedClass>> violations;
        ConstraintViolation<TestedClass> violation;

        bean.value.set(null);
        violations = validator.validate(bean);
        assertEquals(violations.size(), 0);

        bean.value.set("TEST");
        violations = validator.validate(bean);
        assertEquals(violations.size(), 1);
        violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().iterator().next().getName(), "value");
    }

    @Test
    public void testAssertTrueValidator() {
        @DolphinBean
        class TestedClass {
            @AssertTrue
            private Property<Boolean> value = new MockedProperty<>();
        }

        TestedClass bean = new TestedClass();
        Set<ConstraintViolation<TestedClass>> violations;
        ConstraintViolation<TestedClass> violation;

        bean.value.set(false);
        violations = validator.validate(bean);
        assertEquals(violations.size(), 1);
        violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().iterator().next().getName(), "value");

        bean.value.set(true);
        violations = validator.validate(bean);
        assertEquals(violations.size(), 0);
    }

    @Test
    public void testAssertFalseValidator() {
        @DolphinBean
        class TestedClass {
            @AssertFalse
            private Property<Boolean> value = new MockedProperty<>();
        }

        TestedClass bean = new TestedClass();
        Set<ConstraintViolation<TestedClass>> violations;
        ConstraintViolation<TestedClass> violation;

        bean.value.set(true);
        violations = validator.validate(bean);
        assertEquals(violations.size(), 1);
        violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().iterator().next().getName(), "value");

        bean.value.set(false);
        violations = validator.validate(bean);
        assertEquals(violations.size(), 0);
    }
}
