package com.canoo.dp.impl.server.security;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.server.security.User;
import org.keycloak.KeycloakSecurityContext;

import java.util.stream.Stream;

public class UserKeycloakImpl implements User {

    private final KeycloakSecurityContext keycloakSecurityContext;

    public UserKeycloakImpl(KeycloakSecurityContext keycloakSecurityContext) {
        this.keycloakSecurityContext = Assert.requireNonNull(keycloakSecurityContext, "keycloakSecurityContext");
    }

    @Override
    public Stream<String> getRoles() {
        return keycloakSecurityContext.getToken().getRealmAccess().getRoles().stream();
    }

    @Override
    public String getEmail() {
        return keycloakSecurityContext.getToken().getEmail();
    }

    @Override
    public String getUserName() {
        return keycloakSecurityContext.getToken().getPreferredUsername();
    }

    @Override
    public String getFirstName() {
        return keycloakSecurityContext.getToken().getGivenName();
    }

    @Override
    public String getLastName() {
        return keycloakSecurityContext.getToken().getFamilyName();
    }

    @Override
    public String toString() {
        return "User " + getUserName() + " [first name:" + getFirstName() + ", last name:" + getLastName() + ", mail:" + getEmail() + "] Roles:" + getRoles().reduce("", (a,b) -> a+ ", " +b);
    }
}
