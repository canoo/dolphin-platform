/*
 * Copyright 2015-2018 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dp.impl.security;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.client.security.Security;
import com.canoo.platform.core.DolphinRuntimeException;
import com.canoo.platform.core.http.HttpURLConnectionHandler;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;

import static com.canoo.dp.impl.security.SecurityHttpHeader.*;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.19.0", status = INTERNAL)
public class KeycloakRequestHandler implements HttpURLConnectionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(KeycloakRequestHandler.class);

    public final static long MIN_TOKEN_LIFETIME = 5000;

    private final Security security;

    public KeycloakRequestHandler(final Security security) {
        this.security = Assert.requireNonNull(security, "security");
    }

    @Override
    public void handle(HttpURLConnection connection) {
        //No redirect, can not be handled in Java
        connection.setRequestProperty(SecurityHttpHeader.BEARER_ONLY_HEADER, "true");

        connection.setRequestProperty(REALM_NAME_HEADER, security.getRealm());
        connection.setRequestProperty(APPLICATION_NAME_HEADER, security.getApp());

        if (security.isAuthorized()) {
            long tokenLifetime = security.tokenExpiresAt() - System.currentTimeMillis();
            LOG.debug("security token will expire in " + tokenLifetime + " ms");
            if (tokenLifetime < MIN_TOKEN_LIFETIME) {
                LOG.debug("Need to refresh security token");
                try {
                    security.refreshToken().get();
                } catch (Exception e) {
                    throw new DolphinRuntimeException("Can not refresh security token", e);
                }
            }
        }
        String accessToken = security.getAccessToken();
        if (accessToken != null && !accessToken.isEmpty()) {
            LOG.debug("Adding security access token to request");
            connection.setRequestProperty(AUTHORIZATION_HEADER, BEARER + accessToken);
        }
    }
}
