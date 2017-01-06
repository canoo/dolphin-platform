/*
 * Copyright 2015-2016 Canoo Engineering AG.
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

import com.canoo.dolphin.client.util.AbstractDolphinBasedTest;
import com.canoo.implementation.dolphin.client.DolphinCommandHandler;
import org.opendolphin.core.client.ClientAttribute;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.comm.UiThreadHandler;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.testng.Assert.assertEquals;

public class TestDolphinCommandHandler extends AbstractDolphinBasedTest {

    @Test
    public void testInvocation() throws Exception {
        //Given:
        final DolphinTestConfiguration configuration = createDolphinTestConfiguration();
        final ServerDolphin serverDolphin = configuration.getServerDolphin();
        final ClientDolphin clientDolphin = configuration.getClientDolphin();
        clientDolphin.getClientConnector().setUiThreadHandler(new UiThreadHandler() {
            @Override
            public void executeInsideUiThread(Runnable runnable) {
                runnable.run();
            }
        });
        final DolphinCommandHandler dolphinCommandHandler = new DolphinCommandHandler(clientDolphin);
        final String modelId = UUID.randomUUID().toString();
        clientDolphin.presentationModel(modelId, new ClientAttribute("myAttribute", "UNKNOWN"));
        serverDolphin.register(new DolphinServerAction() {
            @Override
            public void registerIn(ActionRegistry registry) {
                registry.register("CHANGE_VALUE", new CommandHandler() {
                    @Override
                    public void handleCommand(Command command, List response) {
                        serverDolphin.getPresentationModel(modelId).getAttribute("myAttribute").setValue("Hello World");
                    }
                });
            }
        });

        //When:
        dolphinCommandHandler.invokeDolphinCommand("CHANGE_VALUE").get();

        //Then:
        assertEquals(clientDolphin.getPresentationModel(modelId).getAttribute("myAttribute").getValue(), "Hello World");
    }

}
