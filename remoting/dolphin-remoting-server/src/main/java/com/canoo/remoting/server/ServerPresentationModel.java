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
package com.canoo.remoting.server;

import com.canoo.communication.common.BasePresentationModel;

import java.util.List;
import java.util.logging.Logger;

public class ServerPresentationModel extends BasePresentationModel<ServerAttribute> {

    private static final Logger LOG = Logger.getLogger(ServerPresentationModel.class.getName());

    public static final String AUTO_ID_SUFFIX = "-AUTO-SRV";

    private ServerModelStore modelStore;

    /**
     * @param id if id is null or empty, an auto-generated id will be used
     */
    public ServerPresentationModel(String id, List<ServerAttribute> attributes, ServerModelStore serverModelStore) {
        super((id != null && id.length() > 0) ? id : makeId(serverModelStore), attributes);
        if (id != null && id.endsWith(AUTO_ID_SUFFIX)) {
            LOG.info("Creating a PM with self-provided id \'" + id + "\' even though it ends with a reserved suffix.");
        }
        modelStore = serverModelStore;
    }

    private static String makeId(ServerModelStore serverModelStore) {
        long newId = serverModelStore.pmInstanceCount++;
        return String.valueOf(newId) + AUTO_ID_SUFFIX;
    }

    public void addAttribute(ServerAttribute attribute) {
        _internal_addAttribute(attribute);
        modelStore.registerAttribute(attribute);
        DefaultServerDolphin.initAt(modelStore.getCurrentResponse(), getId(), attribute.getPropertyName(), attribute.getQualifier(), attribute.getValue());
    }

    public ServerModelStore getModelStore() {
        return modelStore;
    }

    public void setModelStore(ServerModelStore modelStore) {
        this.modelStore = modelStore;
    }
}
