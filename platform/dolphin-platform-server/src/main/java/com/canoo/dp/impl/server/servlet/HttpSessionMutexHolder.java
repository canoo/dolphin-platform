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

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class HttpSessionMutexHolder implements HttpSessionListener {

    private final Map<String, WeakReference<Mutex>> sessionMutexMap = new HashMap<>();

    private final static String SESSION_MUTEX_ATTRIBUTE = "Session-Mutex";

    @Override
    public void sessionCreated(final HttpSessionEvent sessionEvent) {
        Assert.requireNonNull(sessionEvent, "sessionEvent");
        final HttpSession session = sessionEvent.getSession();
        Assert.requireNonNull(session, "session");
        final Mutex mutex = new Mutex();
        session.setAttribute(SESSION_MUTEX_ATTRIBUTE, mutex);
        sessionMutexMap.put(session.getId(), new WeakReference<>(mutex));
    }

    @Override
    public void sessionDestroyed(final HttpSessionEvent sessionEvent) {
        Assert.requireNonNull(sessionEvent, "sessionEvent");
        final HttpSession session = sessionEvent.getSession();
        Assert.requireNonNull(session, "session");
        sessionMutexMap.remove(session.getId());
    }

    public Mutex getMutexForHttpSession(final String sessionId) {
        Assert.requireNonBlank(sessionId, "sessionId");
        final WeakReference<Mutex> mutexReference = sessionMutexMap.get(sessionId);
        Assert.requireNonNull(mutexReference, "mutexReference");
        return mutexReference.get();
    }
}
