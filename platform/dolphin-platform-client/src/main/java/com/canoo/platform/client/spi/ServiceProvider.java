package com.canoo.platform.client.spi;

import com.canoo.platform.client.ClientConfiguration;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(since = "0.19.0", status = EXPERIMENTAL)
public interface ServiceProvider<S> {

    S getService(ClientConfiguration configuration);

    Class<S> getServiceType();
}
