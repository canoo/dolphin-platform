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
package com.canoo.dp.impl.server.legacy.action;

import com.canoo.dp.impl.remoting.legacy.commands.Command;
import com.canoo.dp.impl.server.legacy.ServerAttribute;
import com.canoo.dp.impl.server.legacy.ServerModelStore;

import java.util.List;

/**
 * Common superclass for all actions that need access to
 * the ServerDolphin, e.g. to work with the server model store.
 */
public abstract class DolphinServerAction implements ServerAction {

    private ServerModelStore serverModelStore;

    private List<Command> dolphinResponse;

    public void presentationModel(final String id, final String presentationModelType, final List<ServerAttribute> attributes) {
        ServerModelStore.presentationModelCommand(dolphinResponse, id, presentationModelType, attributes);
    }

    public void changeValue(final ServerAttribute attribute, final String value) {
        ServerModelStore.changeValueCommand(dolphinResponse, attribute, value);
    }

    public ServerModelStore getServerModelStore() {
        return serverModelStore;
    }

    @Deprecated
    public void setServerModelStore(final ServerModelStore serverModelStore) {
        this.serverModelStore = serverModelStore;
    }

    public List<Command> getDolphinResponse() {
        return dolphinResponse;
    }

    public void setDolphinResponse(final List<Command> dolphinResponse) {
        this.dolphinResponse = dolphinResponse;
    }

}
