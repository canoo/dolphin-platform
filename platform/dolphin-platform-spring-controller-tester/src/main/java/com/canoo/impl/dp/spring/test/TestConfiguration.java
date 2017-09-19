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
package com.canoo.impl.dp.spring.test;

import com.canoo.dp.impl.client.ClientContextImpl;
import com.canoo.dp.impl.client.legacy.ClientModelStore;
import com.canoo.dp.impl.client.legacy.communication.AbstractClientConnector;
import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.remoting.legacy.commands.Command;
import com.canoo.dp.impl.server.client.ClientSessionProvider;
import com.canoo.dp.impl.server.config.ConfigurationFileLoader;
import com.canoo.dp.impl.server.config.RemotingConfiguration;
import com.canoo.dp.impl.server.context.DolphinContext;
import com.canoo.dp.impl.server.controller.ControllerRepository;
import com.canoo.dp.impl.server.controller.ControllerValidationException;
import com.canoo.dp.impl.server.scanner.DefaultClasspathScanner;
import com.canoo.platform.core.functional.Function;
import com.canoo.platform.remoting.client.ClientConfiguration;
import com.canoo.platform.remoting.client.ClientContext;
import com.canoo.platform.server.client.ClientSession;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpSession;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestConfiguration {

    private final DolphinTestContext dolphinTestContext;

    private final ClientContextImpl clientContext;

    public TestConfiguration(final WebApplicationContext context, final HttpSession httpSession) throws ControllerValidationException, MalformedURLException, ExecutionException, InterruptedException {
        Assert.requireNonNull(context, "context");
        Assert.requireNonNull(httpSession, "httpSession");

        //Client
        final ExecutorService clientExecutor = Executors.newSingleThreadExecutor();
        final ClientConfiguration clientConfiguration = new ClientConfiguration(new URL("http://dummyURL"), clientExecutor);
        clientConfiguration.setConnectionTimeout(Long.MAX_VALUE);

        clientContext = new ClientContextImpl(clientConfiguration, new Function<ClientModelStore, AbstractClientConnector>() {
            @Override
            public AbstractClientConnector call(ClientModelStore clientModelStore) {
                return new DolphinTestClientConnector(clientModelStore, clientExecutor, new Function<List<Command>, List<Command>>(){

                    @Override
                    public List<Command> call(List<Command> commands) {
                        return sendToServer(commands);
                    }
                });
            }
        });


        //Server
        final ControllerRepository controllerRepository = new ControllerRepository(new DefaultClasspathScanner());
        final TestSpringManagedBeanFactory containerManager = new TestSpringManagedBeanFactory(context);
        containerManager.init(context.getServletContext());
        final DolphinContextProviderMock dolphinContextProviderMock = new DolphinContextProviderMock();
        dolphinTestContext = new DolphinTestContext(new RemotingConfiguration(ConfigurationFileLoader.loadConfiguration()), dolphinContextProviderMock, containerManager, controllerRepository, httpSession);
        dolphinContextProviderMock.setCurrentContext(dolphinTestContext);
    }

    private List<Command> sendToServer(final List<Command> commandList) {
        return dolphinTestContext.handle(commandList);
    }

    public DolphinTestContext getDolphinTestContext() {
        return dolphinTestContext;
    }

    private class DolphinContextProviderMock implements ClientSessionProvider {

        DolphinContext currentContext;

        public void setCurrentContext(DolphinContext currentContext) {
            this.currentContext = currentContext;
        }

        @Override
        public ClientSession getCurrentClientSession() {
            return currentContext.getDolphinSession();
        }
    }

    public ClientContext getClientContext() {
        return clientContext;
    }
}
