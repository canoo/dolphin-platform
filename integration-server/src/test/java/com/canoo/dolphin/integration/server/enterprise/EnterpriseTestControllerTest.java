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
package com.canoo.dolphin.integration.server.enterprise;

import com.canoo.dolphin.integration.enterprise.EnterpriseTestBean;
import com.canoo.dolphin.integration.server.TestConfiguration;
import com.canoo.dolphin.mapping.Property;
import com.canoo.dolphin.test.ControllerUnderTest;
import com.canoo.dolphin.test.SpringTestNGControllerTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.canoo.dolphin.integration.enterprise.EnterpriseTestConstants.ENTERPRISE_CONTROLLER_NAME;

@SpringApplicationConfiguration(classes = TestConfiguration.class)
public class EnterpriseTestControllerTest extends SpringTestNGControllerTest {

    private ControllerUnderTest<EnterpriseTestBean> controller;

    @BeforeMethod
    public void init() {
        controller = createController(ENTERPRISE_CONTROLLER_NAME);
    }

    public void destroy() {
        controller.destroy();
    }

    @Test
    public void testPostConstructCalled() {
        Assert.assertTrue(controller.getModel().getPostConstructCalled());
        destroy();
    }

    @Test
    public void testPreDestroyCalled() {
        Property<Boolean> preDestroyCalledProperty = controller.getModel().preDestroyCalledProperty();
        destroy();
        Assert.assertTrue(preDestroyCalledProperty.get());
    }

}
