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
package com.canoo.dolphin.test.impl;

import com.canoo.dolphin.client.ControllerProxy;
import com.canoo.dolphin.client.Param;
import com.canoo.dolphin.test.ControllerTestException;
import com.canoo.dolphin.test.ControllerUnderTest;
import com.canoo.dolphin.util.Assert;

public class ClientTestFactory {

    public static <T> ControllerUnderTest<T> createController(final ClientContextForTests clientContext, final String controllerName) {
        Assert.requireNonNull(clientContext, "clientContext");
        Assert.requireNonBlank(controllerName, "controllerName");
        try {
            final ControllerProxy<T> proxy = (ControllerProxy<T>) clientContext.createController(controllerName).get();
            return new ControllerUnderTest<T>() {
                @Override
                public T getModel() {
                    return proxy.getModel();
                }

                @Override
                public void invoke(String actionName, Param... params) {
                    try {
                        proxy.invoke(actionName, params).get();
                    } catch (Exception e) {
                        throw new ControllerTestException("Error in action invocation", e);
                    }
                }

                @Override
                public void sync() {
                    clientContext.sync();
                }

                @Override
                public void destroy() {
                    try {
                        proxy.destroy().get();
                    } catch (Exception e) {
                        throw new ControllerTestException("Error in destroy", e);
                    }
                }
            };
        } catch (Exception e) {
            throw new ControllerTestException("Can't create controller proxy", e);
        }
    }
}
