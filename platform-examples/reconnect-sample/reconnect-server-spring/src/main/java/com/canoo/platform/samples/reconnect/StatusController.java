package com.canoo.platform.samples.reconnect;

import com.canoo.platform.remoting.server.DolphinController;
import com.canoo.platform.remoting.server.DolphinModel;
import com.canoo.platform.remoting.server.RemotingContext;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;

import static com.canoo.platform.samples.reconnect.RemotingConstants.STATUS_CONTROLLER_NAME;

@DolphinController(STATUS_CONTROLLER_NAME)
public class StatusController {

    @DolphinModel
    private StatusBean model;

    @Inject
    private RemotingContext context;

    @PostConstruct
    public void init() {
        Executors.newSingleThreadExecutor().submit(() -> {
            while (true) {
                Thread.sleep(1_000);
                context.createSessionExecutor().runLaterInClientSession(() -> model.setTime(ZonedDateTime.now()));
            }
        });
    }
}
