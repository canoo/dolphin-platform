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

import com.canoo.platform.core.functional.Subscription;
import com.canoo.platform.server.client.ClientSession;
import com.canoo.platform.core.functional.Callback;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ClientSessionLifecycleHandlerImpl implements ClientSessionLifecycleHandler {

    private final List<Callback<ClientSession>> onCreateCallbacks = new CopyOnWriteArrayList<>();

    private final List<Callback<ClientSession>> onDestroyCallbacks = new CopyOnWriteArrayList<>();

    private final ThreadLocal<ClientSession> currentClientSession = new ThreadLocal<>();

    @Override
    public Subscription addSessionCreatedListener(final Callback<ClientSession> listener) {
        onCreateCallbacks.add(listener);
        return new Subscription() {
            @Override
            public void unsubscribe() {
                onCreateCallbacks.remove(listener);
            }
        };
    }

    @Override
    public Subscription addSessionDestroyedListener(final Callback<ClientSession> listener) {
        onDestroyCallbacks.add(listener);
        return new Subscription() {
            @Override
            public void unsubscribe() {
                onDestroyCallbacks.remove(listener);
            }
        };
    }

    @Override
    public void onSessionCreated(final ClientSession session) {
        for (Callback<ClientSession> listener : onCreateCallbacks) {
            try {
                listener.call(session);
            } catch (Exception e) {
                throw new RuntimeException("Error while handling onSessionCreated listener", e);
            }
        }
    }

    @Override
    public void onSessionDestroyed(final ClientSession session) {
        for (Callback<ClientSession> listener : onDestroyCallbacks) {
            try {
                listener.call(session);
            } catch (Exception e) {
                throw new RuntimeException("Error while handling onSessionDestroyed listener", e);
            }
        }
    }

    public ClientSession getCurrentDolphinSession() {
        return currentClientSession.get();
    }

    public void setCurrentSession(ClientSession currentSession) {
        currentClientSession.set(currentSession);
    }
}
