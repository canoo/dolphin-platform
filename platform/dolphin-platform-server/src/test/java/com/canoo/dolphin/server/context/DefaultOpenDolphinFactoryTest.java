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
package com.canoo.dolphin.server.context;

import com.canoo.dolphin.impl.codec.OptimizedJsonCodec;
import com.canoo.remoting.server.DefaultServerDolphin;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class DefaultOpenDolphinFactoryTest {

    @Test
    public void testDolphinCreation() {
        OpenDolphinFactory factory = new DefaultOpenDolphinFactory();
        DefaultServerDolphin serverDolphin = factory.create();
        assertNotNull(serverDolphin);
        assertNotNull(serverDolphin.getModelStore());
        assertNotNull(serverDolphin.getServerConnector());
        assertNotNull(serverDolphin.getServerModelStore());
        assertNotNull(serverDolphin.getServerConnector().getCodec());
        assertEquals(OptimizedJsonCodec.class, serverDolphin.getServerConnector().getCodec().getClass());

        assertEquals(serverDolphin.getServerConnector().getRegistry().getActions().size(), 6);
        assertTrue(serverDolphin.getServerConnector().getRegistry().getActions().containsKey("Empty"));
        assertTrue(serverDolphin.getServerConnector().getRegistry().getActions().containsKey("ValueChanged"));
        assertTrue(serverDolphin.getServerConnector().getRegistry().getActions().containsKey("CreatePresentationModel"));
        assertTrue(serverDolphin.getServerConnector().getRegistry().getActions().containsKey("AttributeCreated"));
        assertTrue(serverDolphin.getServerConnector().getRegistry().getActions().containsKey("ChangeAttributeMetadata"));
        assertTrue(serverDolphin.getServerConnector().getRegistry().getActions().containsKey("DeletedPresentationModel"));

        assertEquals(serverDolphin.listPresentationModelIds().size(), 0);
    }
}
