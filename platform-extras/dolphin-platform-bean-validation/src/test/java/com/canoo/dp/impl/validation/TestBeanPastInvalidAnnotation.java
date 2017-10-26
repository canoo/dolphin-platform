package com.canoo.dp.impl.validation;

import com.canoo.dp.impl.remoting.MockedProperty;
import com.canoo.platform.remoting.DolphinBean;
import com.canoo.platform.remoting.Property;

import javax.validation.constraints.Past;

@DolphinBean
class TestBeanPastInvalidAnnotation {

    @Past
    private Property<Short> date = new MockedProperty<>();

    public Property<Short> dateProperty() {
        return date;
    }
}
