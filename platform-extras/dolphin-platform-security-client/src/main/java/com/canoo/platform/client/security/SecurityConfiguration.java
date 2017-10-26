package com.canoo.platform.client.security;

import org.apiguardian.api.API;

@API(since = "0.19.0", status = API.Status.EXPERIMENTAL)
public interface SecurityConfiguration {

    String AUTH_ENDPOINT = "platform.security.endpoint";
    String REALM_NAME = "platform.security.realm";
    String APP_NAME = "platform.security.app";
}
