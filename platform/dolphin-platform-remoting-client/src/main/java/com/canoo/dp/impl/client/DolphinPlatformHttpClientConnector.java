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
package com.canoo.dp.impl.client;

import com.canoo.dp.impl.client.legacy.ClientModelStore;
import com.canoo.dp.impl.client.legacy.communication.AbstractClientConnector;
import com.canoo.dp.impl.client.legacy.communication.BlindCommandBatcher;
import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.platform.core.http.HttpHeaderConstants;
import com.canoo.dp.impl.remoting.commands.DestroyContextCommand;
import com.canoo.dp.impl.remoting.legacy.communication.Codec;
import com.canoo.dp.impl.remoting.legacy.communication.Command;
import com.canoo.platform.client.ClientConfiguration;
import com.canoo.platform.core.http.HttpClient;
import com.canoo.platform.core.http.RequestMethod;
import com.canoo.platform.remoting.DolphinRemotingException;
import com.canoo.platform.remoting.client.RemotingExceptionHandler;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * This class is used to sync the unique client scope id of the current dolphin
 */
@API(since = "0.x", status = INTERNAL)
public class DolphinPlatformHttpClientConnector extends AbstractClientConnector {

    private static final Logger LOG = LoggerFactory.getLogger(DolphinPlatformHttpClientConnector.class);

    private final URI servletUrl;

    private final Codec codec;

    private final HttpClient client;

    private final AtomicBoolean disconnecting = new AtomicBoolean(false);

    public DolphinPlatformHttpClientConnector(final URI servletUrl, final ClientConfiguration configuration, final ClientModelStore clientModelStore, final Codec codec, final RemotingExceptionHandler onException, final HttpClient client) {
        super(clientModelStore, Assert.requireNonNull(configuration, "configuration").getUiExecutor(), new BlindCommandBatcher(), onException, configuration.getBackgroundExecutor());
        this.servletUrl = Assert.requireNonNull(servletUrl, "servletUrl");
        this.codec = Assert.requireNonNull(codec, "codec");
        this.client = Assert.requireNonNull(client, "client");
    }

    public List<Command> transmit(final List<Command> commands) throws DolphinRemotingException {
        Assert.requireNonNull(commands, "commands");

        //block if diconnect is called in other thread (poll / interruptLongPoll)
        for (Command command : commands) {
            if (command instanceof DestroyContextCommand) {
                disconnecting.set(true);
            }
        }

        try {
            final String data = codec.encode(commands);
            final String receivedContent = client.request(servletUrl, RequestMethod.POST).withContent(data, HttpHeaderConstants.JSON_MIME_TYPE).readString().execute().get().getContent();
            return codec.decode(receivedContent);
        } catch (final Exception e) {
            throw new DolphinRemotingException("Error in remoting layer", e);
        }
    }

    @Override
    public void connect() {
        disconnecting.set(false);
        super.connect();
    }

    @Override
    public void disconnect() {
        super.disconnect();
        disconnecting.set(false);
    }
}



