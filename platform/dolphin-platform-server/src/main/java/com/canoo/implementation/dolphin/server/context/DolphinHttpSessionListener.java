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
package com.canoo.implementation.dolphin.server.context;

import com.canoo.implementation.dolphin.server.config.DolphinPlatformConfiguration;
import com.canoo.implementation.dolphin.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * A {@link HttpSessionListener} that destroys all {@link DolphinContext} instances for a session
 */
public class DolphinHttpSessionListener implements HttpSessionListener {

    private static final Logger LOG = LoggerFactory.getLogger(DolphinHttpSessionListener.class);

    private int sessionTimeoutInSeconds = DolphinPlatformConfiguration.SESSION_TIMEOUT_DEFAULT_VALUE;

    public void init(final DolphinPlatformConfiguration configuration) {
        this.sessionTimeoutInSeconds = Assert.requireNonNull(configuration, "configuration").getSessionTimeout();
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
    public void sessionDestroyed(HttpSessionEvent sessionEvent) {
        Assert.requireNonNull(sessionEvent, "sessionEvent");
        LOG.trace("Session " + sessionEvent.getSession().getId() + " destroyed! Will remove all DolphinContext instances for the session.");
        DolphinContextUtils.removeAllContextsInSession(sessionEvent.getSession());
    }
}
