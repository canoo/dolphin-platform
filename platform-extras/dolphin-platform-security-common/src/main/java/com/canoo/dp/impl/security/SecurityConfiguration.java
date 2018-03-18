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
package com.canoo.dp.impl.security;

import org.apiguardian.api.API;

@API(since = "0.19.0", status = API.Status.EXPERIMENTAL)
public interface SecurityConfiguration {

    String AUTH_ENDPOINT_PROPERTY_NAME = "security.keycloak.endpoint";

    String AUTH_ENDPOINT_PROPERTY_DEFAULT_VALUE = "http://localhost:8080/openid-connect";

    String REALM_PROPERTY_NAME = "security.keycloak.realm";

    String ALLOWED_REALMS_PROPERTY_NAME = "security.keycloak.allowedRealms";

    String REALM_PROPERTY_DEFAULT_VALUE = "myRealm";

    String APPLICATION_PROPERTY_NAME = "security.keycloak.app";

    String ALLOWED_APPLICATIONS_PROPERTY_NAME = "security.keycloak.allowedApplications";

    String DIRECT_CONNECTION_PROPERTY_NAME = "security.useDirectConnection";

    boolean DIRECT_CONNECTION_PROPERTY_DEFAULT_VALUE = false;

    String APPLICATION_PROPERTY_DEFAULT_VALUE = "myApp";
}
