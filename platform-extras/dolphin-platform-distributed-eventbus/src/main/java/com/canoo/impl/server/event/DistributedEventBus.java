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
package com.canoo.impl.server.event;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.core.functional.Subscription;
import com.canoo.platform.server.event.MessageListener;
import com.canoo.platform.server.event.Topic;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DistributedEventBus extends AbstractEventBus {

    private final HazelcastInstance hazelcastClient;

    private final Map<String, String> iTopicRegistrations = new ConcurrentHashMap<>();

    private final Map<String, Integer> iTopicCount = new ConcurrentHashMap<>();

    private final Lock hazelcastEventPipeLock = new ReentrantLock();

    public DistributedEventBus(final HazelcastInstance hazelcastClient) {
        this.hazelcastClient = Assert.requireNonNull(hazelcastClient, "hazelcastClient");
    }

    protected <T extends Serializable> void publishForOtherSessions(final DolphinEvent<T> event) {
        Assert.requireNonNull(event, "event");
        final ITopic<DolphinEvent<T>> topic = toHazelcastTopic(event.getTopic());
        topic.publish(event);
    }

    @Override
    public <T extends Serializable> Subscription subscribe(final Topic<T> topic, final MessageListener<? super T> handler) {
        final Subscription basicSubscription = super.subscribe(topic, handler);
        final Subscription hazelcastSubscription = createHazelcastSubscription(topic);
        return new Subscription() {
            @Override
            public void unsubscribe() {
                hazelcastSubscription.unsubscribe();
                basicSubscription.unsubscribe();
            }
        };
    }

    private <T extends Serializable> Subscription createHazelcastSubscription(final Topic<T> topic) {
        hazelcastEventPipeLock.lock();
        try {
            final ITopic<DolphinEvent<T>> hazelcastTopic = toHazelcastTopic(topic);
            Assert.requireNonNull(hazelcastTopic, "hazelcastTopic");

            final Integer currentCount = iTopicCount.get(topic.getName());
            if (currentCount == null || currentCount == 0) {
                registerHazelcastEventPipe(topic);
            } else {
                iTopicCount.put(topic.getName(), currentCount + 1);
            }

            return new Subscription() {
                @Override
                public void unsubscribe() {
                    final Integer currentCount = iTopicCount.get(topic.getName());
                    if (currentCount > 1) {
                        iTopicCount.put(topic.getName(), currentCount - 1);
                    } else {
                        unregisterHazelcastEventPipe(topic);
                    }
                }
            };
        } finally {
            hazelcastEventPipeLock.unlock();
        }
    }

    private <T extends Serializable> void registerHazelcastEventPipe(final Topic<T> topic) {
        hazelcastEventPipeLock.lock();
        try {
            final ITopic<DolphinEvent<T>> hazelcastTopic = toHazelcastTopic(topic);
            Assert.requireNonNull(hazelcastTopic, "hazelcastTopic");

            final String registrationId = hazelcastTopic.addMessageListener(new com.hazelcast.core.MessageListener<DolphinEvent<T>>() {
                @Override
                public void onMessage(com.hazelcast.core.Message<DolphinEvent<T>> message) {
                    final DolphinEvent<T> event = message.getMessageObject();
                    triggerEventHandling(event);
                }
            });
            Assert.requireNonBlank(registrationId, "registrationId");

            iTopicRegistrations.put(hazelcastTopic.getName(), registrationId);
            iTopicCount.put(hazelcastTopic.getName(), 1);
        } finally {
            hazelcastEventPipeLock.unlock();
        }
    }

    private <T extends Serializable> void unregisterHazelcastEventPipe(final Topic<T> topic) {
        hazelcastEventPipeLock.lock();
        try {
            final ITopic<DolphinEvent<T>> hazelcastTopic = toHazelcastTopic(topic);
            Assert.requireNonNull(hazelcastTopic, "hazelcastTopic");

            final Integer count = iTopicCount.get(hazelcastTopic.getName());
            if (count == null || count != 1) {
                throw new IllegalStateException("Count for topic " + topic.getName() + " is wrong: " + count);
            }

            final String registrationId = iTopicRegistrations.get(hazelcastTopic.getName());
            Assert.requireNonBlank(registrationId, "registrationId");

            hazelcastTopic.removeMessageListener(registrationId);

            iTopicRegistrations.remove(hazelcastTopic.getName());
            iTopicCount.remove(hazelcastTopic.getName());
        } finally {
            hazelcastEventPipeLock.unlock();
        }
    }

    private <T extends Serializable> ITopic<DolphinEvent<T>> toHazelcastTopic(final Topic<T> topic) {
        return hazelcastClient.getTopic(topic.getName());
    }

}
