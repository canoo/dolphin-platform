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

import com.canoo.dolphin.server.event.Message;
import com.canoo.dolphin.server.event.Topic;

public final class MessageImpl<T> implements Message<T> {

    private final Topic<T> topic;

    private final T data;

    private final long timestamp;

    protected MessageImpl(Topic<T> topic, T data) {
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