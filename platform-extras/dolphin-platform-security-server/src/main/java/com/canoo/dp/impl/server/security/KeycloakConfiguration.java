package com.canoo.dp.impl.server.security;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.core.PlatformConfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KeycloakConfiguration implements Serializable {

    public final static String SECURITY_ACTIVE_PROPERTY_NAME = "security.active";

    public final static boolean SECURITY_ACTIVE_PROPERTY_DEFAULT_VALUE = true;

    public final static String SECURITY_REALM_PROPERTY_NAME = "security.realm";

    public final static String SECURITY_REALM_PROPERTY_DEFAULT_VALUE = "myRealm";

    public final static String SECURITY_APPLICATION_PROPERTY_NAME = "security.application";

    public final static String SECURITY_APPLICATION_PROPERTY_DEFAULT_VALUE = "myApp";

    public final static String SECURITY_SERVER_URL_PROPERTY_NAME = "security.server";

    public final static String SECURITY_SERVER_URL_PROPERTY_DEFAULT_PROPERTY = "http://localhost:8080";

    public final static String SECURE_ENDPOINTS_PROPERTY_NAME = "security.endpoints";

    public final static String SECURE_ENDPOINTS_PROPERTY_DEFAULT_VALUE = "/dolphin";

    private final String realmName;

    private final boolean securityActive;

    private final String applicationName;

    private final String authEndpoint;

    private final List<String> secureEndpoints = new ArrayList<>();

    public KeycloakConfiguration(final PlatformConfiguration platformConfiguration) {
        Assert.requireNonNull(platformConfiguration, "platformConfiguration");
        this.realmName = platformConfiguration.getProperty(SECURITY_REALM_PROPERTY_NAME, null);
        this.applicationName = platformConfiguration.getProperty(SECURITY_APPLICATION_PROPERTY_NAME, null);
        this.authEndpoint = platformConfiguration.getProperty(SECURITY_SERVER_URL_PROPERTY_NAME, null);
        this.secureEndpoints.addAll(platformConfiguration.getListProperty(SECURE_ENDPOINTS_PROPERTY_NAME, Collections.emptyList()));
        this.securityActive = platformConfiguration.getBooleanProperty(SECURITY_ACTIVE_PROPERTY_NAME, false);
    }

    public String getRealmName() {
        return realmName;
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
}
