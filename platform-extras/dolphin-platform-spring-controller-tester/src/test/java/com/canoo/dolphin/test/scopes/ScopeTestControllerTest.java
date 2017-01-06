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
package com.canoo.dolphin.test.scopes;

import com.canoo.dolphin.test.spring.ControllerUnderTest;
import com.canoo.dolphin.test.spring.SpringTestNGControllerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

@SpringApplicationConfiguration(classes = ScopesConfig.class)
public class ScopeTestControllerTest extends SpringTestNGControllerTest {

    @Autowired
    private RequestService requestService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SingletonService singletonService;

    @Test
    public void testAllScopes() {
        ControllerUnderTest<ScopeModel> controller = createController("ScopeTestController");

        Assert.assertTrue(requestService.getId() != null);
        Assert.assertTrue(controller.getModel().requestServiceIdProperty().get() != null);
        Assert.assertTrue(requestService.getId().equals(controller.getModel().requestServiceIdProperty().get()));

        Assert.assertTrue(clientService.getId() != null);
        Assert.assertTrue(controller.getModel().clientServiceIdProperty().get() != null);
        Assert.assertTrue(clientService.getId().equals(controller.getModel().clientServiceIdProperty().get()));

        Assert.assertTrue(sessionService.getId() != null);
        Assert.assertTrue(controller.getModel().sessionServiceIdProperty().get() != null);
        Assert.assertTrue(sessionService.getId().equals(controller.getModel().sessionServiceIdProperty().get()));

        Assert.assertTrue(singletonService.getId() != null);
        Assert.assertTrue(controller.getModel().singletonServiceIdProperty().get() != null);
        Assert.assertTrue(singletonService.getId().equals(controller.getModel().singletonServiceIdProperty().get()));

        controller.destroy();
    }
}
