package com.canoo.dp.impl.validation;

import com.canoo.dp.impl.remoting.MockedProperty;
import com.canoo.platform.remoting.DolphinBean;
import com.canoo.platform.remoting.Property;

import javax.validation.constraints.Future;
import java.util.Calendar;
import java.util.Date;

@DolphinBean
public class TestBeanFuture {

    @Future
    private Property<Date> date = new MockedProperty<>();

    @Future
    private Property<Calendar> calendar = new MockedProperty<>();

    public Property<Date> dateProperty() {
        return date;
    }

    public Property<Calendar> calendarProperty() {
        return calendar;
    }

}
