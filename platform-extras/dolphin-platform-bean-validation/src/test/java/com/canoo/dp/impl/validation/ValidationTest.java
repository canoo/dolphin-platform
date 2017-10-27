<<<<<<< HEAD
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
=======
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

import javax.validation.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

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

    @Test
    public void testPastValidationBasic() {
        TestBeanPast bean = new TestBeanPast();
        ConstraintViolation<TestBeanPast> violation;

        Set<ConstraintViolation<TestBeanPast>> violations = null;

        // Empty bean must be good
        violations = validator.validate(bean);
        assertEquals(violations.size(), 0);

        // Past date must be good
        bean.dateProperty().set(new Date(0L) );
        Calendar past = Calendar.getInstance();
        past.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) - 1); // Gotta be the past
        bean.calendarProperty().set(past);

        violations = validator.validate(bean);
        assertEquals(violations.size(), 0);

        // Future date must be bad
        bean.dateProperty().set(new Date(new Date().getTime() + 1000000) ); // Gotta be the future?

        violations = validator.validate(bean);
        assertEquals(violations.size(), 1);
        violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().iterator().next().getName(), "date");

        // Reset back to clean
        bean.dateProperty().set(new Date(0L) );

        violations = validator.validate(bean);
        assertEquals(violations.size(), 0);

        // Future calendar must be bad
        Calendar future = Calendar.getInstance();
        future.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) + 1);
        bean.calendarProperty().set(future);

        violations = validator.validate(bean);
        assertEquals(violations.size(), 1);
        violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().iterator().next().getName(), "calendar");

        // Past calendar must be good
        Calendar pastCalendar = Calendar.getInstance();
        pastCalendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) - 1);
        bean.calendarProperty().set(pastCalendar);

        violations = validator.validate(bean);
        assertEquals(violations.size(), 0);

        // Check when both fields are invalid
        future = Calendar.getInstance();
        future.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) + 1);
        bean.calendarProperty().set(future);
        bean.dateProperty().set(new Date(new Date().getTime() + 1000000) );

        violations = validator.validate(bean);
        assertEquals(violations.size(), 2);

        // Lastly, check validations on invalid type
        TestBeanPastInvalidAnnotation invalid = new TestBeanPastInvalidAnnotation();
        invalid.dateProperty().set((short) 5);
        try {
            validator.validate(invalid);
            fail("Validator must have caught invalid type.");
        } catch (ValidationException ve) {
            // Runtime exception must be thrown when invalid type is validated
        }
    }

    /*
     * Same test as testPastValidationBasic but inverted from past to future
     */
    @Test
    public void testFutureValidationBasic() {
        TestBeanFuture bean = new TestBeanFuture();
        ConstraintViolation<TestBeanFuture> violation;

        Set<ConstraintViolation<TestBeanFuture> > violations = null;

        // Empty bean must be good
        violations = validator.validate(bean);
        assertEquals(violations.size(), 0);

        // Future date must be good
        bean.dateProperty().set(
                new Date(new Date().getTime() + 1000000)
        );

        violations = validator.validate(bean);
        assertEquals(violations.size(), 0);

        // Past date must be bad
        bean.dateProperty().set(new Date(new Date().getTime() - 1000000) );

        violations = validator.validate(bean);
        assertEquals(violations.size(), 1);
        violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().iterator().next().getName(), "date");

        // Reset back to clean
        bean.dateProperty().set(new Date(new Date().getTime() + 1000000) );

        violations = validator.validate(bean);
        assertEquals(violations.size(), 0);

        // Past calendar must be bad
        Calendar past = Calendar.getInstance();
        past.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) - 1);
        bean.calendarProperty().set(past);

        violations = validator.validate(bean);
        assertEquals(violations.size(), 1);
        violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().iterator().next().getName(), "calendar");

        // Future calendar must be good
        Calendar future = Calendar.getInstance();
        future.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) + 1);
        bean.calendarProperty().set(future);

        violations = validator.validate(bean);
        assertEquals(violations.size(), 0);

        // Check when both fields are invalid
        past = Calendar.getInstance();
        past.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) - 1);
        bean.calendarProperty().set(past);
        bean.dateProperty().set(new Date(new Date().getTime() - 1000000) );

        violations = validator.validate(bean);
        assertEquals(violations.size(), 2);

        // Lastly, check validations on invalid type
        TestBeanFutureInvalidAnnotation invalid = new TestBeanFutureInvalidAnnotation();
        invalid.dateProperty().set((short) 5);
        try {
            validator.validate(invalid);
            fail("Validator must have caught invalid type.");
        } catch (ValidationException ve) {
            // Runtime exception must be thrown when invalid type is validated
        }
    }

    @Test
    public void testMinValidationBasic() {
        TestBeanMin bean = new TestBeanMin();

        ConstraintViolation<TestBeanMin> violation;

        Set<ConstraintViolation<TestBeanMin> > violations = null;

        {   // Long
            // Empty bean must be good
            violations = validator.validate(bean);
            assertEquals(violations.size(), 0);

            // Basic test when number is higher than min
            bean.longProperty().set(2L);
            violations = validator.validate(bean);
            assertEquals(violations.size(), 0);

            // Basic test when number is equal to min
            bean.longProperty().set(1L);
            violations = validator.validate(bean);
            assertEquals(violations.size(), 0);

            // Basic test when number is less than min
            bean.longProperty().set(0L);
            violations = validator.validate(bean);
            assertEquals(violations.size(), 1);
            violation = violations.iterator().next();
            assertEquals(violation.getPropertyPath().iterator().next().getName(), "longProperty");
        }

        resetMinTestBean(bean);

        {   // Same with byte
            bean.byteProperty().set((byte) 2);
            violations = validator.validate(bean);
            assertEquals(violations.size(), 0);

            bean.byteProperty().set((byte) 1);
            violations = validator.validate(bean);
            assertEquals(violations.size(), 0);

            bean.byteProperty().set((byte) 0);
            violations = validator.validate(bean);
            assertEquals(violations.size(), 1);
            violation = violations.iterator().next();
            assertEquals(violation.getPropertyPath().iterator().next().getName(), "byteProperty");
        }

        resetMinTestBean(bean);

        {   // BigInteger
            bean.bigIntegerProperty().set(BigInteger.valueOf(2) );
            violations = validator.validate(bean);
            assertEquals(violations.size(), 0);

            bean.bigIntegerProperty().set(BigInteger.valueOf(1) );
            violations = validator.validate(bean);
            assertEquals(violations.size(), 0);

            bean.bigIntegerProperty().set(BigInteger.valueOf(0) );
            violations = validator.validate(bean);
            assertEquals(violations.size(), 1);
            violation = violations.iterator().next();
            assertEquals(violation.getPropertyPath().iterator().next().getName(), "bigInteger");
        }

        resetMinTestBean(bean);

        {   // BigDecimal
            bean.bigDecimalProperty().set(BigDecimal.valueOf(2) );
            violations = validator.validate(bean);
            assertEquals(violations.size(), 0);

            bean.bigDecimalProperty().set(BigDecimal.valueOf(1) );
            violations = validator.validate(bean);
            assertEquals(violations.size(), 0);

            bean.bigDecimalProperty().set(BigDecimal.valueOf(0) );
            violations = validator.validate(bean);
            assertEquals(violations.size(), 1);
            violation = violations.iterator().next();
            assertEquals(violation.getPropertyPath().iterator().next().getName(), "bigDecimal");
        }

        resetMinTestBean(bean);

        bean.stringProperty().set("Dummy string.");
        try {
            violations = validator.validate(bean);
            fail("Did not throw wrong validation data type exception.");
        } catch (ValidationException ve) {
            // Must catch runtime exception on wrong validation data type
        }

    }

    @Test
    public void testMinValidationExtremeValues() {
        TestBeanMin bean = new TestBeanMin();

        Set<ConstraintViolation<TestBeanMin> > violations = null;

        // +
        BigInteger extremeValue = new BigInteger(String.valueOf(Long.MAX_VALUE));
        extremeValue = extremeValue.add(extremeValue);  // double the max long value
        bean.bigIntegerProperty().set(extremeValue);

        violations = validator.validate(bean);
        assertEquals(violations.size(), 0);

        // -
        extremeValue = new BigInteger(String.valueOf(Long.MIN_VALUE));
        extremeValue = extremeValue.add(extremeValue);  // double the min long value
        bean.bigIntegerProperty().set(extremeValue);

        violations = validator.validate(bean);
        assertEquals(violations.size(), 1);

        assertEquals(violations.iterator().next().getPropertyPath().iterator().next().getName(), "bigInteger");

        // TODO: add more
    }

    private void resetMinTestBean(TestBeanMin bean) {
        bean.bigDecimalProperty().set(null);
        bean.bigIntegerProperty().set(null);
        bean.longProperty().set(null);
        bean.byteProperty().set(null);
        assertEquals(validator.validate(bean).size(), 0);
    }
    
    @Test
    public void testDigitsValidator() {
        @DolphinBean
        class TestedClass {
            @Digits(integer=4,fraction=2)
            private Property<CharSequence> value = new MockedProperty<>();
        }

        TestedClass bean = new TestedClass();
        Set<ConstraintViolation<TestedClass>> violations;
        ConstraintViolation<TestedClass> violation;

		// test valid state
        bean.value.set("1234.34");
        violations = validator.validate(bean);
        assertEquals(violations.size(), 0);
        
        // test invalid numeric format
        bean.value.set("1234.343");
        violations = validator.validate(bean);
        assertEquals(violations.size(), 1);
        violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().iterator().next().getName(), "value");
    }

}
>>>>>>> branch 'master' of https://github.com/canoo/dolphin-platform.git
