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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.Set;

import static org.testng.Assert.*;

public class SizePropertyValidatorTest {
    private Validator validator;

    @BeforeClass
    public void setup() {
        Configuration<?> validationConf = Validation.byDefaultProvider().configure();
        validator = validationConf.buildValidatorFactory().getValidator();
    }

    @Test
    public void testCheckValid() throws Exception {
        @DolphinBean
        class TestedClass {
            @Size(min = 3, max = 8)
            private Property<Object> value = new MockedProperty<>();
        }

        TestedClass bean = new TestedClass();
        Set<ConstraintViolation<TestedClass>> violations;
        ConstraintViolation<TestedClass> violation;

        bean.value.set("hola");
        violations = validator.validate(bean);
        assertEquals(violations.size(), 0);

        bean.value.set("holayadios");
        violations = validator.validate(bean);
        assertEquals(violations.size(), 1);
        violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().iterator().next().getName(), "value");

        bean.value.set("ho");
        violations = validator.validate(bean);
        assertEquals(violations.size(), 1);
        violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().iterator().next().getName(), "value");
    }

}