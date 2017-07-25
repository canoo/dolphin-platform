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
package com.canoo.dp.impl.server.servlet;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.server.config.DefaultModuleConfig;
import com.canoo.platform.server.spi.PlatformConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class HttpSessionTimeoutListener implements HttpSessionListener {

    private static final Logger LOG = LoggerFactory.getLogger(HttpSessionTimeoutListener.class);

    private final int sessionTimeoutInSeconds;

    public HttpSessionTimeoutListener(final PlatformConfiguration configuration) {
        this.sessionTimeoutInSeconds = DefaultModuleConfig.getSessionTimeout(configuration);
    }

    @Override
    public void sessionCreated(HttpSessionEvent sessionEvent) {
        Assert.requireNonNull(sessionEvent, "sessionEvent");
        try {
            sessionEvent.getSession().setMaxInactiveInterval(sessionTimeoutInSeconds);
        } catch (Exception e) {
            LOG.warn("Can not set the defined session timeout!", e);
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {}
}
