package com.canoo.dp.impl.server.security;

import com.canoo.platform.server.spi.ConfigurationProvider;
import org.apiguardian.api.API;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.19.0", status = INTERNAL)
public class SecurityDefaultValueProvider implements ConfigurationProvider {

    @Override
    public Map<String, String> getStringProperties() {
        HashMap<String, String> ret = new HashMap<>();
        ret.put(KeycloakConfiguration.SECURITY_REALM_PROPERTY_NAME, KeycloakConfiguration.SECURITY_REALM_PROPERTY_DEFAULT_VALUE);
        ret.put(KeycloakConfiguration.SECURITY_APPLICATION_PROPERTY_NAME, KeycloakConfiguration.SECURITY_APPLICATION_PROPERTY_DEFAULT_VALUE);
        ret.put(KeycloakConfiguration.SECURITY_SERVER_URL_PROPERTY_NAME, KeycloakConfiguration.SECURITY_SERVER_URL_PROPERTY_DEFAULT_PROPERTY);
        return ret;
    }

    @Override
    public Map<String, Boolean> getBooleanProperties() {
        return Collections.singletonMap(KeycloakConfiguration.SECURITY_ACTIVE_PROPERTY_NAME, KeycloakConfiguration.SECURITY_ACTIVE_PROPERTY_DEFAULT_VALUE);
    }

    @Override
    public Map<String, List<String>> getListProperties() {
        return Collections.singletonMap(KeycloakConfiguration.SECURE_ENDPOINTS_PROPERTY_NAME, Collections.<String>singletonList(KeycloakConfiguration.SECURE_ENDPOINTS_PROPERTY_DEFAULT_VALUE));
    }
}
