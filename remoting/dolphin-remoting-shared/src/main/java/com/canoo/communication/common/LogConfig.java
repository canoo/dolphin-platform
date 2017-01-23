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
package com.canoo.communication.common;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Keep logging details in one place
 **/
public class LogConfig {

    private static final Logger DOLPHIN_LOGGER = Logger.getLogger("com.canoo.communication");

    public static void logOnLevel(Level level) {
        logOnLevel(DOLPHIN_LOGGER, level);
    }

    /**
     * One may choose to use DOLPHIN_LOGGER or ROOT_LOGGER.
     */
    public static void logOnLevel(Logger logger, final Level level) {
        logger.setLevel(level);
        for(Handler handler : logger.getHandlers()) {
            handler.setLevel(level);
        }
    }
}

