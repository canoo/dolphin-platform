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

/**
 * An message of the dolphin platform event bus (see {@link com.canoo.dolphin.server.event.DolphinEventBus}).
 */
public interface Message<T> {


    /**
     * Returns the topic of the event
     * @return the topic
     */
    Topic<T> getTopic();

    /**
     * Returns the data of the message
     * @return the data
     */
    T getData();

    /**
     * Returns the timestamp of the send date of this message
     * @return the timestamp
     */
    long getSendTimestamp();
}
