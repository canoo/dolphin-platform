package com.canoo.platform.samples.reconnect;

import com.canoo.platform.remoting.DolphinBean;
import com.canoo.platform.remoting.Property;

import java.time.ZonedDateTime;

@DolphinBean
public class StatusBean {

    private Property<ZonedDateTime> time;

    public ZonedDateTime getTime() {
        return time.get();
    }

    public Property<ZonedDateTime> timeProperty() {
        return time;
    }

    public void setTime(final ZonedDateTime time) {
        this.time.set(time);
    }
}
