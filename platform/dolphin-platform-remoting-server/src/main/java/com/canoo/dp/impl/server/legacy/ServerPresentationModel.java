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
package com.canoo.dp.impl.server.legacy;

import com.canoo.dp.impl.remoting.legacy.RemotingConstants;
import com.canoo.dp.impl.remoting.legacy.core.PresentationModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ServerPresentationModel extends PresentationModel<ServerAttribute> {

    private static final Logger LOG = LoggerFactory.getLogger(ServerPresentationModel.class);

    private ServerModelStore modelStore;

    /**
     * @param id if id is null or empty, an auto-generated id will be used
     */
    public ServerPresentationModel(final String id, final List<ServerAttribute> attributes, final ServerModelStore serverModelStore) {
        super((id != null && id.length() > 0) ? id : makeId(serverModelStore), attributes);
        if (id != null && id.endsWith(RemotingConstants.SERVER_PM_AUTO_ID_SUFFIX)) {
            LOG.trace("Creating a PM with self-provided id '{}' even though it ends with a reserved suffix.", id);
        }
        modelStore = serverModelStore;
    }

    private static long pmInstanceCount = 0L;

    private static String makeId(final ServerModelStore serverModelStore) {
        long newId = pmInstanceCount++;
        return String.valueOf(newId) + RemotingConstants.SERVER_PM_AUTO_ID_SUFFIX;
    }

    public ServerModelStore getModelStore() {
        return modelStore;
    }

    public void setModelStore(final ServerModelStore modelStore) {
        this.modelStore = modelStore;
    }
}
