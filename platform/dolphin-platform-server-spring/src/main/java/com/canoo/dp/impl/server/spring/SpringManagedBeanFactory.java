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
package com.canoo.dp.impl.server.spring;

import com.canoo.dp.impl.server.beans.ManagedBeanFactory;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletContext;

/**
 * Spring specific implementation of the {@link ManagedBeanFactory} interface
 *
 * @author Hendrik Ebbers
 */
public class SpringManagedBeanFactory extends AbstractSpringManagedBeanFactory {

    @Override
    public void init(ServletContext servletContext) {
        init();

    }

    /**
     * Returns the Spring {@link org.springframework.context.ApplicationContext} for the current {@link javax.servlet.ServletContext}
     *
     * @return the spring context
     */
    protected ApplicationContext getContext() {
        return DolphinPlatformSpringBootstrap.getApplicationContext();
    }
}
