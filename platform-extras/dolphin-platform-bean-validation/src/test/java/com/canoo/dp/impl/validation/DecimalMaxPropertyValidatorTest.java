package com.canoo.dp.impl.validation;

import com.canoo.dp.impl.remoting.MockedProperty;
import com.canoo.platform.remoting.DolphinBean;
import com.canoo.platform.remoting.Property;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.validation.*;
import javax.validation.constraints.DecimalMax;
import java.math.BigDecimal;
import java.util.Set;

import static org.testng.Assert.assertEquals;

/*
 * TODO: this test is base bones, needs more.
 */
public class DecimalMaxPropertyValidatorTest {

    @DolphinBean
    private class MaxBean {
        // inclusive = true by default
        @DecimalMax(value = "1234.5E-4")
        private Property<BigDecimal> bigDecimal = new MockedProperty<>();

        public Property<BigDecimal> bigDecimalProperty() {
            return bigDecimal;
        }
    }

    private Validator validator;

    @BeforeClass
    public void setup() {
        Configuration<?> validationConf = Validation.byDefaultProvider().configure();
        validator = validationConf.buildValidatorFactory().getValidator();
    }

    @Test
    public void testCheckValidBasic() {
        MaxBean bean = new MaxBean();

        Set<ConstraintViolation<MaxBean>> violations;

        bean.bigDecimalProperty().set(new BigDecimal("1234.4E-4"));
        violations = validator.validate(bean);
        assertEquals(violations.size(), 0);

        bean.bigDecimalProperty().set(new BigDecimal("1234.5E-4"));
        violations = validator.validate(bean);
        assertEquals(violations.size(), 0);

        bean.bigDecimalProperty().set(new BigDecimal("1234.6E-4"));
        violations = validator.validate(bean);
        assertEquals(violations.size(), 1);
    }

}
