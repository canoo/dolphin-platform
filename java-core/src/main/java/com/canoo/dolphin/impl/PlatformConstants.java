/*
 * Copyright 2015 Canoo Engineering AG.
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
package com.canoo.dolphin.impl;

/**
 * Created by hendrikebbers on 14.09.15.
 */
public interface PlatformConstants {

    public static final String DOLPHIN_PLATFORM_PREFIX = "dolphin_platform_intern_";

    public static final String RELEASE_COMMAND_NAME = DOLPHIN_PLATFORM_PREFIX + "release";

    public static final String POLL_COMMAND_NAME = DOLPHIN_PLATFORM_PREFIX + "longPoll";

    public static final String REGISTER_CONTROLLER_COMMAND_NAME = DOLPHIN_PLATFORM_PREFIX + "registerController";

    public static final String DESTROY_CONTROLLER_COMMAND_NAME = DOLPHIN_PLATFORM_PREFIX + "destroyController";

    public static final String CALL_CONTROLLER_ACTION_COMMAND_NAME = DOLPHIN_PLATFORM_PREFIX + "callControllerAction";

    public static final String INIT_COMMAND_NAME = DOLPHIN_PLATFORM_PREFIX + "initClientContext";

    public static final String CLIENT_ID_HTTP_HEADER_NAME = DOLPHIN_PLATFORM_PREFIX + "dolphin-client-id";

    public static final String DISCONNECT_COMMAND_NAME = DOLPHIN_PLATFORM_PREFIX + "disconnectClientContext";

    public static final String DOLPHIN_BEAN = "@@@ DOLPHIN_BEAN @@@";

    public static final String SOURCE_SYSTEM = "@@@ SOURCE_SYSTEM @@@";
    public static final String SOURCE_SYSTEM_CLIENT = "client";
    public static final String SOURCE_SYSTEM_SERVER = "server";
    public static final String JAVA_CLASS = "@@@ JAVA_CLASS @@@";

    public static final String LIST_ADD = "@@@ LIST_ADD @@@";
    public static final String LIST_DEL = "@@@ LIST_DEL @@@";
    public static final String LIST_SET = "@@@ LIST_SET @@@";

    public static final String CONTROLLER_ACTION_CALL_ERROR_BEAN_NAME = "@@@ CONTROLLER_ACTION_CALL_ERROR_BEAN @@@";

    public static final String CONTROLLER_ACTION_CALL_BEAN_NAME = "@@@ CONTROLLER_ACTION_CALL_BEAN @@@";

    public static final String CONTROLLER_ACTION_CALL_PARAM_BEAN_NAME = "@@@ CONTROLLER_ACTION_CALL_PARAM_BEAN @@@";

    public static final String CONTROLLER_DESTROY_BEAN_NAME = "@@@ CONTROLLER_DESTROY_BEAN @@@";

    public static final String CONTROLLER_REGISTRY_BEAN_NAME = "@@@ CONTROLLER_REGISTRY_BEAN @@@";
}
