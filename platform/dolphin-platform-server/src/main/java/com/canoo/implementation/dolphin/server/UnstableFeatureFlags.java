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
package com.canoo.implementation.dolphin.server;

/**
 * A helper class that can be used to define some feature flags for unstable features that can be activated for tests.
 */
public class UnstableFeatureFlags {

    /**
     * Defines if the DOlphin Bean garbage Collection should be active
     */
    private static boolean USE_GC = false;

    public static synchronized boolean isUseGc() {
        return USE_GC;
    }

    public static synchronized void setUseGc(boolean useGc) {
        USE_GC = useGc;
    }
}
