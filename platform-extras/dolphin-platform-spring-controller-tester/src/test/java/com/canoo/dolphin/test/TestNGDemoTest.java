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
package com.canoo.dolphin.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TestNGDemoTest extends SpringTestNGControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(TestNGDemoTest.class);


    private ControllerUnderTest<TestModel> controller;

    @BeforeMethod
    protected void init() {
        controller = createController("TestController");
    }


    @AfterMethod
    protected void destroy() {
        controller.destroy();
    }

    @Test
    public void testAddToList() {
        controller.invoke("addToList");
        assertEquals(controller.getModel().getItems().size(), 1);
        assertTrue(controller.getModel().getItems().contains("Hallo"));
    }

    @Test
    public void testAddBeanToList() {
        controller.invoke("addBeanToList");
        assertEquals(controller.getModel().getInternModels().size(), 1);
        assertEquals(controller.getModel().getInternModels().get(0).getValue(), "I'm a subbean");
    }

    @Test(enabled = false)
    public void testEventBus() {
        controller.invoke("sendEvent");
        controller.sync();
        assertEquals(controller.getModel().getValue(), "changed by eventBus!");
    }
}
