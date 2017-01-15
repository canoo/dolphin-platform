package com.canoo.dolphin.server.event;

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.server.context.DolphinSessionLifecycleHandler;
import com.canoo.dolphin.server.context.DolphinSessionProvider;
import com.canoo.dolphin.server.event.impl.AbstractEventBus;
import com.canoo.dolphin.server.event.impl.DolphinEvent;
import com.canoo.dolphin.util.Assert;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DistributedEventBus extends AbstractEventBus implements DolphinEventBus {

    private final HazelcastInstance hazelcastClient;

    private final Map<String, String> iTopicRegistrations = new ConcurrentHashMap<>();

    private final Map<String, Integer> iTopicCount = new ConcurrentHashMap<>();

    private final Lock hazelcastEventPipeLock = new ReentrantLock();

    public DistributedEventBus(final HazelcastInstance hazelcastClient, final DolphinSessionProvider sessionProvider, final DolphinSessionLifecycleHandler lifecycleHandler) {
        super(sessionProvider, lifecycleHandler);
        this.hazelcastClient = hazelcastClient;
    }

    protected  <T extends Serializable> void publishForOtherSessions(final DolphinEvent<T> event) {
        ITopic<DolphinEvent<T>> topic = toHazelcastTopic(event.getTopic());
        topic.publish(event);
    }

    @Override
    public <T extends Serializable> Subscription subscribe(final Topic<T> topic, final MessageListener<? super T> handler) {
        final Subscription basicSubscription = subscribeListenerToTopic(topic, handler);
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

            Integer currentCount = iTopicCount.get(topic.getName());
            if (currentCount == 0) {
                registerHazelcastEventPipe(topic);
            } else {
                iTopicCount.put(topic.getName(), currentCount + 1);
            }

            return new Subscription() {
                @Override
                public void unsubscribe() {
                    Integer currentCount = iTopicCount.get(topic.getName());
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
                    DolphinEvent<T> event = message.getMessageObject();
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

            Integer count = iTopicCount.get(hazelcastTopic.getName());
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

    private <T extends Serializable> ITopic<DolphinEvent<T>> toHazelcastTopic(Topic<T> topic) {
        return hazelcastClient.getTopic(topic.getName());
    }

}