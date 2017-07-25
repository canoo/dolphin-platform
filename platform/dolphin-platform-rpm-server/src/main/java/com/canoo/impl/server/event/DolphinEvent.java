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
import com.canoo.platform.server.event.EventSessionFilter;
import com.canoo.platform.server.event.Message;
import com.canoo.platform.server.event.Topic;

import java.io.Serializable;

public class DolphinEvent<T extends Serializable> implements Serializable {

    private final Message<T> message;

    private final String senderSessionId;

    private final EventSessionFilter sessionFilter;

    public DolphinEvent(final String senderSessionId, final Message<T> message, final EventSessionFilter sessionFilter) {
        this.senderSessionId = senderSessionId;
        this.message = Assert.requireNonNull(message, "message");
        this.sessionFilter = Assert.requireNonNull(sessionFilter, "sessionFilter");
    }

    public Message<T> getMessage() {
        return message;
    }

    public Topic<T> getTopic() {
        return getMessage().getTopic();
    }

    public String getSenderSessionId() {
        return senderSessionId;
    }

    public EventSessionFilter getSessionFilter() {
        return sessionFilter;
    }
}
