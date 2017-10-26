package com.canoo.platform.server.security;

import org.apiguardian.api.API;

import java.security.Principal;
import java.util.Optional;
import java.util.stream.Stream;

@API(since = "0.19.0", status = API.Status.EXPERIMENTAL)
public interface User extends Principal {

    String getUserName();

    Stream<String> getRoles();

    String getEmail();

    default Optional<String> email() {
        return Optional.ofNullable(getEmail());
    }

    String getFirstName();

    default Optional<String> firstName() {
        return Optional.ofNullable(getFirstName());
    }

    String getLastName();

    default Optional<String> lastName() {
        return Optional.ofNullable(getLastName());
    }

    default String getName() {
        return getUserName();
    }
}
