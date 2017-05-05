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
package com.canoo.dolphin.server.config;

import java.util.HashMap;
import java.util.Map;

public class TestConfigurationProvider implements ConfigurationProvider {

    public final static String PROPERTY_1_NAME = "testProperty1";

    public final static String PROPERTY_2_NAME = "testProperty2";

    public final static String PROPERTY_3_NAME = "testProperty3";

    public final static String PROPERTY_1_VALUE = "YEAH!";

    public final static String PROPERTY_2_VALUE = "JUHU";

    public final static String PROPERTY_3_VALUE = null;

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> ret = new HashMap<>();
        ret.put(PROPERTY_1_NAME, PROPERTY_1_VALUE);
        ret.put(PROPERTY_2_NAME, PROPERTY_2_VALUE);
        ret.put(PROPERTY_3_NAME, PROPERTY_3_VALUE);

        //This should not be overwritten from dolphin.properties
        ret.put(DolphinPlatformConfiguration.USE_CROSS_SITE_ORIGIN_FILTER, "true");
        return ret;
    }
}
