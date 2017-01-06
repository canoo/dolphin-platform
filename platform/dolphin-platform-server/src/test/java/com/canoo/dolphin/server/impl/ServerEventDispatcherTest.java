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
package com.canoo.dolphin.server.impl;

import com.canoo.implementation.dolphin.PlatformConstants;
import com.canoo.implementation.dolphin.server.context.DefaultOpenDolphinFactory;
import com.canoo.implementation.dolphin.server.context.OpenDolphinFactory;
import com.canoo.implementation.dolphin.server.ServerEventDispatcher;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by hendrikebbers on 18.03.16.
 */
public class ServerEventDispatcherTest {

    @Test
    public void testLocalSystemIdentifier() {
        //given:
        OpenDolphinFactory factory = new DefaultOpenDolphinFactory();
        ServerEventDispatcher dispatcher = new ServerEventDispatcher(factory.create());

        //then:
        assertEquals(dispatcher.getLocalSystemIdentifier(), PlatformConstants.SOURCE_SYSTEM_SERVER);
    }
}
