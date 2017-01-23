/*
 * Copyright 2015-2017 Canoo Engineering AG.
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
package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.client.ClientConfiguration;
import com.canoo.dolphin.client.DolphinSessionException;
import com.canoo.dolphin.client.HttpURLConnectionFactory;
import com.canoo.dolphin.client.HttpURLConnectionResponseHandler;
import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.common.Assert;
import com.canoo.dolphin.util.DolphinRemotingException;
import com.canoo.remoting.client.ClientDolphin;
import com.canoo.remoting.client.communication.AbstractClientConnector;
import com.canoo.remoting.client.communication.BlindCommandBatcher;
import com.canoo.communication.common.codec.Codec;
import com.canoo.communication.common.commands.Command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is used to sync the unique client scope id of the current dolphin
 */
public class DolphinPlatformHttpClientConnector extends AbstractClientConnector {

    private static final String CHARSET = "UTF-8";

    private static final String CONTENT_TYPE_HEADER = "Content-Type";

    private static final String ACCEPT_HEADER = "Accept";

    private static final String COOKIE_HEADER = "Cookie";

    private static final String SET_COOKIE_HEADER = "Set-Cookie";

    private static final String POST_METHOD = "POST";

    private static final String JSON_MIME_TYPE = "application/json";

    private final URL servletUrl;

    private final ForwardableCallback<DolphinRemotingException> remotingErrorHandler;

    private final Codec codec;

    private final CookieStore cookieStore;

    private final HttpURLConnectionFactory connectionFactory;

    private final HttpURLConnectionResponseHandler responseHandler;

    private String clientId;

    public DolphinPlatformHttpClientConnector(ClientConfiguration configuration, ClientDolphin clientDolphin, Codec codec, ForwardableCallback<DolphinRemotingException> remotingErrorHandler) {
        super(clientDolphin, new BlindCommandBatcher());
        Assert.requireNonNull(configuration, "configuration");
        setUiThreadHandler(configuration.getUiThreadHandler());
        this.servletUrl = configuration.getServerEndpoint();

        this.connectionFactory = configuration.getConnectionFactory();
        this.cookieStore = configuration.getCookieStore();
        this.responseHandler = configuration.getResponseHandler();

        this.codec = Assert.requireNonNull(codec, "codec");
        this.remotingErrorHandler = Assert.requireNonNull(remotingErrorHandler, "remotingErrorHandler");

    }

    public List<Command> transmit(List<Command> commands) {
        Assert.requireNonNull(commands, "commands");
        try {
            //REQUEST
            HttpURLConnection conn = connectionFactory.create(servletUrl);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty(CONTENT_TYPE_HEADER, JSON_MIME_TYPE);
            conn.setRequestProperty(ACCEPT_HEADER, JSON_MIME_TYPE);
            conn.setRequestMethod(POST_METHOD);
            conn.setRequestProperty(PlatformConstants.CLIENT_ID_HTTP_HEADER_NAME, clientId);
            setRequestCookies(conn);
            String content = codec.encode(commands);
            OutputStream w = conn.getOutputStream();
            w.write(content.getBytes(CHARSET));
            w.close();

            //RESPONSE
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpStatus.SC_REQUEST_TIMEOUT) {
                throw new DolphinSessionException("Server can not handle Dolphin Client ID");
            }
            if (responseCode >= HttpStatus.SC_MULTIPLE_CHOICES) {
                throw new DolphinHttpResponseException(responseCode, conn.getResponseMessage());
            }
            updateCookiesFromResponse(conn);
            updateClientId(conn);
            if (commands.size() == 1 && commands.get(0) == getReleaseCommand()) {
                return new ArrayList<>();
            } else {
                String receivedContent = new String(inputStreamToByte(conn.getInputStream()), CHARSET);
                return codec.decode(receivedContent);
            }
        } catch (Exception e) {
            DolphinRemotingException dolphinRemotingException = new DolphinRemotingException("Error in remoting layer", e);
            remotingErrorHandler.call(dolphinRemotingException);
            throw dolphinRemotingException;
        }
    }

    private void updateCookiesFromResponse(HttpURLConnection conn) throws URISyntaxException {
        Map<String, List<String>> headerFields = conn.getHeaderFields();
        List<String> cookiesHeader = headerFields.get(SET_COOKIE_HEADER);

        if (cookiesHeader != null) {
            for (String cookie : cookiesHeader) {
                List<HttpCookie> cookies = HttpCookie.parse(cookie);
                for(HttpCookie httpCookie : cookies) {
                    cookieStore.add(servletUrl.toURI(), httpCookie);
                }
            }
        }
    }

    private void setRequestCookies(HttpURLConnection conn) throws URISyntaxException {
        if (cookieStore.getCookies().size() > 0) {

            String cookieValue = "";
            for(HttpCookie cookie : cookieStore.get(servletUrl.toURI())) {
                cookieValue = cookieValue + cookie + ";";
            }
            if(!cookieValue.isEmpty()) {
                cookieValue = cookieValue.substring(0, cookieValue.length());
                conn.setRequestProperty(COOKIE_HEADER, cookieValue);
            }
        }
    }

    private void updateClientId(HttpURLConnection conn) {
        String clientIdInHeader = conn.getHeaderField(PlatformConstants.CLIENT_ID_HTTP_HEADER_NAME);
        if (this.clientId != null && !this.clientId.equals(clientIdInHeader)) {
            throw new DolphinRemotingException("Error: client id conflict!");
        }
        this.clientId = clientIdInHeader;
    }

    private byte[] inputStreamToByte(InputStream is) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int read = is.read();
        while (read != -1) {
            byteArrayOutputStream.write(read);
            read = is.read();
        }
        return byteArrayOutputStream.toByteArray();
    }
}



