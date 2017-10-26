package com.canoo.platform.server.security;

import org.apiguardian.api.API;

@API(since = "0.19.0", status = API.Status.EXPERIMENTAL)
public class SecurityException extends RuntimeException {

    public SecurityException(final String message) {
        super(message);
    }

    public SecurityException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
