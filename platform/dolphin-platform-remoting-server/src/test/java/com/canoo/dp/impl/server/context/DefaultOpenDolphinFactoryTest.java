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
package com.canoo.dp.impl.server.context;

import com.canoo.dp.impl.remoting.legacy.commands.ChangeAttributeMetadataCommand;
import com.canoo.dp.impl.remoting.legacy.commands.CreatePresentationModelCommand;
import com.canoo.dp.impl.remoting.legacy.commands.ValueChangedCommand;
import com.canoo.dp.impl.server.legacy.DefaultServerDolphin;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class DefaultOpenDolphinFactoryTest {

    @Test
    public void testDolphinCreation() {
        OpenDolphinFactory factory = new OpenDolphinFactory();
        DefaultServerDolphin serverDolphin = factory.create();
        assertNotNull(serverDolphin);
        assertNotNull(serverDolphin.getModelStore());
        assertNotNull(serverDolphin.getServerConnector());
        assertNotNull(serverDolphin.getModelStore());

        assertEquals(serverDolphin.getServerConnector().getRegistry().getActions().size(), 3);
        assertTrue(serverDolphin.getServerConnector().getRegistry().getActions().containsKey(ValueChangedCommand.class));
        assertTrue(serverDolphin.getServerConnector().getRegistry().getActions().containsKey(CreatePresentationModelCommand.class));
        assertTrue(serverDolphin.getServerConnector().getRegistry().getActions().containsKey(ChangeAttributeMetadataCommand.class));

        assertEquals(serverDolphin.getModelStore().listPresentationModelIds().size(), 0);
    }
}
