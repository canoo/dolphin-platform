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
package com.canoo.dolphin.server.config;

import com.canoo.implementation.dolphin.server.config.ConfigurationFileLoader;
import com.canoo.implementation.dolphin.server.config.DolphinPlatformConfiguration;
import org.testng.annotations.Test;

import java.util.logging.Level;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * Created by hendrikebbers on 22.03.16.
 */
public class ConfigurationFileLoaderTest {

    @Test
    public void testConfigLoad() {
        try {
            //given:
            DolphinPlatformConfiguration configuration = ConfigurationFileLoader.load();

            //then:
            assertEquals(configuration.isUseCrossSiteOriginFilter(), false);
            assertEquals(configuration.getDolphinPlatformServletMapping(), "/test");
            assertEquals(configuration.getOpenDolphinLogLevel(), Level.FINER);
        } catch (Exception e) {
            fail();
        }
    }
}
