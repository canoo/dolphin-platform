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
package com.canoo.dolphin.test.impl;

import com.canoo.remoting.client.ClientDolphin;
import com.canoo.remoting.client.ClientModelStore;
import com.canoo.remoting.server.DefaultServerDolphin;
import com.canoo.remoting.server.ServerDolphinFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestInMemoryConfiguration {

    final ClientDolphin clientDolphin = new ClientDolphin();

    final DefaultServerDolphin serverDolphin = (DefaultServerDolphin) ServerDolphinFactory.create();

    private final ExecutorService clientExecutor = Executors.newSingleThreadExecutor();

    public TestInMemoryConfiguration() {
        clientDolphin.setClientModelStore(new ClientModelStore(clientDolphin));
    }

    public ExecutorService getClientExecutor() {
        return clientExecutor;
    }

    public ClientDolphin getClientDolphin() {
        return clientDolphin;
    }

    public DefaultServerDolphin getServerDolphin() {
        return serverDolphin;
    }
}
