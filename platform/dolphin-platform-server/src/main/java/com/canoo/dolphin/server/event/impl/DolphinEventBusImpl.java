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
package com.canoo.dolphin.server.event.impl;

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.server.context.DolphinContextProvider;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.Message;
import com.canoo.dolphin.server.event.MessageListener;
import com.canoo.dolphin.server.event.Topic;
import com.canoo.dolphin.util.Assert;
import groovyx.gpars.dataflow.DataflowQueue;
import org.opendolphin.StringUtil;
import org.opendolphin.core.server.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class DolphinEventBusImpl implements DolphinEventBus {

    private static final int MAX_POLL_DURATION = 500;

    public static final int TIMEOUT = 100;

    private static final Logger LOG = LoggerFactory.getLogger(DolphinEventBusImpl.class);

    private final EventBus eventBus = new EventBus();

    private final DataflowQueue sender = new DataflowQueue();

    private final Object releaseVal = new Object();

    //access is only concurrent for different keys. This sync strategy should be sufficient
    private Map<String, Receiver> receiverPerSession = new ConcurrentHashMap<>();

    private final DolphinContextProvider dolphinContextProvider;

    public DolphinEventBusImpl(DolphinContextProvider dolphinContextProvider) {
        this.dolphinContextProvider = Assert.requireNonNull(dolphinContextProvider, "dolphinContextProvider");
    }

    public <T> void publish(final Topic<T> topic, final T data) {
        if (topic == null) {
            throw new IllegalArgumentException("topic must not be null!");
        }
        eventBus.publish(sender, new MessageImpl(topic, data));
    }

    public <T> Subscription subscribe(final Topic<T> topic, final MessageListener<? super T> handler) {
        if (topic == null) {
            throw new IllegalArgumentException("topic must not be null!");
        }
        if (handler == null) {
            throw new IllegalArgumentException("handler must not be empty!");
        }
        String dolphinId = getDolphinId();
        if (dolphinId == null) {
            throw new IllegalStateException("subscribe was called outside a dolphin session");
        }
        return getOrCreateReceiverInSession(dolphinId).subscribe(topic, handler);
    }

    protected String getDolphinId() {
        return dolphinContextProvider.getCurrentContext().getId();
    }

    public void unsubscribeSession(final String dolphinId) {
        if (StringUtil.isBlank(dolphinId)) {
            throw new IllegalArgumentException("dolphinId must not be empty!");
        }
        Receiver receiver = receiverPerSession.remove(dolphinId);
        if (receiver != null) {
            receiver.unregister(eventBus);
        }
    }

    private Receiver getOrCreateReceiverInSession(String dolphinId) {
        Assert.requireNonBlank(dolphinId, "dolphinId");

        Receiver receiver = receiverPerSession.get(dolphinId);
        if (receiver == null) {
            receiver = new Receiver();
            receiverPerSession.put(dolphinId, receiver);
        }
        return receiver;
    }

    /**
     * this method blocks till a release event occurs or there is something to handle in this session.
     * This is the only location where we subscribe to our internal eventBus.
     * So if longPoll is not called from client, we will never listen to the eventBus.
     */
    public void longPoll() throws InterruptedException {
        final String dolphinId = getDolphinId();
        if (dolphinId == null) {
            throw new IllegalStateException("longPoll was called outside a dolphin session");
        }
        LOG.debug("long poll call for dolphin session {}", dolphinId);
        final Receiver receiverInSession = getOrCreateReceiverInSession(dolphinId);
        if (!receiverInSession.isListeningToEventBus()) {
            receiverInSession.register(eventBus);
        }
        final DataflowQueue receiverQueue = receiverInSession.getReceiverQueue();

        final long maxTime = System.currentTimeMillis() + MAX_POLL_DURATION;
        while (System.currentTimeMillis() < maxTime) {
            try {
                Object val = receiverQueue.getVal(TIMEOUT, MILLISECONDS);
                if (val == releaseVal) {
                    return;
                }
                if (val instanceof Message) {
                    final Message message = (Message) val;
                    LOG.debug("Handle Event Bus Message for topic {} in context {}", message.getTopic(), dolphinId);
                    receiverInSession.handle(message);
                }
            } catch (InterruptedException e) {
                //Interrupt is ok, maybe we do not have a new message...
            }
        }
        LOG.debug("long poll released for dolphin session {}", dolphinId);
    }

    public void release() {
        String dolphinId = getDolphinId();
        if (dolphinId == null) {
            throw new IllegalStateException("release was called outside a dolphin session");
        }

        Receiver receiver = receiverPerSession.get(dolphinId);
        if (receiver != null && receiver.isListeningToEventBus()) {
            receiver.getReceiverQueue().leftShift(releaseVal);
        }
    }
}
