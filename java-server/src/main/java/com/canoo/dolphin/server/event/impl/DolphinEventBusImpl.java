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
package com.canoo.dolphin.server.event.impl;

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.server.context.DolphinContext;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.Message;
import com.canoo.dolphin.server.event.MessageListener;
import com.canoo.dolphin.server.event.Topic;
import groovyx.gpars.dataflow.DataflowQueue;
import org.opendolphin.StringUtil;
import org.opendolphin.core.server.EventBus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class DolphinEventBusImpl implements DolphinEventBus {

    private static final DolphinEventBusImpl instance = new DolphinEventBusImpl();
    private static final int MAX_POLL_DURATION = 100;
    public static final int TIMEOUT = 20;

    public static DolphinEventBusImpl getInstance() {
        return instance;
    }

    private final EventBus eventBus = new EventBus();

    private final DataflowQueue sender = new DataflowQueue();

    private final Object releaseVal = new Object();

    //access is only concurrent for different keys. This sync strategy should be sufficient
    private Map<String, Receiver> receiverPerSession = new ConcurrentHashMap<>();

    protected DolphinEventBusImpl() {
    }

    public <T> void publish(final Topic<T> topic, final T data) {
        if(topic == null) {
            throw new IllegalArgumentException("topic mustn't be null!");
        }
        eventBus.publish(sender, new MessageImpl(topic, data));
    }

    public void triggerTaskExecution() {
        eventBus.publish(sender, new TaskTrigger(){});
    }

    public <T> Subscription subscribe(final Topic<T> topic, final MessageListener<T> handler) {
        if(topic == null) {
            throw new IllegalArgumentException("topic mustn't be null!");
        }
        if(handler == null) {
            throw new IllegalArgumentException("handler mustn't be empty!");
        }
        String dolphinId = getDolphinId();
        if (dolphinId == null) {
            throw new IllegalStateException("subscribe was called outside a dolphin session");
        }
        return getOrCreateReceiverInSession(dolphinId).subscribe(topic, handler);
    }

    protected String getDolphinId() {
        return DolphinContext.getCurrentContext().getId();
    }

    public void unsubscribeSession(final String dolphinId) {
        if(StringUtil.isBlank(dolphinId)) {
            throw new IllegalArgumentException("dolphinId mustn't be empty!");
        }
        Receiver receiver = receiverPerSession.remove(dolphinId);
        if (receiver != null) {
            receiver.unregister(eventBus);
        }
    }

    private Receiver getOrCreateReceiverInSession(String dolphinId) {
        if(StringUtil.isBlank(dolphinId)) {
            throw new IllegalArgumentException("dolphinId mustn't be empty!");
        }
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
        //TODO replace by log
//        System.out.println("long poll call from dolphin session " + dolphinId);
        final Receiver receiverInSession = getOrCreateReceiverInSession(dolphinId);
        if (!receiverInSession.isListeningToEventBus()) {
            receiverInSession.register(eventBus);
        }
        final DataflowQueue receiverQueue = receiverInSession.getReceiverQueue();

        boolean somethingHandled = false;

        while (!somethingHandled) {
            //blocking call
            Object val = receiverQueue.getVal();

            final long startTime = System.currentTimeMillis();

            while (val != null) {
                if (val == releaseVal) {
                    return;
                }
                if(val instanceof  Message) {
                    final Message event = (Message) val;
                    //TODO replace by log
//                    System.out.println("handle event for dolphinId: " + dolphinId);
                    somethingHandled |= receiverInSession.handle(event);
                } else if(TaskTrigger.class.isAssignableFrom(val.getClass())) {
                    somethingHandled |= DolphinContext.getCurrentContext().getTaskExecutor().execute();
                }

                //if there are many events we would loop forever -> additional exit condition
                if (System.currentTimeMillis() - startTime <= MAX_POLL_DURATION) {
                    val = receiverQueue.getVal(TIMEOUT, MILLISECONDS);
                } else {
                    val = null;
                }
            }
        }
    }

    /**
     * TODO we must release per session!!!
     */
    @SuppressWarnings("unchecked")
    public void release() {
//        //TODO the release should happen in the context of a dolphin session.
//        //TODO this piece of code should be used then
//        String dolphinId = getId();
//        if (dolphinId == null) {
//            //TODO replace by log
//            System.out.println("warning: release was called outside dolphin session");
//            //TODO warn or throw exception?
//            return;
//        }
//        Receiver receiver = receiverPerSession.get(dolphinId);
//        if (receiver != null && receiver.isListeningToEventBus()) {
//            receiver.getReceiverQueue().leftShift(releaseVal);
//        }


        //TODO remove
        for (Receiver receiver : receiverPerSession.values()) {
            if (receiver.isListeningToEventBus()) {
                receiver.getReceiverQueue().leftShift(releaseVal);
            }
        }
    }

    private final static class MessageImpl<T> implements Message<T> {

        private final Topic<T> topic;

        private final T data;

        private final long timestamp;

        private MessageImpl(Topic<T> topic, T data) {
            this.topic = topic;
            this.data = data;
            this.timestamp = System.currentTimeMillis();
        }

        @Override
        public Topic<T> getTopic() {
            return topic;
        }

        @Override
        public T getData() {
            return data;
        }

        @Override
        public long getSendTimestamp() {
            return timestamp;
        }
    }
}
