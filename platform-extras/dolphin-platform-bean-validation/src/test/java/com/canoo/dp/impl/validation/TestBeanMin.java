package com.canoo.dp.impl.validation;

import com.canoo.dp.impl.remoting.MockedProperty;
import com.canoo.platform.remoting.DolphinBean;
import com.canoo.platform.remoting.Property;

import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.math.BigInteger;

@DolphinBean
public class TestBeanMin {
    @Min(1)
    private Property<BigDecimal> bigDecimal = new MockedProperty<>();

    @Min(1)
    private Property<BigInteger> bigInteger = new MockedProperty<>();

    @Min(1)
    private Property<Long> longProperty = new MockedProperty<>();

    @Min(1)
    private Property<Byte> byteProperty = new MockedProperty<>();

    @Min(1)
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
