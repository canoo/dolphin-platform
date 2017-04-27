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
package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.impl.AbstractPresentationModelBuilder;
import com.canoo.dolphin.util.Assert;
import org.opendolphin.RemotingConstants;
import org.opendolphin.core.client.ClientAttribute;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.ClientPresentationModel;

import java.util.ArrayList;
import java.util.List;

public class ClientPresentationModelBuilder extends AbstractPresentationModelBuilder<ClientPresentationModel> {

    private final List<ClientAttribute> attributes = new ArrayList<>();
    private final ClientModelStore modelStore;

    public ClientPresentationModelBuilder(final ClientModelStore modelStore) {
        this.modelStore = Assert.requireNonNull(modelStore, "modelStore");
        attributes.add(new ClientAttribute(RemotingConstants.SOURCE_SYSTEM, RemotingConstants.SOURCE_SYSTEM_CLIENT));
    }

    @Override
    public ClientPresentationModelBuilder withAttribute(String name) {
        attributes.add(new ClientAttribute(name, null));
        return this;
    }

    @Override
    public ClientPresentationModelBuilder withAttribute(String name, Object value) {
        attributes.add(new ClientAttribute(name, value));
        return this;
    }

    @Override
    public ClientPresentationModelBuilder withAttribute(String name, Object value, String qualifier) {
        attributes.add(new ClientAttribute(name, value, qualifier));
        return this;
    }

    @Override
    public ClientPresentationModel create() {
        ClientPresentationModel result = new ClientPresentationModel(id, attributes);
        result.setPresentationModelType(type);
        modelStore.add(result);
        return result;
    }

}
