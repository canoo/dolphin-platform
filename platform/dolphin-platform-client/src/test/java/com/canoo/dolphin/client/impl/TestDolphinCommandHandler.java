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

import com.canoo.dolphin.client.util.AbstractDolphinBasedTest;
import org.opendolphin.core.client.ClientAttribute;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.testng.Assert.assertEquals;

public class TestDolphinCommandHandler extends AbstractDolphinBasedTest {

    private final class TestChangeCommand extends Command {}

    @Test
    public void testInvocation() throws Exception {
        //Given:
        final DolphinTestConfiguration configuration = createDolphinTestConfiguration();
        final ServerDolphin serverDolphin = configuration.getServerDolphin();
        final ClientDolphin clientDolphin = configuration.getClientDolphin();
        final DolphinCommandHandler dolphinCommandHandler = new DolphinCommandHandler(clientDolphin.getClientConnector());
        final String modelId = UUID.randomUUID().toString();

        ClientPresentationModel model = new ClientPresentationModel(modelId, Arrays.asList(new ClientAttribute("myAttribute", "UNKNOWN")));
        clientDolphin.getModelStore().add(model);

        serverDolphin.getServerConnector().register(new DolphinServerAction() {
            @Override
            public void registerIn(ActionRegistry registry) {
                registry.register(TestChangeCommand.class, new CommandHandler() {
                    @Override
                    public void handleCommand(Command command) {
                        serverDolphin.getModelStore().findPresentationModelById(modelId).getAttribute("myAttribute").setValue("Hello World");
                    }
                });
            }
        });

        //When:
        dolphinCommandHandler.invokeDolphinCommand(new TestChangeCommand()).get();

        //Then:
        assertEquals(clientDolphin.getModelStore().findPresentationModelById(modelId).getAttribute("myAttribute").getValue(), "Hello World");
    }

}
