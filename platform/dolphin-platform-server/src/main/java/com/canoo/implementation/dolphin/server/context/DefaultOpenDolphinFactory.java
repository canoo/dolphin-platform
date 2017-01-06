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
package com.canoo.implementation.dolphin.server.context;

import com.canoo.implementation.dolphin.codec.OptimizedJsonCodec;
import org.opendolphin.core.server.DefaultServerDolphin;
import org.opendolphin.core.server.ServerConnector;
import org.opendolphin.core.server.ServerModelStore;

/**
 * Default implementation of the {@link OpenDolphinFactory}
 */
public class DefaultOpenDolphinFactory implements OpenDolphinFactory {

    @Override
    public DefaultServerDolphin create() {
        //Init Open Dolphin
        final ServerModelStore modelStore = new ServerModelStore();
        final ServerConnector serverConnector = new ServerConnector();
        serverConnector.setCodec(new OptimizedJsonCodec());
        serverConnector.setServerModelStore(modelStore);
        final DefaultServerDolphin dolphin = new DefaultServerDolphin(modelStore, serverConnector);
        dolphin.registerDefaultActions();
        return dolphin;
    }
}
