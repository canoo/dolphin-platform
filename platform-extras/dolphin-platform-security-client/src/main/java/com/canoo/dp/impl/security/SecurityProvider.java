package com.canoo.dp.impl.security;

import com.canoo.dp.impl.platform.client.AbstractServiceProvider;
import com.canoo.platform.client.ClientConfiguration;
import com.canoo.platform.client.security.Security;

import static com.canoo.platform.client.security.SecurityConfiguration.APP_NAME;
import static com.canoo.platform.client.security.SecurityConfiguration.AUTH_ENDPOINT;
import static com.canoo.platform.client.security.SecurityConfiguration.REALM_NAME;

public class SecurityProvider extends AbstractServiceProvider<Security> {

    public SecurityProvider() {
        super(Security.class);
    }

    @Override
    protected Security createService(ClientConfiguration configuration) {
        final String authEndpoint = configuration.getProperty(AUTH_ENDPOINT, null);
        final String realmName = configuration.getProperty(REALM_NAME, null);
        final String appName = configuration.getProperty(APP_NAME, null);
        return new KeycloakSecurity(authEndpoint, realmName, appName, configuration.getBackgroundExecutor());
    }
}
