/*
 * Copyright 2015-2016 Canoo Engineering AG.
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
package com.canoo.remoting.combined

import com.canoo.remoting.client.ClientDolphin
import com.canoo.communication.common.commands.Command
import com.canoo.remoting.server.ServerConnector

class InMemoryClientConnectorTests extends GroovyTestCase {

    void testCallConnector_NoServerConnectorWired() {
        InMemoryClientConnector connector = new InMemoryClientConnector(new ClientDolphin(), null)
        assert [] == connector.transmit([new Command()])
    }

    void testCallConnector_ServerWired() {
        boolean serverCalled = false
        def serverConnector = [receive: { cmd ->
            serverCalled = true
            return []
        }] as ServerConnector
        InMemoryClientConnector connector = new InMemoryClientConnector(new ClientDolphin(), serverConnector)
        def command = new Command()
        connector.transmit([command])
        assert serverCalled
    }

    void testCallConnector_ServerWiredWithSleep() {
        boolean serverCalled = false
        def serverConnector = [receive: { cmd ->
            serverCalled = true
            return []
        }] as ServerConnector
        InMemoryClientConnector connector = new InMemoryClientConnector(new ClientDolphin(), serverConnector)
        connector.sleepMillis = 10
        def command = new Command()
        connector.transmit([command])
        assert serverCalled
    }

}
