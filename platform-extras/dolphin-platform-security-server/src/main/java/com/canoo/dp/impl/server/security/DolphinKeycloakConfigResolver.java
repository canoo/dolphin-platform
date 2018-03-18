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
import com.canoo.platform.server.security.SecurityException;
import org.apiguardian.api.API;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.representations.adapters.config.AdapterConfig;

import javax.servlet.http.HttpSession;
import java.util.Optional;
import java.util.function.Supplier;

import static com.canoo.dp.impl.security.SecurityHttpHeader.BEARER_ONLY_HEADER;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.19.0", status = INTERNAL)
public class DolphinKeycloakConfigResolver implements KeycloakConfigResolver {

    private final KeycloakConfiguration configuration;

    private final Supplier<HttpSession> sessionSupplier;

    public DolphinKeycloakConfigResolver(final KeycloakConfiguration configuration, final Supplier<HttpSession> sessionSupplier) {
        this.configuration = Assert.requireNonNull(configuration, "configuration");
        this.sessionSupplier = Assert.requireNonNull(sessionSupplier, "sessionSupplier");
    }

    public KeycloakDeployment resolve(final HttpFacade.Request request) {
        Assert.requireNonNull(request, "request");

        final String realmName = Optional.ofNullable(sessionSupplier.get())
                .map(s -> s.getAttribute(SecurityServerConstants.REALM_SESSION_ATTRIBUTE_NAME))
                .map(s -> s.toString())
                .orElse(configuration.getRealmName());

        final String applicationName = Optional.ofNullable(sessionSupplier.get())
                .map(s -> s.getAttribute(SecurityServerConstants.APP_SESSION_ATTRIBUTE_NAME))
                .map(s -> s.toString())
                .orElse(configuration.getApplicationName());

        final String authEndPoint = configuration.getAuthEndpoint();

        Optional.ofNullable(realmName).orElseThrow(() -> new SecurityException("Realm name for security check is not configured!"));
        Optional.ofNullable(applicationName).orElseThrow(() -> new SecurityException("Application name for security check is not configured!"));
        Optional.ofNullable(authEndPoint).orElseThrow(() -> new SecurityException("Auth endpoint for security check is not configured!"));

        final AdapterConfig adapterConfig = new AdapterConfig();

        if (configuration.isRealmAllowed(realmName)) {
            adapterConfig.setRealm(realmName);
        } else {
            throw new SecurityException("Access Denied! The given realm is not allowed.");
        }
        if (configuration.isApplicationAllowed(applicationName)) {
            adapterConfig.setResource(applicationName);
        } else {
            throw new SecurityException("Access Denied! The given application is not allowed.");
        }

        adapterConfig.setAuthServerUrl(authEndPoint);
        Optional.ofNullable(request.getHeader(BEARER_ONLY_HEADER)).
                ifPresent(v -> adapterConfig.setBearerOnly(true));
        return KeycloakDeploymentBuilder.build(adapterConfig);
    }
}
