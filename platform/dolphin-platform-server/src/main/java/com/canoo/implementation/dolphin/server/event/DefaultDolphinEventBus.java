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
package com.canoo.implementation.dolphin.server.event;

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.server.session.DolphinSession;
import com.canoo.implementation.dolphin.server.context.DolphinSessionLifecycleHandler;
import com.canoo.implementation.dolphin.server.context.DolphinSessionProvider;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.Message;
import com.canoo.dolphin.server.event.MessageListener;
import com.canoo.dolphin.server.event.Topic;
import com.canoo.implementation.dolphin.util.Assert;
import com.canoo.implementation.dolphin.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultDolphinEventBus implements DolphinEventBus {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDolphinEventBus.class);

    private final Map<Topic<?>, List<MessageListener<?>>> topicListeners = new ConcurrentHashMap<>();

    private final Map<String, List<Subscription>> sessionStore = new ConcurrentHashMap<>();

    private final DolphinSessionProvider sessionProvider;

    public DefaultDolphinEventBus(DolphinSessionProvider sessionProvider, DolphinSessionLifecycleHandler lifecycleHandler) {
        this.sessionProvider = Assert.requireNonNull(sessionProvider, "sessionProvider");
        Assert.requireNonNull(lifecycleHandler, "lifecycleHandler").addSessionDestroyedListener(new Callback<DolphinSession>() {
            @Override
            public void call(DolphinSession dolphinSession) {
                onSessionEnds(dolphinSession.getId());
            }
        });
    }

    @Override
    public <T> void publish(final Topic<T> topic, final T data) {
        Assert.requireNonNull(topic, "topic");
        LOG.trace("Publishing data for topic {}", topic.getName());
        final List<MessageListener<?>> listeners = topicListeners.get(topic);
        if(listeners != null) {
            final long timestamp = System.currentTimeMillis();
            for(MessageListener<?> listener : listeners) {
                listener.onMessage(new Message() {
                    @Override
                    public Topic<?> getTopic() {
                        return topic;
                    }

                    @Override
                    public Object getData() {
                        return data;
                    }

                    @Override
                    public long getSendTimestamp() {
                        return timestamp;
                    }
                });
            }
        }
    }

    @Override
    public <T> Subscription subscribe(final Topic<T> topic, final MessageListener<? super T> handler) {
        Assert.requireNonNull(topic, "topic");
        Assert.requireNonNull(handler, "handler");
        final DolphinSession subscriptionSession = sessionProvider.getCurrentDolphinSession();
        if(subscriptionSession == null) {
            throw new IllegalStateException("Subscription can only be done from Dolphin Context!");
        }

        LOG.trace("Adding subscription for topic {} in Dolphin Platform context {}", topic.getName(), subscriptionSession.getId());
        List<MessageListener<?>> listeners = topicListeners.get(topic);
        if(listeners == null) {
            listeners = new CopyOnWriteArrayList<>();
            topicListeners.put(topic, listeners);
        }

        final MessageListener<? super T> listener = new MessageListener<T>() {
            @Override
            public void onMessage(final Message<T> message) {
                DolphinSession currentSession = sessionProvider.getCurrentDolphinSession();

                if(currentSession != null && currentSession.getId().equals(subscriptionSession.getId())) {
                    LOG.trace("Event listener for topic {} can be called directly in Dolphin Platform context {}", topic.getName(), subscriptionSession.getId());
                    ((MessageListener<T>)handler).onMessage(message);
                } else {
                    LOG.trace("Event listener for topic {} must be called later in Dolphin Platform context {}", topic.getName(), subscriptionSession.getId());
                    subscriptionSession.runLater(new Runnable() {

                        @Override
                        public void run() {
                            LOG.trace("Calling event listener for topic {} in Dolphin Platform context {}", topic.getName(), subscriptionSession.getId());
                            ((MessageListener<T>)handler).onMessage(message);
                        }
                    });
                }
            }
        };
        listeners.add(listener);

        Subscription subscription = new Subscription() {
            @Override
            public void unsubscribe() {
                LOG.trace("Removing subscription for topic {} in Dolphin Platform context {}", topic.getName(), subscriptionSession.getId());
                List<MessageListener<?>> listeners = topicListeners.get(topic);
                if(listeners != null) {
                    listeners.remove(listener);
                }
                removeSubscriptionForSession(this, subscriptionSession.getId());
            }
        };
        List<Subscription> subscriptionsForSession = sessionStore.get(subscriptionSession.getId());
        if(subscriptionsForSession == null) {
            subscriptionsForSession = new CopyOnWriteArrayList<>();
            sessionStore.put(subscriptionSession.getId(), subscriptionsForSession);
        }
        subscriptionsForSession.add(subscription);
        return subscription;
    }

    private void removeSubscriptionForSession(final Subscription subscription, final String dolphinSessionId) {
        List<Subscription> subscriptionsForSession = sessionStore.get(dolphinSessionId);
        if(subscriptionsForSession != null) {
            subscriptionsForSession.remove(subscription);
        }
    }

    private void onSessionEnds(final String dolphinSessionId) {
        Assert.requireNonBlank(dolphinSessionId, "dolphinSessionId");
        List<Subscription> subscriptions = sessionStore.get(dolphinSessionId);
        if(subscriptions != null) {
            for (Subscription subscription : subscriptions) {
                subscription.unsubscribe();
            }
        }
    }
}
