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

import com.canoo.impl.platform.core.Assert;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.CommandConstants;

public final class DestroyControllerCommand extends Command {

    private String controllerId;

    public DestroyControllerCommand() {
        super(CommandConstants.DESTROY_CONTROLLER_COMMAND_ID);
    }

    public String getControllerId() {
        return controllerId;
    }

    public void setControllerId(final String controllerId) {
        Assert.requireNonBlank(controllerId, "controllerId");
        this.controllerId = controllerId;
    }
}

