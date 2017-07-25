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
package com.canoo.dp.impl.server.client;

import com.canoo.dp.impl.platform.core.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * A {@link HttpSessionListener} that destroys all {@link DolphinContext} instances for a session
 */
public class HttpSessionCleanerListener implements HttpSessionListener {

    private static final Logger LOG = LoggerFactory.getLogger(HttpSessionCleanerListener.class);

    private final ClientSessionManager clientSessionManager;

    public HttpSessionCleanerListener(ClientSessionManager clientSessionManager) {
        this.clientSessionManager = clientSessionManager;
    }

    @Override
    public void sessionCreated(HttpSessionEvent sessionEvent) {}

    @Override
    public void sessionDestroyed(HttpSessionEvent sessionEvent) {
        Assert.requireNonNull(sessionEvent, "sessionEvent");
        LOG.trace("Http session {} destroyed! Will remove all client sessions for the http session.", sessionEvent.getSession().getId());
        clientSessionManager.removeAllClientSessionsInHttpSession(sessionEvent.getSession());
    }
}
