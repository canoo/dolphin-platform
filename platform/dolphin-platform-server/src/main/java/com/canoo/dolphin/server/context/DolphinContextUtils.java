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

import com.canoo.common.Assert;

import javax.servlet.http.HttpSession;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class DolphinContextUtils {

    private static final String DOLPHIN_CONTEXT_MAP = "DOLPHIN_CONTEXT_MAP";

    private static final ThreadLocal<DolphinContext> currentContext = new ThreadLocal<>();

    private static final HashMap<String, WeakReference<DolphinContext>> weakContextMap = new HashMap<>();

    private DolphinContextUtils() {
    }

    public static Future<Void> runLaterInClientSession(String clientSessionId, Runnable runnable) {
        Assert.requireNonBlank(clientSessionId, "clientSessionId");
        Assert.requireNonNull(runnable, "runnable");

        WeakReference<DolphinContext> ref = weakContextMap.get(clientSessionId);
        DolphinContext dolphinContext = ref.get();
        Assert.requireNonNull(dolphinContext, "dolphinContext");

        return dolphinContext.runLater(runnable);
    }

    public static DolphinContext getClientInSession(HttpSession session, String clientId) {
        Assert.requireNonNull(session, "session");
        Assert.requireNonBlank(clientId, "clientId");
        Object contextMap = session.getAttribute(DOLPHIN_CONTEXT_MAP);
        if(contextMap == null) {
            return null;
        }
        return ((Map<String, DolphinContext>) contextMap).get(clientId);
    }

    public static void storeInSession(HttpSession session, DolphinContext context) {
        Assert.requireNonNull(session, "session");
        Assert.requireNonNull(context, "context");
        getOrCreateContextMapInSession(session).put(context.getId(), context);
    }

    public static void removeFromSession(HttpSession session, DolphinContext context) {
        Assert.requireNonNull(session, "session");
        Assert.requireNonNull(context, "context");
        getOrCreateContextMapInSession(session).remove(context.getId());
        weakContextMap.remove(context.getId());
    }

    public static Map<String, DolphinContext> getOrCreateContextMapInSession(HttpSession session) {
        Assert.requireNonNull(session, "session");
        Object contextMap = session.getAttribute(DOLPHIN_CONTEXT_MAP);
        if (contextMap == null) {
            contextMap = new HashMap<>();
            session.setAttribute(DOLPHIN_CONTEXT_MAP, contextMap);
        }
        return (Map<String, DolphinContext>) contextMap;
    }

    public static void removeAllContextsInSession(HttpSession session) {
        Assert.requireNonNull(session, "session");
        List<DolphinContext> currentContexts = new ArrayList<>(getOrCreateContextMapInSession(session).values());
        for (DolphinContext context : currentContexts) {
            context.destroy();
        }
        session.removeAttribute(DOLPHIN_CONTEXT_MAP);
    }

    public static DolphinContext getContextForCurrentThread() {
        return currentContext.get();
    }

    public static void setContextForCurrentThread(DolphinContext dolphinContext) {
        currentContext.set(dolphinContext);

        if(dolphinContext != null && !weakContextMap.containsKey(dolphinContext.getId())) {
            weakContextMap.put(dolphinContext.getId(), new WeakReference<>(dolphinContext));
        }
    }
}
