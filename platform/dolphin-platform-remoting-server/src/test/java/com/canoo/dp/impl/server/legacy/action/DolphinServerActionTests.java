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
package com.canoo.dp.impl.server.legacy.action;

import com.canoo.dp.impl.remoting.legacy.commands.CreatePresentationModelCommand;
import com.canoo.dp.impl.remoting.legacy.commands.ValueChangedCommand;
import com.canoo.dp.impl.server.legacy.ServerAttribute;
import com.canoo.dp.impl.server.legacy.communication.ActionRegistry;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;

public class DolphinServerActionTests  {

    @BeforeMethod
    protected void setUp() throws Exception {
        action = new DolphinServerAction() {
            @Override
            public void registerIn(ActionRegistry registry) {

            }

        };
        action.setDolphinResponse(new ArrayList());
    }

    @Test
    public void testCreatePresentationModel() {
        action.presentationModel("p1", "person", Collections.<ServerAttribute>emptyList());
        Assert.assertEquals(1, action.getDolphinResponse().size());
        Assert.assertEquals(CreatePresentationModelCommand.class, action.getDolphinResponse().get(0).getClass());
        Assert.assertEquals("p1", ((CreatePresentationModelCommand) action.getDolphinResponse().get(0)).getPmId());
        Assert.assertEquals("person", ((CreatePresentationModelCommand) action.getDolphinResponse().get(0)).getPmType());
    }

    public void testChangeValue() {
        action.changeValue(new ServerAttribute("attr", "initial"), "newValue");
        Assert.assertEquals(1, action.getDolphinResponse().size());
        Assert.assertEquals(ValueChangedCommand.class, action.getDolphinResponse().get(0).getClass());
    }

    private DolphinServerAction action;
}
