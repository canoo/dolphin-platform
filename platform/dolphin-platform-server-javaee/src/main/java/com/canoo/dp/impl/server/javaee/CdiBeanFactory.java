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
package com.canoo.dp.impl.server.javaee;

import com.canoo.dp.impl.server.bootstrap.PlatformBootstrap;
import com.canoo.dp.impl.server.client.ClientSessionProvider;
import com.canoo.platform.core.DolphinRuntimeException;
import com.canoo.platform.server.client.ClientSession;
import com.canoo.platform.server.javaee.ClientScoped;
import org.apiguardian.api.API;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * Factory that provides all needed Dolphin Platform extensions as CDI beans.
 *
 * @author Hendrik Ebbers
 */
@ApplicationScoped
@API(since = "0.x", status = INTERNAL)
public class CdiBeanFactory {

    @Produces
    @ClientScoped
    public ClientSession createDolphinSession() {
        return PlatformBootstrap.getServerCoreComponents().
                getInstance(ClientSessionProvider.class).
                map(p -> p.getCurrentClientSession()).
                orElseThrow(() -> new DolphinRuntimeException("Can not provide " + ClientSession.class));
    }

}
