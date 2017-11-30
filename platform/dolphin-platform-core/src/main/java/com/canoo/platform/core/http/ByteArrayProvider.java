package com.canoo.platform.core.http;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(since = "0.x", status = EXPERIMENTAL)
@FunctionalInterface
public interface ByteArrayProvider {

    byte[] get();
}
