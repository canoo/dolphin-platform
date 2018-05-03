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
package com.canoo.dp.impl.platform.core;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public interface PlatformConstants {

    String HASH_ALGORITHM = "MD5";

    String APPLICATION_CONTEXT = "application.name";

    String THREAD_CONTEXT = "thread";

    String UNNAMED_APPLICATION = "UNNAMED";

    String HOST_NAME_CONTEXT = "hostName";

    String CANONICAL_HOST_NAME_CONTEXT = "canonicalHostName";

    String HOST_ADDRESS_CONTEXT = "hostAddress";

    String APPLICATION_NAME_PROPERTY = "application.name";

    String ANSI_PROPERTY = "console.ansi";

    boolean ANSI_DEFAULT_VALUE = true;

    String APPLICATION_NAME_DEFAULT = "app";

    String REMOTING_DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    String TIMEZONE_UTC = "UTC";
    String CLIENT_ID_HTTP_HEADER_NAME = "X-Client-Session-Id";

    String THREAD_NAME_PREFIX = "Dolphin-Platform-Background-Thread-";

    String THREAD_GROUP_NAME = "Dolphin Platform executors";

    String PLATFORM_VERSION_CONTEXT = "platform.version";
}
