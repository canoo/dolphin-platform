package com.canoo.platform.server.security;

import org.apiguardian.api.API;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@API(since = "0.19.0", status = API.Status.EXPERIMENTAL)
public interface SecurityContext {

    String[] EMPTY_STRING_ARRAY = {};

    default boolean hasRole(final String role) {
        return Optional.ofNullable(getUser()).
                map(u -> u.getRoles()).
                map(s -> s.collect(Collectors.toSet())).
                map(l -> l.contains(role)).
                orElse(false);
    }

    default void requireRole(final String role) {
        if (!hasRole(role)) {
            accessDenied();
        }
    }

    default void ifRole(final String role, final Runnable r) {
        if (hasRole(role)) {
            r.run();
        }
    }

    default boolean hasAnyRole(final String... roles) {
        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    default void requireAnyRole(final String... roles) {
        if (!hasAnyRole(roles)) {
            accessDenied();
        }
    }

    default void ifAnyRole(final String[] roles, final Runnable r) {
        if (hasAnyRole(roles)) {
            r.run();
        }
    }

    default void ifAnyRole(final Collection<String> roles, final Runnable r) {
        if (hasAnyRole(roles.toArray(EMPTY_STRING_ARRAY))) {
            r.run();
        }
    }

    default boolean hasAllRoles(final String... roles) {
        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    default void requireAllRoles(final String... roles) {
        if (!hasAllRoles(roles)) {
            accessDenied();
        }
    }

    default void ifAllRoles(final String[] roles, final Runnable r) {
        if (hasAllRoles(roles)) {
            r.run();
        }
    }

    default void ifAllRoles(final Collection<String> roles, final Runnable r) {
        if (hasAllRoles(roles.toArray(EMPTY_STRING_ARRAY))) {
            r.run();
        }
    }

    default boolean isUser(final String user) {
        return Optional.ofNullable(getUser()).
                map(u -> u.getUserName()).
                map(u -> u.equals(user)).orElse(false);
    }

    default void requireUser(final String user) {
        if (!isUser(user)) {
           accessDenied();
        }
    }

    default void ifUser(final String user, final Runnable r) {
        if (isUser(user)) {
            r.run();
        }
    }

    default boolean hasUser() {
        return Optional.ofNullable(getUser()).isPresent();
    }

    User getUser();

    void accessDenied();
}
