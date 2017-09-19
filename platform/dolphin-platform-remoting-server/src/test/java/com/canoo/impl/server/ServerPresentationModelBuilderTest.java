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
package com.canoo.impl.server;

import com.canoo.dp.impl.server.model.ServerPresentationModelBuilder;
import com.canoo.impl.server.util.AbstractDolphinBasedTest;
import com.canoo.dp.impl.remoting.legacy.RemotingConstants;
import com.canoo.dp.impl.server.legacy.ServerDolphin;
import com.canoo.dp.impl.server.legacy.ServerPresentationModel;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ServerPresentationModelBuilderTest extends AbstractDolphinBasedTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullArgument() {
        new ServerPresentationModelBuilder(null);
    }

    @Test
    public void testSimpleCreation() {
        ServerDolphin serverDolphin = createServerDolphin();
        ServerPresentationModelBuilder builder = new ServerPresentationModelBuilder(serverDolphin);

        ServerPresentationModel model = builder.create();
        assertNotNull(model);
        assertEquals(model.getAttributes().size(), 1);
        assertEquals(model.getAttributes().get(0).getPropertyName(), RemotingConstants.SOURCE_SYSTEM);
        assertEquals(model.getAttributes().get(0).getValue(), RemotingConstants.SOURCE_SYSTEM_SERVER);
    }

    @Test
    public void testWithAttributeCreation() {
        ServerDolphin serverDolphin = createServerDolphin();
        ServerPresentationModelBuilder builder = new ServerPresentationModelBuilder(serverDolphin);
        ServerPresentationModel model = builder.withAttribute("testName", null).create();
        assertNotNull(model);
        assertEquals(model.getAttributes().size(), 2);
        assertNotNull(model.getAttribute(RemotingConstants.SOURCE_SYSTEM));
        assertNotNull(model.getAttribute("testName"));
    }

    @Test
    public void testWithFilledAttributeCreation() {
        ServerDolphin serverDolphin = createServerDolphin();
        ServerPresentationModelBuilder builder = new ServerPresentationModelBuilder(serverDolphin);
        ServerPresentationModel model = builder.withAttribute("testName", "testValue").create();
        assertNotNull(model);
        assertEquals(model.getAttributes().size(), 2);
        assertNotNull(model.getAttribute(RemotingConstants.SOURCE_SYSTEM));
        assertNotNull(model.getAttribute("testName"));
        assertEquals(model.getAttribute("testName").getValue(), "testValue");
    }

    @Test
    public void testWithIdCreation() {
        ServerDolphin serverDolphin = createServerDolphin();
        ServerPresentationModelBuilder builder = new ServerPresentationModelBuilder(serverDolphin);
        ServerPresentationModel model = builder.withId("testId").create();
        assertNotNull(model);
        assertEquals(model.getId(), "testId");
    }

    @Test
    public void testWithTypeCreation() {
        ServerDolphin serverDolphin = createServerDolphin();
        ServerPresentationModelBuilder builder = new ServerPresentationModelBuilder(serverDolphin);
        ServerPresentationModel model = builder.withType("testType").create();
        assertNotNull(model);
        assertEquals(model.getPresentationModelType(), "testType");
    }
}
