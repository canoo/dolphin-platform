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
package com.canoo.dp.impl.platform.core;

public interface PlatformConstants {
    String DOLPHIN_PLATFORM_PREFIX = "dolphin_platform_intern_";
    String REMOTING_DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    String TIMEZONE_UTC = "UTC";
    String CHARSET = "UTF-8";
    String CONTENT_TYPE_HEADER = "Content-Type";
    String ACCEPT_CHARSET_HEADER = "Accept-Charset";
    String ACCEPT_HEADER = "Accept";
    String COOKIE_HEADER = "Cookie";
    String SET_COOKIE_HEADER = "Set-Cookie";
    String POST_METHOD = "POST";
    String JSON_MIME_TYPE = "application/json;charset=utf-8";
    String CLIENT_ID_HTTP_HEADER_NAME = DOLPHIN_PLATFORM_PREFIX + "dolphinClientId";

}
