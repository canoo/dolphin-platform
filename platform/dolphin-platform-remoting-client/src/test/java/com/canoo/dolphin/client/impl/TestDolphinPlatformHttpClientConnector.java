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

import com.canoo.dolphin.client.DummyUiThreadHandler;
import com.canoo.dp.impl.client.DolphinPlatformHttpClientConnector;
import com.canoo.dp.impl.client.legacy.ClientDolphin;
import com.canoo.dp.impl.client.legacy.ClientModelStore;
import com.canoo.dp.impl.client.legacy.ModelSynchronizer;
import com.canoo.dp.impl.client.legacy.communication.AbstractClientConnector;
import com.canoo.dp.impl.client.legacy.communication.SimpleExceptionHandler;
import com.canoo.dp.impl.platform.core.PlatformConstants;
import com.canoo.dp.impl.remoting.commands.CreateContextCommand;
import com.canoo.dp.impl.remoting.legacy.commands.Command;
import com.canoo.dp.impl.remoting.legacy.commands.CreatePresentationModelCommand;
import com.canoo.platform.client.HttpURLConnectionFactory;
import com.canoo.platform.core.functional.Provider;
import com.canoo.platform.remoting.DolphinRemotingException;
import com.canoo.platform.remoting.client.ClientConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.canoo.dp.impl.remoting.legacy.commands.CommandConstants.CREATE_PRESENTATION_MODEL_COMMAND_ID;
import static com.canoo.dp.impl.remoting.legacy.commands.CommandConstants.ID;
import static com.canoo.dp.impl.remoting.legacy.commands.CommandConstants.PM_ATTRIBUTES;
import static com.canoo.dp.impl.remoting.legacy.commands.CommandConstants.PM_ID;
import static com.canoo.dp.impl.remoting.legacy.commands.CommandConstants.PM_TYPE;

public class TestDolphinPlatformHttpClientConnector {

    @Test
    public void testSimpleCall() throws DolphinRemotingException {
        ClientConfiguration clientConfiguration = new ClientConfiguration(getDummyURL(), new DummyUiThreadHandler());
        clientConfiguration.setConnectionFactory(new HttpURLConnectionFactory() {
            @Override
            public HttpURLConnection create(URL url) throws IOException {
                return new HttpURLConnection(url) {
                    @Override
                    public void disconnect() {

                    }

                    @Override
                    public boolean usingProxy() {
                        return false;
                    }

                    @Override
                    public void connect() throws IOException {

                    }

                    @Override
                    public OutputStream getOutputStream() throws IOException {
                        return new ByteArrayOutputStream();
                    }

                    @Override
                    public InputStream getInputStream() throws IOException {
                        String response = "[{" + PM_ID + ":\"p1\"," + ID + ":" + CREATE_PRESENTATION_MODEL_COMMAND_ID + "," + PM_ATTRIBUTES + ":[]," + PM_TYPE + ":null}]";
                        return new ByteArrayInputStream(response.getBytes("UTF-8"));
                    }

                    @Override
                    public String getHeaderField(String name) {
                        if(PlatformConstants.CLIENT_ID_HTTP_HEADER_NAME.equals(name)) {
                            return "TEST-ID";
                        }
                        return super.getHeaderField(name);
                    }
                };
            }
        });

        ClientDolphin clientDolphin = new ClientDolphin();
        clientDolphin.setClientModelStore(new ClientModelStore(new ModelSynchronizer(new Provider<AbstractClientConnector>() {
            @Override
            public AbstractClientConnector get() {
                return null;
            }
        })));
        DolphinPlatformHttpClientConnector connector = new DolphinPlatformHttpClientConnector(clientConfiguration, clientDolphin.getModelStore(), new SimpleExceptionHandler());

        CreatePresentationModelCommand command = new CreatePresentationModelCommand();
        command.setPmId("p1");
        Command rawCommand = command;
        List<Command> result = connector.transmit(Collections.singletonList(rawCommand));

        Assert.assertEquals(result.size(), 1);
        Assert.assertTrue(result.get(0) instanceof CreatePresentationModelCommand);
        Assert.assertEquals(((CreatePresentationModelCommand) result.get(0)).getPmId(), "p1");
    }

    @Test(expectedExceptions = DolphinRemotingException.class)
    public void testBadResponse() throws DolphinRemotingException {

        ClientConfiguration clientConfiguration = new ClientConfiguration(getDummyURL(), new DummyUiThreadHandler());
        clientConfiguration.setConnectionFactory(new HttpURLConnectionFactory() {
            @Override
            public HttpURLConnection create(URL url) throws IOException {
                return new HttpURLConnection(url) {
                    @Override
                    public void disconnect() {

                    }

                    @Override
                    public boolean usingProxy() {
                        return false;
                    }

                    @Override
                    public void connect() throws IOException {

                    }

                    @Override
                    public OutputStream getOutputStream() throws IOException {
                        return new ByteArrayOutputStream();
                    }

                };
            }
        });

        ClientDolphin clientDolphin = new ClientDolphin();
        clientDolphin.setClientModelStore(new ClientModelStore(new ModelSynchronizer(new Provider<AbstractClientConnector>() {
            @Override
            public AbstractClientConnector get() {
                return null;
            }
        })));
        DolphinPlatformHttpClientConnector connector = new DolphinPlatformHttpClientConnector(clientConfiguration, clientDolphin.getModelStore(), new SimpleExceptionHandler());

        List<Command> commands = new ArrayList<>();
        commands.add(new CreateContextCommand());
        connector.transmit(commands);
    }

    private URL getDummyURL() {
        try {
            return new URL("http://dummyURL");
        } catch (MalformedURLException e) {
            throw new RuntimeException("Exception occurred while creating URL", e);
        }
    }
}
