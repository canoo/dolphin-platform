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

import com.canoo.dp.impl.server.bootstrap.PlatformBootstrap;
import com.canoo.dp.impl.server.config.ConfigurationFileLoader;
import com.canoo.dp.impl.server.config.DefaultPlatformConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Basic Bootstrap for Spring based application. The bootstrap automatically starts the dolphin platform bootstrap.
 *
 * @author Hendrik Ebbers
 */
@Configuration
public class DolphinPlatformSpringBootstrap implements ServletContextInitializer, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(DolphinPlatformSpringBootstrap.class);

    private static final String PREFIX = "dolphinPlatform.";

    @Autowired
    private Environment environment;

    @Autowired(required = false)
    private DefaultPlatformConfiguration injectedConfig;

    @Override
    public void onStartup(final ServletContext servletContext) throws ServletException {
        DefaultPlatformConfiguration configuration = injectedConfig;
        if(configuration == null) {
            configuration = ConfigurationFileLoader.loadConfiguration();
        }
        updateConfigurationBySpring(configuration);
        PlatformBootstrap bootstrap = new PlatformBootstrap();
        bootstrap.init(servletContext, configuration);
    }

    private void updateConfigurationBySpring(DefaultPlatformConfiguration configuration) {
        for(String key : configuration.getPropertyKeys()) {
            String valInSpringConfig = environment.getProperty(PREFIX + key);
            if(valInSpringConfig != null) {
                LOG.debug("Dolphin Platform property '{}' found in spring configuration", key);
                configuration.setProperty(key, valInSpringConfig);
            }
        }
    }

    private static ApplicationContext ctx = null;

    public static ApplicationContext getApplicationContext() {
        return ctx;
    }

    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.ctx = ctx;
    }
}
