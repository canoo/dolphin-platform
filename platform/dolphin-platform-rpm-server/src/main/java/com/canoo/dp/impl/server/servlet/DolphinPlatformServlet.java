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

import com.canoo.impl.server.context.DolphinContextCommunicationHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The default servlet of the dolphin platform. All communication is based on this servlet.
 */
public class DolphinPlatformServlet extends HttpServlet {

    private final DolphinContextCommunicationHandler communicationHandler;

    public DolphinPlatformServlet(DolphinContextCommunicationHandler communicationHandler) {
        this.communicationHandler = communicationHandler;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        communicationHandler.handle(req, resp);
    }
}
