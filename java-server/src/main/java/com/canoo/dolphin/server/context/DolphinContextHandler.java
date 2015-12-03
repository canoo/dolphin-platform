/*
 * Copyright 2015 Canoo Engineering AG.
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

import com.canoo.dolphin.server.container.ContainerManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DolphinContextHandler {

    private static final Map<String, List<DolphinContext>> globalContextMap = new HashMap<>();

    private static final Lock globalContextMapLock = new ReentrantLock();

    private static final ThreadLocal<DolphinContext> currentContextThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<String> sessionIdThreadLocal = new ThreadLocal<>();

    private final ContainerManager containerManager;

    private final static DolphinContextHandler INSTANCE = new DolphinContextHandler();

    private DolphinContextHandler() {
        ServiceLoader<ContainerManager> serviceLoader = ServiceLoader.load(ContainerManager.class);
        Iterator<ContainerManager> serviceIterator = serviceLoader.iterator();
        if (serviceIterator.hasNext()) {
            this.containerManager = serviceIterator.next();
            if (serviceIterator.hasNext()) {
                throw new RuntimeException("More than 1 " + ContainerManager.class + " found!");
            }
        } else {
            throw new RuntimeException("No " + ContainerManager.class + " found!");
        }

    }

    public void handle(HttpServletRequest request, HttpServletResponse response) {
        //This will refactored later to support a client scope (tab based in browser)
        currentContextThreadLocal.remove();
        DolphinContext currentContext;
        globalContextMapLock.lock();
        try {
            List<DolphinContext> contextList = globalContextMap.get(request.getSession().getId());
            if (contextList == null) {
                contextList = new ArrayList<>();
                globalContextMap.put(request.getSession().getId(), contextList);
            }
            if (contextList.isEmpty()) {
                currentContext = new DolphinContext(containerManager, request.getServletContext());
                contextList.add(currentContext);
            } else {
                currentContext = contextList.get(0);
            }
        } finally {
            globalContextMapLock.unlock();
        }
        currentContextThreadLocal.set(currentContext);
        sessionIdThreadLocal.set(request.getSession().getId());

        try {
            currentContext.handleRequest(request, response);
        } catch (Exception e) {
            throw new RuntimeException("Error in Dolphin command handling", e);
        } finally {
            currentContextThreadLocal.remove();
            sessionIdThreadLocal.remove();
        }
    }

    /**
     * Deprecated because of the client scope that will iontroduced in the next version
     *
     * @param session
     * @return
     */
    public static List<DolphinContext> getContexts(HttpSession session) {
        globalContextMapLock.lock();
        try {
            return globalContextMap.get(session.getId());
        } finally {
            globalContextMapLock.unlock();
        }
    }

    public static DolphinContext getCurrentContext() {
        return currentContextThreadLocal.get();
    }

    public static List<DolphinContext> getAllContexts() {
        globalContextMapLock.lock();
        try {
            List<DolphinContext> ret = new ArrayList<>();
            for (List<DolphinContext> sessionList : globalContextMap.values()) {
                ret.addAll(sessionList);
            }
            return ret;
        } finally {
            globalContextMapLock.unlock();
        }
    }

    public static List<DolphinContext> getAllContextsInSession() {
        globalContextMapLock.lock();
        try {
            return globalContextMap.get(sessionIdThreadLocal.get());
        } finally {
            globalContextMapLock.unlock();
        }
    }

    public static void removeAllContextsInSession(HttpSession session) {
        globalContextMapLock.lock();
        try {
            globalContextMap.remove(session.getId());
        } finally {
            globalContextMapLock.unlock();
        }
    }

    public static DolphinContextHandler getInstance() {
        return INSTANCE;
    }
}
