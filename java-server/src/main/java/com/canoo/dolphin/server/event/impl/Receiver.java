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
import com.canoo.dolphin.server.event.Message;
import com.canoo.dolphin.server.event.MessageListener;
import com.canoo.dolphin.server.event.Topic;
import groovyx.gpars.dataflow.DataflowQueue;
import org.opendolphin.core.server.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * represents one listener to the queue. It need not to be thread safe, because it is related to one dolphin session.
 */
public class Receiver {

    DataflowQueue receiverQueue;

    Map<Topic, List<MessageListener>> listenersPerTopic = new HashMap<>();

    public DataflowQueue getReceiverQueue() {
        return receiverQueue;
    }

    public <T> Subscription subscribe(final Topic<T> topic, final MessageListener<T> handler) {
        if(topic == null) {
            throw new IllegalArgumentException("topic mustn't be null!");
        }
        if(handler == null) {
            throw new IllegalArgumentException("handler mustn't be empty!");
        }
        List<MessageListener> messageListeners = listenersPerTopic.get(topic);
        if (messageListeners == null) {
            messageListeners = new CopyOnWriteArrayList<>();
            listenersPerTopic.put(topic, messageListeners);
        }
        messageListeners.add(handler);
        return new Subscription() {
            @Override
            public void unsubscribe() {
                List<MessageListener> messageListeners = listenersPerTopic.get(topic);
                if (messageListeners == null) {
                    return;
                }
                messageListeners.remove(handler);
                if (messageListeners.isEmpty()) {
                    listenersPerTopic.remove(topic);
                }
            }
        };
    }

    public boolean handle(Message event) {
        List<MessageListener> messageListeners = listenersPerTopic.get(event.getTopic());
        if (messageListeners == null || messageListeners.isEmpty()) {
            return false;
        }
        // iterate over copy, because the list could be changed in an onMessage method
        for (MessageListener messageListener : messageListeners) {
            messageListener.onMessage(event);
        }
        return true;
    }

    public boolean isListeningToEventBus() {
        return receiverQueue != null;
    }

    public void unregister(EventBus eventBus) {
        if(eventBus == null) {
            throw new IllegalArgumentException("eventBus mustn't be empty!");
        }
        if (isListeningToEventBus()) {
            eventBus.unSubscribe(receiverQueue);
            receiverQueue = null;
        }
        listenersPerTopic.clear();
    }

    public void register(EventBus eventBus) {
        if(eventBus == null) {
            throw new IllegalArgumentException("eventBus mustn't be empty!");
        }
        if (isListeningToEventBus()) {
            return;
        }
        receiverQueue = new DataflowQueue();
        eventBus.subscribe(receiverQueue);
    }
}
