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
package com.canoo.impl.server.model;

import com.canoo.dolphin.impl.AbstractPresentationModelBuilder;
import com.canoo.dp.impl.platform.core.Assert;
import org.opendolphin.RemotingConstants;
import org.opendolphin.core.server.DTO;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerPresentationModel;
import org.opendolphin.core.server.Slot;

import java.util.ArrayList;
import java.util.List;

public class ServerPresentationModelBuilder extends AbstractPresentationModelBuilder<ServerPresentationModel> {

    private final List<Slot> slots = new ArrayList<>();
    private final ServerDolphin dolphin;

    public ServerPresentationModelBuilder(ServerDolphin dolphin) {
        Assert.requireNonNull(dolphin, "dolphin");
        this.dolphin = dolphin;
        this.slots.add(new Slot(RemotingConstants.SOURCE_SYSTEM, RemotingConstants.SOURCE_SYSTEM_SERVER));
    }

    @Override
    public ServerPresentationModelBuilder withAttribute(String name) {
        slots.add(new Slot(name, null));
        return this;
    }

    @Override
    public ServerPresentationModelBuilder withAttribute(String name, Object value) {
        slots.add(new Slot(name, value));
        return this;
    }

    @Override
    public ServerPresentationModelBuilder withAttribute(String name, Object value, String qualifier) {
        slots.add(new Slot(name, value, qualifier));
        return this;
    }

    @Override
    public ServerPresentationModel create() {
        return dolphin.getModelStore().presentationModel(id, type, new DTO(slots));
    }

}
