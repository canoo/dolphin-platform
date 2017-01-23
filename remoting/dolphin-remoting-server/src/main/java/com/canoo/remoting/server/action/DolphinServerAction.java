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
package com.canoo.remoting.server.action;

import com.canoo.communication.common.commands.Command;
import com.canoo.remoting.server.DTO;
import com.canoo.remoting.server.DefaultServerDolphin;
import com.canoo.remoting.server.ServerAttribute;

import java.util.List;

/**
 * Common superclass for all actions that need access to
 * the ServerDolphin, e.g. to work with the server model store.
 */
public abstract class DolphinServerAction implements ServerAction {

    private DefaultServerDolphin serverDolphin;

    private List<Command> dolphinResponse;

    public void presentationModel(String id, String presentationModelType, DTO dto) {
        DefaultServerDolphin.presentationModelCommand(dolphinResponse, id, presentationModelType, dto);
    }

    public void changeValue(ServerAttribute attribute, String value) {
        DefaultServerDolphin.changeValueCommand(dolphinResponse, attribute, value);
    }

    public DefaultServerDolphin getServerDolphin() {
        return serverDolphin;
    }

    public void setServerDolphin(DefaultServerDolphin serverDolphin) {
        this.serverDolphin = serverDolphin;
    }

    public List<Command> getDolphinResponse() {
        return dolphinResponse;
    }

    public void setDolphinResponse(List<Command> dolphinResponse) {
        this.dolphinResponse = dolphinResponse;
    }

}
