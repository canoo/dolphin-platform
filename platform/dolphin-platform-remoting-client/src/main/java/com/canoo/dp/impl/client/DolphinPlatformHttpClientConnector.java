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
package com.canoo.dp.impl.client;

import com.canoo.dp.impl.client.legacy.ClientModelStore;
import com.canoo.dp.impl.client.legacy.communication.AbstractClientConnector;
import com.canoo.dp.impl.client.legacy.communication.BlindCommandBatcher;
import com.canoo.dp.impl.platform.client.HttpClientCookieHandler;
import com.canoo.dp.impl.platform.client.HttpStatus;
import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.remoting.codec.OptimizedJsonCodec;
import com.canoo.dp.impl.remoting.commands.DestroyContextCommand;
import com.canoo.dp.impl.remoting.legacy.commands.Command;
import com.canoo.platform.client.ClientSessionSupport;
import com.canoo.platform.client.HttpURLConnectionHandler;
import com.canoo.platform.core.functional.Function;
import com.canoo.platform.remoting.DolphinRemotingException;
import com.canoo.platform.remoting.client.ClientConfiguration;
import com.canoo.platform.remoting.client.DolphinSessionException;
import com.canoo.platform.remoting.client.RemotingExceptionHandler;
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

import static com.canoo.dp.impl.platform.core.PlatformConstants.ACCEPT_CHARSET_HEADER;
import static com.canoo.dp.impl.platform.core.PlatformConstants.ACCEPT_HEADER;
import static com.canoo.dp.impl.platform.core.PlatformConstants.CHARSET;
import static com.canoo.dp.impl.platform.core.PlatformConstants.CONTENT_TYPE_HEADER;
import static com.canoo.dp.impl.platform.core.PlatformConstants.JSON_MIME_TYPE;
import static com.canoo.dp.impl.platform.core.PlatformConstants.POST_METHOD;

/**
 * This class is used to sync the unique client scope id of the current dolphin
 */
public class DolphinPlatformHttpClientConnector extends AbstractClientConnector {

    private static final Logger LOG = LoggerFactory.getLogger(DolphinPlatformHttpClientConnector.class);

    private final URL servletUrl;

    private final HttpURLConnectionHandler responseHandler;

    private final HttpClientCookieHandler httpClientCookieHandler;

    private final AtomicReference<String> clientId = new AtomicReference<>();

    private final ClientSessionSupport clientSessionSupport;

    public DolphinPlatformHttpClientConnector(final ClientConfiguration configuration, final ClientModelStore clientModelStore, final RemotingExceptionHandler onException) {
        super(clientModelStore, Assert.requireNonNull(configuration, "configuration").getUiExecutor(), new BlindCommandBatcher(), onException, configuration.getBackgroundExecutor());
        this.servletUrl = configuration.getServerEndpoint();
        this.httpClientCookieHandler = new HttpClientCookieHandler(configuration.getCookieStore());
        this.responseHandler = configuration.getResponseHandler();
        this.clientSessionSupport = new ClientSessionSupport(configuration.getConnectionFactory());
    }

    private final AtomicBoolean disconnecting = new AtomicBoolean(false);

    public List<Command> transmit(final List<Command> commands) throws DolphinRemotingException {
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
            List<Command> responseCommands = clientSessionSupport.doRequest(servletUrl, new Function<HttpURLConnection, List<Command>>() {
                @Override
                public List<Command> call(HttpURLConnection conn) {
                    try {
                        //REQUEST
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        conn.setRequestProperty(ACCEPT_CHARSET_HEADER, CHARSET);
                        conn.setRequestProperty(CONTENT_TYPE_HEADER, JSON_MIME_TYPE);
                        conn.setRequestProperty(ACCEPT_HEADER, JSON_MIME_TYPE);
                        conn.setRequestMethod(POST_METHOD);
                        httpClientCookieHandler.setRequestCookies(conn);
                        String content = OptimizedJsonCodec.getInstance().encode(commands);
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

                        responseHandler.handle(conn);
                        httpClientCookieHandler.updateCookiesFromResponse(conn);
                        if (commands.size() == 1 && commands.get(0) == getReleaseCommand()) {
                            return new ArrayList<>();
                        } else {
                            String receivedContent = new String(inputStreamToByte(conn.getInputStream()), CHARSET);
                            return OptimizedJsonCodec.getInstance().decode(receivedContent);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("Error in communication", e);
                    }
                }
            });
            this.clientId.set(clientSessionSupport.getClientIdFor(servletUrl));
            return responseCommands;
        } catch (Exception e) {
            throw new DolphinRemotingException("Error in remoting layer", e);
        }
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

    @Override
    public String getClientId() {
        return clientId.get();
    }
}



