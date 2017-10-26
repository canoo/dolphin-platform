package com.canoo.dp.impl.security;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.client.security.Security;
import com.canoo.platform.core.DolphinRuntimeException;
import com.canoo.platform.core.http.HttpURLConnectionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;

public class KeycloakRequestHandler implements HttpURLConnectionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(KeycloakRequestHandler.class);

    public final static long MIN_TOKEN_LIFETIME = 5000;

    private final Security security;

    public KeycloakRequestHandler(final Security security) {
        this.security = Assert.requireNonNull(security, "security");
    }

    @Override
    public void handle(HttpURLConnection connection) {
        if(security.isAuthorized()) {
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
        if(accessToken != null && !accessToken.isEmpty()) {
            LOG.debug("Adding security access token to request");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        }
    }
}
