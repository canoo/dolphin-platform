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
package com.canoo.implementation.dolphin.server.javaee;

import com.canoo.implementation.dolphin.server.bootstrap.DolphinPlatformBootstrap;
import com.canoo.implementation.dolphin.server.config.ConfigurationFileLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

/**
 * The Dolphin Platform Boostrap for a JavaEE based application.
 *
 * @author Hendrik Ebbers
 */
public class DolphinPlatformJavaeeBootstrap implements ServletContainerInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(DolphinPlatformJavaeeBootstrap.class);

    @Override
    public void onStartup(final Set<Class<?>> c, final ServletContext servletContext) throws ServletException {
        DolphinPlatformBootstrap.start(servletContext, ConfigurationFileLoader.loadConfiguration());
    }
}
