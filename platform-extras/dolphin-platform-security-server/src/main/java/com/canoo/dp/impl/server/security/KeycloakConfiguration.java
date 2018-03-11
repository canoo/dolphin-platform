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
package com.canoo.dp.impl.server.security;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.core.PlatformConfiguration;
import org.apiguardian.api.API;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.canoo.dp.impl.security.SecurityConfiguration.*;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.19.0", status = INTERNAL)
public class KeycloakConfiguration implements Serializable {

    public final static String SECURITY_ACTIVE_PROPERTY_NAME = "security.active";

    public final static boolean SECURITY_ACTIVE_PROPERTY_DEFAULT_VALUE = true;

    public final static String SECURE_ENDPOINTS_PROPERTY_NAME = "security.endpoints";

    public final static String LOGIN_ENDPOINTS_ACTIVE_PROPERTY_NAME = "security.loginEndpoint.active";

    public final static String LOGIN_ENDPOINTS_PROPERTY_NAME = "security.loginEndpoint";

    public final static String LOGIN_ENDPOINTS_PROPERTY_DEFAULT_VALUE = "/openid-connect";

    public final static String SECURE_ENDPOINTS_PROPERTY_DEFAULT_VALUE = "/dolphin";

    private final String realmName;

    private final List<String> realmNames;

    private final List<String> applicationNames;

    private final boolean securityActive;

    private final boolean loginEndpointActive;


    private final String applicationName;

    private final String authEndpoint;

    private final String loginEndpoint;

    private final List<String> secureEndpoints = new ArrayList<>();

    public KeycloakConfiguration(final PlatformConfiguration platformConfiguration) {
        Assert.requireNonNull(platformConfiguration, "platformConfiguration");

        this.realmName = platformConfiguration.getProperty(REALM_PROPERTY_NAME);
        this.realmNames = platformConfiguration.getListProperty(ALLOWED_REALMS_PROPERTY_NAME, Collections.emptyList());
//        if(this.realmName != null && !this.realmName.isEmpty()){
//            this.realmNames.add(this.realmName);
//        }
        this.applicationName = platformConfiguration.getProperty(APPLICATION_PROPERTY_NAME, APPLICATION_PROPERTY_DEFAULT_VALUE) ;
        this.applicationNames = platformConfiguration.getListProperty(ALLOWED_APPLICATIONS_PROPERTY_NAME, Collections.emptyList());
//        if(this.applicationName != null && !this.applicationName.isEmpty()){
//            this.applicationNames.add(this.applicationName);
//        }

        this.authEndpoint = platformConfiguration.getProperty(AUTH_ENDPOINT_PROPERTY_NAME, AUTH_ENDPOINT_PROPERTY_DEFAULT_VALUE) + "/auth";
        this.secureEndpoints.addAll(platformConfiguration.getListProperty(SECURE_ENDPOINTS_PROPERTY_NAME, Collections.emptyList()));
        this.securityActive = platformConfiguration.getBooleanProperty(SECURITY_ACTIVE_PROPERTY_NAME, false);
        this.loginEndpointActive = platformConfiguration.getBooleanProperty(LOGIN_ENDPOINTS_ACTIVE_PROPERTY_NAME, true);
        this.loginEndpoint = platformConfiguration.getProperty(LOGIN_ENDPOINTS_PROPERTY_NAME, LOGIN_ENDPOINTS_PROPERTY_DEFAULT_VALUE);
    }

    public String getRealmName() {
        return realmName;
    }

    public List<String> getRealmNames() {
        return realmNames;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getAuthEndpoint() {
        return authEndpoint;
    }

    public List<String> getSecureEndpoints() {
        return Collections.unmodifiableList(secureEndpoints);
    }

    public String[] getSecureEndpointsArray() {
        return secureEndpoints.toArray(new String[secureEndpoints.size()]);
    }

    public void setSecureEndpoints(final List<String> endpoints) {
        Assert.requireNonNull(endpoints, "endpoints");
        this.secureEndpoints.clear();
        this.secureEndpoints.addAll(endpoints);
    }

    public boolean isSecurityActive() {
        return securityActive;
    }

    public boolean isLoginEndpointActive() {
        return loginEndpointActive;
    }

    public String getLoginEndpoint() {
        return loginEndpoint;
    }

    public List<String> getApplicationNames() {
        return applicationNames;
    }

    public boolean isRealmAllowed(final String realmName){
        Assert.requireNonNull(realmName, "realmName");
        return getRealmNames().contains(realmName);
    }

    public boolean isApplicationAllowed(final String applicationName) {
        Assert.requireNonNull(applicationName, "applicationName");
        return getApplicationNames().contains(applicationName);
    }
}
