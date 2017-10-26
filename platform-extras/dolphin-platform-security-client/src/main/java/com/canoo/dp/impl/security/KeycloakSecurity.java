package com.canoo.dp.impl.security;

import com.canoo.dp.impl.platform.client.http.DefaultHttpURLConnectionFactory;
import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.platform.core.PlatformConstants;
import com.canoo.platform.client.PlatformClient;
import com.canoo.platform.client.security.Security;
import com.canoo.platform.core.DolphinRuntimeException;
import com.canoo.platform.core.http.RequestMethod;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class KeycloakSecurity implements Security {

    //Setup for the realm / client -> "Direct Grand" must be enabled in keycloak admin

    private static final Logger LOG = LoggerFactory.getLogger(KeycloakSecurity.class);


    private final String authEndpoint;

    private final String realmName;

    private final String appName;

    private final ExecutorService executor;

    private KeycloakOpenidConnectResult connectResult;

    private long tokenCreation;

    public KeycloakSecurity(final String authEndpoint, final String realmName, final String appName, final ExecutorService executor) {
        this.appName = Assert.requireNonBlank(appName, "appName");
        this.authEndpoint = Assert.requireNonBlank(authEndpoint, "authEndpoint");
        this.realmName = Assert.requireNonBlank(realmName, "realmName");
        this.executor = Assert.requireNonNull(executor, "executor");
    }

    private void receiveTokenFromKeycloak(final String content) throws IOException {
        LOG.debug("receiving new token from keycloak server");
        final byte[] rawContent = content.getBytes(PlatformConstants.CHARSET);
        final URL url = new URL(authEndpoint + "/auth/realms/" + realmName + "/protocol/openid-connect/token");
        final HttpURLConnection connection = new DefaultHttpURLConnectionFactory().create(url);
        connection.setRequestMethod(RequestMethod.POST.getRawName());
        connection.setRequestProperty("charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", rawContent.length + "");
        connection.setDoOutput(true);
        connection.setDoInput(true);

        final OutputStream w = connection.getOutputStream();
        w.write(rawContent);
        w.close();

        final InputStream is = connection.getInputStream();
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int read = is.read();
        while (read != -1) {
            byteArrayOutputStream.write(read);
            read = is.read();
        }
        final byte[] rawInput = byteArrayOutputStream.toByteArray();
        final String input = new String(rawInput);

        final Gson gson = PlatformClient.getService(Gson.class);
        final KeycloakOpenidConnectResult result = gson.fromJson(input, KeycloakOpenidConnectResult.class);
        connectResult = result;
        tokenCreation = System.currentTimeMillis();
    }

    @Override
    public Future<Void> login(final String user, final String password) {
        return (Future<Void>) executor.submit(() -> {
            try {
                receiveTokenFromKeycloak("client_id=" + appName + "&username=" + user + "&password=" + password + "&grant_type=password");
            } catch (IOException e) {
                throw new DolphinRuntimeException("Error in security", e);
            }
        });
    }

    public Future<Void> refreshToken() {
        return (Future<Void>) executor.submit(() -> {
                try {
                    receiveTokenFromKeycloak("grant_type=refresh_token&refresh_token=" + connectResult.getRefresh_token() + "&client_id=" + appName);
                } catch (IOException e) {
                    throw new DolphinRuntimeException("Error in security", e);
                }
            });
    }

    public long tokenExpiresAt() {
        if (connectResult == null) {
            return -1;
        }
        return tokenCreation + connectResult.getExpires_in() * 1000;
    }

    @Override
    public Future<Void> logout() {
        //TODO: currently there is no perfect solution at keycloak to do such a logout
        connectResult = null;
        CompletableFuture<Void> resultHack = new CompletableFuture<>();
        resultHack.complete(null);
        return resultHack;
    }

    public boolean isAuthorized() {
        //REST Endpoint at Keycloak site -> Call must be done manually
        //http://www.keycloak.org/docs-api/3.3/rest-api/index.html
        //See /admin/realms/{realm}/users/{id}/sessions
        return connectResult != null;
    }

    public String getAccessToken() {
        if (connectResult == null) {
            return null;
        }
        return connectResult.getAccess_token();
    }
}
