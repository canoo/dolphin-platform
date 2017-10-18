package com.canoo.platform.client;

import org.apiguardian.api.API;

import java.util.concurrent.Executor;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(since = "0.19.0", status = EXPERIMENTAL)
public interface Toolkit {

    Executor getUiExecutor();

}
