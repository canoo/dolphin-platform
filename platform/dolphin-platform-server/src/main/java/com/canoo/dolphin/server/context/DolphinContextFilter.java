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
package com.canoo.dolphin.server.context;

import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.server.config.DolphinPlatformConfiguration;
import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.common.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class DolphinContextFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(DolphinContextFilter.class);

    private static final String DOLPHIN_PLATFORM_INITIALIZED_IN_SESSION = "DOLPHIN_PLATFORM_INITIALIZED_IN_SESSION";

    private final DolphinPlatformConfiguration configuration;

    private final ContainerManager containerManager;

    private final DolphinContextFactory dolphinContextFactory;

    private final DolphinSessionLifecycleHandler lifecycleHandler;

    public DolphinContextFilter(final DolphinPlatformConfiguration configuration, final ContainerManager containerManager, final DolphinContextFactory dolphinContextFactory, final DolphinSessionLifecycleHandler lifecycleHandler) {
        this.configuration = Assert.requireNonNull(configuration, "configuration");
        this.containerManager = Assert.requireNonNull(containerManager, "containerManager");
        this.dolphinContextFactory = Assert.requireNonNull(dolphinContextFactory, "dolphinContextFactory");
        this.lifecycleHandler = Assert.requireNonNull(lifecycleHandler, "lifecycleHandler");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        DolphinContextUtils.setContextForCurrentThread(null);
        final HttpServletRequest servletRequest = (HttpServletRequest) request;
        final HttpServletResponse servletResponse = (HttpServletResponse) response;
        final HttpSession httpSession = Assert.requireNonNull(servletRequest.getSession(), "request.getSession()");

        try {
            DolphinContext dolphinContext;
            final String clientId = servletRequest.getHeader(PlatformConstants.CLIENT_ID_HTTP_HEADER_NAME);
            if (clientId == null || clientId.trim().isEmpty()) {
                if (DolphinContextUtils.getOrCreateContextMapInSession(httpSession).size() >= configuration.getMaxClientsPerSession()) {
                    servletResponse.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Maximum size for clients in session is reached");
                    LOG.info("Maximum size for clients in session {} is reached", servletRequest.getSession().getId());
                    return;
                }
                dolphinContext = createNewContext(httpSession);
                DolphinContextUtils.storeInSession(httpSession, dolphinContext);
                lifecycleHandler.onSessionCreated(dolphinContext.getDolphinSession());
                LOG.trace("Created new DolphinContext {} in http session {}", dolphinContext.getId(), httpSession.getId());

                Object init = httpSession.getAttribute(DOLPHIN_PLATFORM_INITIALIZED_IN_SESSION);
                if (init == null) {
                    httpSession.setAttribute(DOLPHIN_PLATFORM_INITIALIZED_IN_SESSION, true);
                }
            } else {
                LOG.trace("Trying to find DolphinContext {} in http session {}", clientId, httpSession.getId());
                dolphinContext = DolphinContextUtils.getClientInSession(httpSession, clientId);
                if (dolphinContext == null) {
                    Object init = httpSession.getAttribute(DOLPHIN_PLATFORM_INITIALIZED_IN_SESSION);
                    if (init == null) {
                        LOG.warn("Can not find requested client for id {} in session {} (session timeout)", clientId, httpSession.getId());
                        servletResponse.sendError(HttpServletResponse.SC_REQUEST_TIMEOUT, "Can not find requested client (session timeout)!");
                    } else {
                        LOG.warn("Can not find requested client for id {} in session {} (unknown error)", clientId, httpSession.getId());
                        servletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not find requested client (unknown error)!");
                    }
                    return;
                }
            }
            DolphinContextUtils.setContextForCurrentThread(dolphinContext);
            servletResponse.setHeader(PlatformConstants.CLIENT_ID_HTTP_HEADER_NAME, dolphinContext.getId());
            chain.doFilter(request, response);
        } finally {
            DolphinContextUtils.setContextForCurrentThread(null);
        }
    }

    private DolphinContext createNewContext(final HttpSession httpSession) {
        Assert.requireNonNull(httpSession, "httpSession");
        return dolphinContextFactory.create(httpSession);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //Nothing to do here
    }

    @Override
    public void destroy() {
    }
}
