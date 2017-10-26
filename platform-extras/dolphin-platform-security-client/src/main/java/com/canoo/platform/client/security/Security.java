package com.canoo.platform.client.security;

import org.apiguardian.api.API;

import java.util.concurrent.Future;

@API(since = "0.19.0", status = API.Status.EXPERIMENTAL)
public interface Security {

    Future<Void> login(String user, String password);

    Future<Void> logout();

    boolean isAuthorized();

    @Deprecated
    String getAccessToken();

    @Deprecated
    long tokenExpiresAt();

    @Deprecated
    Future<Void> refreshToken();
}
