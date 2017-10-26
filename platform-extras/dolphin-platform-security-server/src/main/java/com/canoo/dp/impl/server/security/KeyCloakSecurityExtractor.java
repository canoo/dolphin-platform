package com.canoo.dp.impl.server.security;

import com.canoo.dp.impl.platform.core.Assert;
import org.keycloak.KeycloakSecurityContext;

import javax.servlet.ServletRequest;

public class KeyCloakSecurityExtractor {

    public KeycloakSecurityContext extractContext(final ServletRequest request) {
        Assert.requireNonNull(request, "request");
        final KeycloakSecurityContext context = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        return context;
    }
}
