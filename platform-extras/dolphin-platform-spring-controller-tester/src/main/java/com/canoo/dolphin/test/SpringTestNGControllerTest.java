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

import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.test.impl.ClientTestFactory;
import com.canoo.dolphin.test.impl.DolphinPlatformSpringTestBootstrap;
import com.canoo.dolphin.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.testng.annotations.AfterMethod;

/**
 * Base class for TestNG based controller tests in Spring. This class can be extended to write custom controller tests.
 */
@WebAppConfiguration
@SpringApplicationConfiguration(classes = DolphinPlatformSpringTestBootstrap.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class SpringTestNGControllerTest extends AbstractTestNGSpringContextTests implements ControllerTest {

    @Autowired
    private ClientContext clientContext;

    @AfterMethod(alwaysRun = true)
    protected void disconnectClientContext() {
        try {
            clientContext.disconnect().get();
        } catch (Exception e) {
            throw new ControllerTestException("Can not disconnect client context!", e);
        }
    }

    public <T> ControllerUnderTest<T> createController(final String controllerName) {
        Assert.requireNonBlank(controllerName, "controllerName");
        try {
            return ClientTestFactory.createController(clientContext, controllerName);
        } catch (Exception e) {
            throw new ControllerTestException("Can't create controller proxy", e);
        }
    }
}

