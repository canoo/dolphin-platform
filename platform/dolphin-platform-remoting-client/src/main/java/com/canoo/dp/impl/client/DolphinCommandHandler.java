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

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.client.legacy.communication.AbstractClientConnector;
import com.canoo.dp.impl.client.legacy.communication.OnFinishedHandler;
import com.canoo.dp.impl.remoting.legacy.commands.Command;

import java.util.concurrent.CompletableFuture;

public class DolphinCommandHandler {

    private final AbstractClientConnector clientConnector;

    public DolphinCommandHandler(final AbstractClientConnector clientConnector) {
        this.clientConnector = Assert.requireNonNull(clientConnector, "clientDolphin");
    }

    public CompletableFuture<Void> invokeDolphinCommand(final Command command) {
        Assert.requireNonNull(command, "command");
        final CompletableFuture<Void> result = new CompletableFuture<>();
        clientConnector.send(command, new OnFinishedHandler() {
            @Override
            public void onFinished() {
                result.complete(null);
            }
        });
        return result;
    }
}
