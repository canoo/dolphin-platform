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
import com.canoo.dolphin.client.DummyUiThreadHandler;
import com.canoo.dolphin.client.HttpURLConnectionFactory;
import com.canoo.dolphin.util.DolphinRemotingException;
import com.canoo.remoting.client.ClientDolphin;
import com.canoo.communication.common.commands.Command;
import com.canoo.communication.common.commands.CreatePresentationModelCommand;
import com.canoo.communication.common.codec.JsonCodec;
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
import java.util.Collections;
import java.util.List;

public class TestDolphinPlatformHttpClientConnector {

    @Test
    public void testSimpleCall() {
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
                        String response = "[{\"pmId\":\"p1\",\"clientSideOnly\":false,\"id\":\"CreatePresentationModel\",\"attributes\":[],\"pmType\":null,\"className\":\"com.canoo.communication.common.commands.CreatePresentationModelCommand\"}]";
                        return new ByteArrayInputStream(response.getBytes("UTF-8"));
                    }
                };
            }
        });

        DolphinPlatformHttpClientConnector connector = new DolphinPlatformHttpClientConnector(clientConfiguration, new ClientDolphin(), new JsonCodec(), new ForwardableCallback<DolphinRemotingException>());

        CreatePresentationModelCommand command = new CreatePresentationModelCommand();
        command.setPmId("p1");
        Command rawCommand = command;
        List<Command> result = connector.transmit(Collections.singletonList(rawCommand));

        Assert.assertEquals(result.size(), 1);
        Assert.assertTrue(result.get(0) instanceof CreatePresentationModelCommand);
        Assert.assertEquals(((CreatePresentationModelCommand) result.get(0)).getPmId(), "p1");
    }

    @Test(expectedExceptions = DolphinRemotingException.class)
    public void testBadResponse() {

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


        DolphinPlatformHttpClientConnector connector = new DolphinPlatformHttpClientConnector(clientConfiguration, new ClientDolphin(), new JsonCodec(), new ForwardableCallback<DolphinRemotingException>());

        connector.transmit(Collections.singletonList(new Command()));
    }

    private URL getDummyURL() {
        try {
            return new URL("http://dummyURL");
        } catch (MalformedURLException e) {
            throw new RuntimeException("Exception occurred while creating URL", e);
        }
    }
}
