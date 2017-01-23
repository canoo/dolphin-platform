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
package com.canoo.remoting.client;

import com.canoo.communication.common.BasePresentationModel;

import java.util.List;

public final class ClientPresentationModel extends BasePresentationModel<ClientAttribute> {

    public final static String AUTO_ID_SUFFIX = "-AUTO-CLT";

    private boolean clientSideOnly = false;

    private static long instanceCount = 0;

    public ClientPresentationModel(List<ClientAttribute> attributes) {
        this(null, attributes);
    }

    /**
     * @param id if id is null or empty, an auto-generated id will be used
     */
    public ClientPresentationModel(String id, List<ClientAttribute> attributes) {
        super(createUniqueId(id), attributes);
        if (id != null && id.endsWith(AUTO_ID_SUFFIX)) {
            throw new IllegalArgumentException("presentation model with self-provided id \'" + id + "\' may not end with suffix \'" + AUTO_ID_SUFFIX + "\' since that is reserved.");
        }
    }

    private static String createUniqueId(String id) {
        return (id != null && id.length() > 0) ? id : "" + instanceCount++ + AUTO_ID_SUFFIX;
    }

    public boolean isClientSideOnly() {
        return clientSideOnly;
    }

    public void setClientSideOnly(boolean clientSideOnly) {
        this.clientSideOnly = clientSideOnly;
    }

}
