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
package com.canoo.implementation.dolphin.test.spring;

import com.canoo.implementation.dolphin.server.spring.AbstractSpringContainerManager;
import com.canoo.implementation.dolphin.util.Assert;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletContext;

public class TestSpringContainerManager extends AbstractSpringContainerManager {

    private final ApplicationContext webApplicationContext;

    public TestSpringContainerManager(ApplicationContext webApplicationContext) {
        this.webApplicationContext = Assert.requireNonNull(webApplicationContext, "webApplicationContext");
    }

    @Override
    public void init(ServletContext servletContext) {
        init();
    }

    @Override
    protected ApplicationContext getContext() {
        return webApplicationContext;
    }


}
