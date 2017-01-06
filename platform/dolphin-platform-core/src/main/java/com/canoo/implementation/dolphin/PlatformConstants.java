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
package com.canoo.implementation.dolphin;

public interface PlatformConstants {

    String DOLPHIN_PLATFORM_PREFIX = "dolphin_platform_intern_";

    String RELEASE_EVENT_BUS_COMMAND_NAME = DOLPHIN_PLATFORM_PREFIX + "release";

    String POLL_EVENT_BUS_COMMAND_NAME = DOLPHIN_PLATFORM_PREFIX + "longPoll";

    String REGISTER_CONTROLLER_COMMAND_NAME = DOLPHIN_PLATFORM_PREFIX + "registerController";

    String DESTROY_CONTROLLER_COMMAND_NAME = DOLPHIN_PLATFORM_PREFIX + "destroyController";

    String CALL_CONTROLLER_ACTION_COMMAND_NAME = DOLPHIN_PLATFORM_PREFIX + "callControllerAction";

    String INIT_CONTEXT_COMMAND_NAME = DOLPHIN_PLATFORM_PREFIX + "initClientContext";

    String CLIENT_ID_HTTP_HEADER_NAME = DOLPHIN_PLATFORM_PREFIX + "dolphinClientId";

    String DESTROY_CONTEXT_COMMAND_NAME = DOLPHIN_PLATFORM_PREFIX + "disconnectClientContext";

    String GARBAGE_COLLECTION_COMMAND_NAME = DOLPHIN_PLATFORM_PREFIX + "garbageCollection";

    String DOLPHIN_BEAN = "@@@ DOLPHIN_BEAN @@@";

    String SOURCE_SYSTEM = "@@@ SOURCE_SYSTEM @@@";
    String SOURCE_SYSTEM_CLIENT = "client";
    String SOURCE_SYSTEM_SERVER = "server";
    String JAVA_CLASS = "@@@ JAVA_CLASS @@@";

    String LIST_SPLICE = "@DP:LS@";

    String CONTROLLER_ACTION_CALL_BEAN_NAME = "@@@ CONTROLLER_ACTION_CALL_BEAN @@@";
    String INTERNAL_ATTRIBUTES_BEAN_NAME = "@@@ HIGHLANDER_BEAN @@@";
}
