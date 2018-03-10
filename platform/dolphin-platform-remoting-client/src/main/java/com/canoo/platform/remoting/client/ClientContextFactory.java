/*
 * Copyright 2015-2018 Canoo Engineering AG.
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
package com.canoo.platform.remoting.client;

import com.canoo.platform.client.ClientConfiguration;
import org.apiguardian.api.API;

import java.net.URI;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Factory to create a {@link ClientContext}. Normally you will create a {@link ClientContext} at the bootstrap of your
 * client by using the {@link #create(ClientConfiguration, URI)} method and use this context as a singleton in your client.
 * The {@link ClientContext} defines the connection between the client and the Dolphin Platform server endpoint.
 */
@API(since = "0.x", status = EXPERIMENTAL)
@FunctionalInterface
public interface ClientContextFactory {

    /**
     * Create a {@link ClientContext} based on the given configuration.
     *
     * @param clientConfiguration the configuration
     * @param endpoint the server endpoint
     *
     * @return the context
     */
    ClientContext create(final ClientConfiguration clientConfiguration, final URI endpoint);
}
