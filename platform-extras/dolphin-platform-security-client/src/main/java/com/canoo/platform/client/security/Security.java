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
package com.canoo.platform.client.security;

import org.apiguardian.api.API;

import java.util.concurrent.Future;

@API(since = "0.19.0", status = API.Status.EXPERIMENTAL)
public interface Security {

    Future<Void> login(String user, String password, String... args);

    Future<Void> logout();

    boolean isAuthorized();

    @Deprecated
    String getAccessToken();

    @Deprecated
    long tokenExpiresAt();

    @Deprecated
    Future<Void> refreshToken();

    String getRealm();

    String getApp();
}
