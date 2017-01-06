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
package com.canoo.implementation.dolphin.server;

import com.canoo.implementation.dolphin.PlatformConstants;
import com.canoo.implementation.dolphin.EventDispatcherImpl;
import org.opendolphin.core.Dolphin;

public class ServerEventDispatcher extends EventDispatcherImpl {

    public ServerEventDispatcher(Dolphin dolphin) {
        super(dolphin);
    }

    @Override
    public String getLocalSystemIdentifier() {
        return PlatformConstants.SOURCE_SYSTEM_SERVER;
    }
}
