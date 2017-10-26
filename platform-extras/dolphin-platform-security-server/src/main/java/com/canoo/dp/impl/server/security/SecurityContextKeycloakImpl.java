package com.canoo.dp.impl.server.security;

import com.canoo.platform.server.security.SecurityContext;
import com.canoo.platform.server.security.SecurityException;
import com.canoo.platform.server.security.User;
import org.keycloak.KeycloakSecurityContext;

import java.util.Optional;

public class SecurityContextKeycloakImpl implements SecurityContext {

    private final User user;

    private final AccessDeniedCallback accessDeniedCallback;

    public SecurityContextKeycloakImpl(final KeycloakSecurityContext keycloakSecurityContext, final AccessDeniedCallback accessDeniedCallback) {
        this.user = Optional.ofNullable(keycloakSecurityContext).map(c -> new UserKeycloakImpl(keycloakSecurityContext)).orElse(null);
        this.accessDeniedCallback = accessDeniedCallback;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void accessDenied() {
        try {
            accessDeniedCallback.onAccessDenied();
        } finally {
            throw new SecurityException("Access Denied");
        }
    }
}
