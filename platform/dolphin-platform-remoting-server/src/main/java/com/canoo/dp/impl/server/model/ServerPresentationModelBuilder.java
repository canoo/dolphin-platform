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
package com.canoo.dp.impl.server.model;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.remoting.AbstractPresentationModelBuilder;
import com.canoo.dp.impl.remoting.legacy.RemotingConstants;
import com.canoo.dp.impl.server.legacy.ServerAttribute;
import com.canoo.dp.impl.server.legacy.ServerDolphin;
import com.canoo.dp.impl.server.legacy.ServerPresentationModel;

import java.util.ArrayList;
import java.util.List;

public class ServerPresentationModelBuilder extends AbstractPresentationModelBuilder<ServerPresentationModel> {

    private final List<ServerAttribute> attributes = new ArrayList<>();
    private final ServerDolphin dolphin;

    public ServerPresentationModelBuilder(ServerDolphin dolphin) {
        Assert.requireNonNull(dolphin, "dolphin");
        this.dolphin = dolphin;
        withAttribute(RemotingConstants.SOURCE_SYSTEM, RemotingConstants.SOURCE_SYSTEM_SERVER);
    }


    @Override
    public ServerPresentationModelBuilder withAttribute(String name, Object value) {
        attributes.add(new ServerAttribute(name, value));
        return this;
    }


    @Override
    public ServerPresentationModel create() {
        return dolphin.getModelStore().presentationModel(id, type, attributes);
    }

}
