package com.canoo.dp.impl.validation;

import com.canoo.dp.impl.remoting.MockedProperty;
import com.canoo.platform.remoting.DolphinBean;
import com.canoo.platform.remoting.Property;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.validation.*;
import javax.validation.constraints.Max;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class MaxPropertyValidatorTest {

    @DolphinBean
    private class MaxBean {
        @Max(1)
        private Property<BigDecimal> bigDecimal = new MockedProperty<>();

        @Max(1)
        private Property<BigInteger> bigInteger = new MockedProperty<>();

        @Max(1)
        private Property<Long> longProperty = new MockedProperty<>();

        @Max(1)
        private Property<Byte> byteProperty = new MockedProperty<>();

        @Max(1)
        private Property<String> string = new MockedProperty<>();

        public Property<BigDecimal> bigDecimalProperty() {
            return bigDecimal;
        }

        public Property<BigInteger> bigIntegerProperty() {
            return bigInteger;
        }

        public Property<Long> longProperty() {
            return longProperty;
        }

        public Property<Byte> byteProperty() {
            return byteProperty;
        }

        public Property<String> stringProperty() {
            return string;
        }
    }

    private Validator validator;

    @BeforeClass
    public void setup() {
        Configuration<?> validationConf = Validation.byDefaultProvider().configure();
        validator = validationConf.buildValidatorFactory().getValidator();
    }

    @Test
    public void testCheckValidNull() {
        MaxBean bean = new MaxBean();

        Set<ConstraintViolation<MaxBean>> violations;

        // null values are good
        violations = validator.validate(bean);
        assertEquals(violations.size(), 0, "Null values are acceptable");
    }

    @Test
    public void testCheckValidBigInteger() {
        MaxBean bean = new MaxBean();

        Set<ConstraintViolation<MaxBean>> violations;
        ConstraintViolation<MaxBean> violation;

        bean.bigIntegerProperty().set(BigInteger.valueOf(2L) );
        violations = validator.validate(bean);
        assertEquals(violations.size(), 1);
        violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().iterator().next().getName(), "bigInteger", "2 is more than 1");

        bean.bigIntegerProperty().set(BigInteger.valueOf(1L) );
        violations = validator.validate(bean);
        assertEquals(violations.size(), 0, "1 is equal to 1");

        bean.bigIntegerProperty().set(BigInteger.valueOf(0L));
        violations = validator.validate(bean);
        assertEquals(violations.size(), 0, "0 is less than 1");
    }

    @Test
    public void testCheckValidLong() {
        MaxBean bean = new MaxBean();

        Set<ConstraintViolation<MaxBean>> violations;
        ConstraintViolation<MaxBean> violation;

        // Long prop
        bean.longProperty().set(2L);
        violations = validator.validate(bean);
        assertEquals(violations.size(), 1);
        violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().iterator().next().getName(), "longProperty", "2 is more than 1");

        bean.longProperty().set(1L);
        violations = validator.validate(bean);
        assertEquals(violations.size(), 0, "1 is equal to 1");

        bean.longProperty().set(0L);
        violations = validator.validate(bean);
        assertEquals(violations.size(), 0, "0 is less than 1");
    }

    @Test
    public void testWrongDataType() {
        MaxBean bean = new MaxBean();

        Set<ConstraintViolation<MaxBean>> violations;
        ConstraintViolation<MaxBean> violation;

        bean.stringProperty().set("String is not a number");
        try {
            violations = validator.validate(bean);
            fail("Exception must have been thrown because of wrong data type.");
        } catch (ValidationException ve) {
            // Must catch this exception
        }
    }

    @Test
    public void testCheckValidByte() {
        MaxBean bean = new MaxBean();

        Set<ConstraintViolation<MaxBean>> violations;
        ConstraintViolation<MaxBean> violation;

        // Long prop
        bean.byteProperty().set(Byte.valueOf("2") );
        violations = validator.validate(bean);
        assertEquals(violations.size(), 1);
        violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().iterator().next().getName(), "byteProperty", "2 is more than 1");

        bean.byteProperty().set(Byte.valueOf("1") );
        violations = validator.validate(bean);
        assertEquals(violations.size(), 0, "1 is equal to 1");

        bean.byteProperty().set(Byte.valueOf("0") );
        violations = validator.validate(bean);
        assertEquals(violations.size(), 0, "0 is less than 1");
    }
}
