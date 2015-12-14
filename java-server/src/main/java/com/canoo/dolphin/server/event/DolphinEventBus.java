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
package com.canoo.dolphin.server.event;

import com.canoo.dolphin.event.Subscription;

/**
 * The dolphin event bus that can be used to send messages to dolphin sessions.
 * The {@link com.canoo.dolphin.server.event.DolphinEventBus} can be injected in any
 * managed bean and will automatically publish the given data in the dolphin session.
 * THis means that you ca subscribe your dolphin controller (see {@link com.canoo.dolphin.server.DolphinController})
 * to the event bus and publish messages from any other bean like an REST endpoint.
 * <br>
 * <center><img src="doc-files/event-bus.png" alt="Notification MVC Widget example"></center>
 *
 */
public interface DolphinEventBus {

    /**
     * Publish a message to the given address
     *
     * @param data the data of the message
     */
    <T> void publish(Topic<T> topic, T data);

    /**
     * Register as a handler / listener for a given address. All messages that will be published for the given address
     * by any dolphin session will trigger the given handler in the correct dolphin session.
     *
     * @param topic   the topic
     * @param handler the handler
     */
    <T> Subscription subscribe(Topic<T> topic, MessageListener<T> handler);
}
