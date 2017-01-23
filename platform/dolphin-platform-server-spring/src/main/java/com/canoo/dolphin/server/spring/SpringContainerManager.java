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
package com.canoo.dolphin.server.spring;

import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.common.Assert;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

/**
 * Spring specific implementation of the {@link ContainerManager} interface
 *
 * @author Hendrik Ebbers
 */
public class SpringContainerManager extends AbstractSpringContainerManager {

    private ServletContext servletContext;

    @Override
    public void init(ServletContext servletContext) {
        this.servletContext = Assert.requireNonNull(servletContext, "servletContext");
        init();
    }

    /**
     * Returns the Spring {@link org.springframework.context.ApplicationContext} for the current {@link javax.servlet.ServletContext}
     *
     * @return the spring context
     */
    protected ApplicationContext getContext() {
        return WebApplicationContextUtils.getWebApplicationContext(servletContext);
    }
}
