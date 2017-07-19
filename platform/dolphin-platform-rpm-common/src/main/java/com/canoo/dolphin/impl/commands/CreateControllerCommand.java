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
package com.canoo.dolphin.impl.commands;

import com.canoo.dolphin.impl.PlatformRemotingConstants;
import com.canoo.impl.platform.core.Assert;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.CommandConstants;

public final class CreateControllerCommand extends Command {

    private String parentControllerId;

    private String controllerName;

    public CreateControllerCommand() {
        super(CommandConstants.CREATE_CONTROLLER_COMMAND_ID);
    }

    public String getParentControllerId() {
        return parentControllerId;
    }

    public void setParentControllerId(final String parentControllerId) {
        this.parentControllerId = parentControllerId;
    }

    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(final String controllerName) {
        Assert.requireNonBlank(controllerName, "controllerName");
        this.controllerName = controllerName;
    }
}
