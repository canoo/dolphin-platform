package com.canoo.dp.impl.platform.client.http;

import com.canoo.platform.core.http.HttpResponse;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public interface HttpProvider<R> {

    HttpResponse<R> get() throws Exception;
}
