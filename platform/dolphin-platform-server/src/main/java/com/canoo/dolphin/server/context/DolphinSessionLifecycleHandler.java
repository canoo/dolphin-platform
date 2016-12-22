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
package com.canoo.dolphin.server.context;

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.server.DolphinSession;
import com.canoo.dolphin.util.Callback;

/**
 * Created by hendrikebbers on 14.11.16.
 */
public interface DolphinSessionLifecycleHandler {

    Subscription addSessionCreatedListener(Callback<DolphinSession> listener);

    Subscription addSessionDestroyedListener(Callback<DolphinSession> listener);

    void onSessionCreated(final DolphinSession session);

    void onSessionDestroyed(final DolphinSession session);
}