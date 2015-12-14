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
 * A handler that can be registered to the dolphin message bus (see {@link com.canoo.dolphin.server.event.DolphinEventBus})
 * to receive publish messages (see {@link com.canoo.dolphin.server.event.Message}).
 */
public interface MessageListener<T> {

    /**
     * Method will be called whenever a message is received
     * @param message
     */
    void onMessage(Message<T> message);
}
