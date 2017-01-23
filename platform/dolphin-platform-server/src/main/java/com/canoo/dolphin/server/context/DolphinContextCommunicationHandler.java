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

import com.canoo.dolphin.server.config.DolphinPlatformConfiguration;
import com.canoo.common.Assert;
import com.canoo.dolphin.util.DolphinRemotingException;
import com.canoo.communication.common.commands.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

public class DolphinContextCommunicationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DolphinContextCommunicationHandler.class);

    private final DolphinPlatformConfiguration configuration;

    private final DolphinContextProvider contextProvider;

    public DolphinContextCommunicationHandler(final DolphinPlatformConfiguration configuration, final DolphinContextProvider contextProvider) {
        this.configuration = Assert.requireNonNull(configuration, "configuration");
        this.contextProvider = Assert.requireNonNull(contextProvider, "contextProvider");
    }

    public void handle(final HttpServletRequest request, final HttpServletResponse response) {
        Assert.requireNonNull(request, "request");
        Assert.requireNonNull(response, "response");
        final HttpSession httpSession = Assert.requireNonNull(request.getSession(), "request.getSession()");

        try {
            DolphinContext currentContext = contextProvider.getCurrentContext();
            if (currentContext == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                LOG.warn("Can not find or create matching dolphin context in session " + httpSession.getId());
                return;
            }

            String userAgent = request.getHeader("user-agent");
            LOG.trace("receiving Request for DolphinContext {} in http session {} from client with user-agent {}", currentContext.getId(), httpSession.getId(), userAgent);

            final List<Command> commands = new ArrayList<>();
            try {
                StringBuilder requestJson = new StringBuilder();
                String line;
                while ((line = request.getReader().readLine()) != null) {
                    requestJson.append(line).append("\n");
                }
                commands.addAll(currentContext.getDolphin().getServerConnector().getCodec().decode(requestJson.toString()));
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                LOG.error("Can not parse request! (DolphinContext " + currentContext.getId() + ")", e);
                return;
            }
            LOG.trace("Request for DolphinContext {} in http session {} contains {} commands", currentContext.getId(), httpSession.getId(), commands.size());


            final List<Command> results = new ArrayList<>();
            try {
                results.addAll(currentContext.handle(commands));
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                LOG.error("Can not handle the commands (DolphinContext " + currentContext.getId() + ")", e);
                return;
            }

            response.setHeader("Content-Type", "application/json");
            response.setCharacterEncoding("UTF-8");

            LOG.trace("Sending response for DolphinContext {} in http session {} from client with user-agent {}", currentContext.getId(), httpSession.getId(), userAgent);
            LOG.trace("Response for DolphinContext {} in http session {} contains {} commands", currentContext.getId(), httpSession.getId(), results.size());

            try {
                final String jsonResponse = currentContext.getDolphin().getServerConnector().getCodec().encode(results);
                response.getWriter().print(jsonResponse);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                LOG.error("Can not write response!", e);
                return;
            }
        } catch (Exception e) {
            throw new DolphinRemotingException("Unexpected Dolphin Platform error", e);
        }
    }
}
