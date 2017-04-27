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
import com.canoo.dolphin.impl.commands.DestroyContextCommand;
import com.canoo.dolphin.util.Assert;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.comm.AbstractClientConnector;
import org.opendolphin.core.client.comm.BlindCommandBatcher;
import org.opendolphin.core.client.comm.RemotingExceptionHandler;
import org.opendolphin.core.comm.Codec;
import org.opendolphin.core.comm.Command;
import org.opendolphin.util.DolphinRemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class is used to sync the unique client scope id of the current dolphin
 */
public class DolphinPlatformHttpClientConnector extends AbstractClientConnector {

    private static final Logger LOG = LoggerFactory.getLogger(DolphinPlatformHttpClientConnector.class);

    private final URL servletUrl;

    private final Codec codec;

    private final HttpURLConnectionFactory connectionFactory;

    private final HttpURLConnectionResponseHandler responseHandler;

    private final HttpClientCookieHandler httpClientCookieHandler;

    private AtomicReference<String> clientId = new AtomicReference<>();

    public DolphinPlatformHttpClientConnector(ClientConfiguration configuration, ClientModelStore clientModelStore, Codec codec, RemotingExceptionHandler onException) {
        super(clientModelStore, Assert.requireNonNull(configuration, "configuration").getUiExecutor(), new BlindCommandBatcher(), onException, configuration.getBackgroundExecutor());
        this.servletUrl = configuration.getServerEndpoint();
        this.connectionFactory = configuration.getConnectionFactory();
        this.httpClientCookieHandler = new HttpClientCookieHandler(configuration.getCookieStore());
        this.responseHandler = configuration.getResponseHandler();
        this.codec = Assert.requireNonNull(codec, "codec");
    }

    private final AtomicBoolean disconnecting = new AtomicBoolean(false);

    public List<Command> transmit(List<Command> commands) throws DolphinRemotingException {
        Assert.requireNonNull(commands, "commands");

        if (disconnecting.get()) {
            LOG.warn("Canceled communication based on disconnect");
            return Collections.emptyList();
        }

        //block if diconnect is called in other thread (poll / release)
        for (Command command : commands) {
            if (command instanceof DestroyContextCommand) {
                disconnecting.set(true);
            }
        }


        try {
            //REQUEST
            final HttpURLConnection conn = connectionFactory.create(servletUrl);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty(PlatformConstants.CONTENT_TYPE_HEADER, PlatformConstants.JSON_MIME_TYPE);
            conn.setRequestProperty(PlatformConstants.ACCEPT_HEADER, PlatformConstants.JSON_MIME_TYPE);
            conn.setRequestMethod(PlatformConstants.POST_METHOD);
            if (clientId.get() != null) {
                conn.setRequestProperty(PlatformConstants.CLIENT_ID_HTTP_HEADER_NAME, clientId.get());
            } else {
                LOG.debug("Sending first request to server. Dolphin client id not defined.");
            }
            httpClientCookieHandler.setRequestCookies(conn);
            String content = codec.encode(commands);
            OutputStream w = conn.getOutputStream();
            w.write(content.getBytes(PlatformConstants.CHARSET));
            w.close();

            //RESPONSE
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpStatus.SC_REQUEST_TIMEOUT) {
                throw new DolphinSessionException("Server can not handle Dolphin Client ID");
            }
            if (responseCode >= HttpStatus.SC_MULTIPLE_CHOICES) {
                throw new DolphinHttpResponseException(responseCode, conn.getResponseMessage());
            }

            responseHandler.handle(conn);

            httpClientCookieHandler.updateCookiesFromResponse(conn);
            updateClientId(conn);
            if (commands.size() == 1 && commands.get(0) == getReleaseCommand()) {
                return new ArrayList<>();
            } else {
                String receivedContent = new String(inputStreamToByte(conn.getInputStream()), PlatformConstants.CHARSET);
                return codec.decode(receivedContent);
            }
        } catch (Exception e) {
            throw new DolphinRemotingException("Error in remoting layer", e);
        }
    }

    private void updateClientId(HttpURLConnection conn) {
        String clientIdInHeader = conn.getHeaderField(PlatformConstants.CLIENT_ID_HTTP_HEADER_NAME);
        if (this.clientId.get() != null && !this.clientId.get().equals(clientIdInHeader)) {
            throw new IllegalStateException("Error: client id conflict!");
        }
        this.clientId.set(clientIdInHeader);
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

    @Override
    public void connect() {
        clientId.set(null);
        disconnecting.set(false);
        super.connect();
    }

    @Override
    public void disconnect() {
        super.disconnect();
        clientId.set(null);
        disconnecting.set(false);
    }
}



