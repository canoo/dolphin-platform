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
package com.canoo.dolphin.integration;

import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.ControllerProxy;
import com.canoo.dolphin.todo.pm.ToDoList;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static com.canoo.dolphin.todo.TodoAppConstants.ADD_ACTION;
import static com.canoo.dolphin.todo.TodoAppConstants.CONTROLLER_NAME;

public class TodoAppTest extends AbstractIntegrationTest {

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Tests if the client API can create a conection to the server")
    public void testConnection(String containerType, String endpoint) {
        try {
            ClientContext context = createClientContext(endpoint);
            Assert.assertNotNull(context);
        } catch (Exception e) {
            Assert.fail("Can not create connection for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test if controller and model can be created")
    public void testCreateController(String containerType, String endpoint) {
        try {
            ClientContext context = createClientContext(endpoint);
            ControllerProxy<ToDoList> controller = createController(context, CONTROLLER_NAME);
            Assert.assertNotNull(controller);
            Assert.assertNotNull(controller.getModel());
            Assert.assertEquals(controller.getModel().getClass(), ToDoList.class);
            Assert.assertNotNull(controller.getModel().getItems());
            Assert.assertNotNull(controller.getModel().getNewItemText());
            destroy(controller, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Tests if an item can be added to the todo list")
    public void testAddItem(String containerType, String endpoint) {
        try {
            ClientContext context = createClientContext(endpoint);
            ControllerProxy<ToDoList> controller = createController(context, CONTROLLER_NAME);
            controller.getModel().getNewItemText().set("Milk");
            invoke(controller, ADD_ACTION, endpoint);

            Assert.assertEquals(controller.getModel().getNewItemText().get(), "");

            Assert.assertFalse(controller.getModel().getItems().isEmpty());
            Assert.assertEquals(controller.getModel().getItems().size(), 1);
            Assert.assertEquals(controller.getModel().getItems().get(0).getText(), "Milk");
            Assert.assertFalse(controller.getModel().getItems().get(0).isCompleted());
            destroy(controller, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not add item for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Tests if an added item will be synchronized on multiple clients")
    public void testClientSync(String containerType, String endpoint) {
        try {
            ClientContext context1 = createClientContext(endpoint);
            ControllerProxy<ToDoList> controller1 = createController(context1, CONTROLLER_NAME);

            ClientContext context2 = createClientContext(endpoint);
            ControllerProxy<ToDoList> controller2 = createController(context2, CONTROLLER_NAME);

            controller1.getModel().getNewItemText().set("Banana");
            invoke(controller1, ADD_ACTION, endpoint);

            sleep(1_000, TimeUnit.MILLISECONDS);

            Assert.assertFalse(controller2.getModel().getItems().isEmpty());
            Assert.assertEquals(controller2.getModel().getItems().size(), 1);
            Assert.assertEquals(controller2.getModel().getItems().get(0).getText(), "Banana");
            Assert.assertFalse(controller2.getModel().getItems().get(0).isCompleted());
            destroy(controller1, endpoint);
            destroy(controller2, endpoint);
        } catch (Exception e) {
            Assert.fail("Added item not synchronized for " + containerType, e);
        }
    }
}
