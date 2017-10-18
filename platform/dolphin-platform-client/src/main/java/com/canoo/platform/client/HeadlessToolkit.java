package com.canoo.platform.client;

import org.apiguardian.api.API;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(since = "0.19.0", status = EXPERIMENTAL)
public class HeadlessToolkit implements Toolkit {
    private final static Executor executor = Executors.newSingleThreadExecutor();

    @Override
    public Executor getUiExecutor() {
        return executor;
    }
}
