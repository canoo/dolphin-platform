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
import com.canoo.platform.server.event.Message;
import com.canoo.platform.server.event.Topic;

import java.io.Serializable;

public class DefaultMessage<T extends Serializable> implements Message<T> {

    private final Topic<T> topic;

    private final T data;

    private final long timestamp;

    public DefaultMessage(final Topic<T> topic, final T data, final long timestamp) {
        this.topic = Assert.requireNonNull(topic, "topic");
        this.data = data;
        this.timestamp = timestamp;
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
