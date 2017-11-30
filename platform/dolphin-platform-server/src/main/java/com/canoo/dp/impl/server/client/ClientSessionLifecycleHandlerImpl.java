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
import org.apiguardian.api.API;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public final class ClientSessionLifecycleHandlerImpl implements ClientSessionLifecycleHandler {

    private final List<Consumer<ClientSession>> onCreateCallbacks = new CopyOnWriteArrayList<>();

    private final List<Consumer<ClientSession>> onDestroyCallbacks = new CopyOnWriteArrayList<>();

    private final ThreadLocal<ClientSession> currentClientSession = new ThreadLocal<>();

    @Override
    public Subscription addSessionCreatedListener(final Consumer<ClientSession> listener) {
        onCreateCallbacks.add(listener);
        return new Subscription() {
            @Override
            public void unsubscribe() {
                onCreateCallbacks.remove(listener);
            }
        };
    }

    @Override
    public Subscription addSessionDestroyedListener(final Consumer<ClientSession> listener) {
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
        for (Consumer<ClientSession> listener : onCreateCallbacks) {
            try {
                listener.accept(session);
            } catch (Exception e) {
                throw new RuntimeException("Error while handling onSessionCreated listener", e);
            }
        }
    }

    @Override
    public void onSessionDestroyed(final ClientSession session) {
        for (Consumer<ClientSession> listener : onDestroyCallbacks) {
            try {
                listener.accept(session);
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
