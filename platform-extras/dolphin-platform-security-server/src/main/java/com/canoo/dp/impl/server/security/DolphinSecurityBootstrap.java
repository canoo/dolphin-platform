package com.canoo.dp.impl.server.security;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.core.PlatformConfiguration;
import com.canoo.platform.server.security.SecurityContext;
import org.keycloak.adapters.servlet.KeycloakOIDCFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import java.util.EnumSet;

public class DolphinSecurityBootstrap {

    private final static Logger LOG = LoggerFactory.getLogger(DolphinSecurityBootstrap.class);

    private final static String FILTER_NAME = "DolphinSecurityFilter";

    private final static String EXTRACTOR_FILTER_NAME = "KeycloakSecurityContextExtractFilter";

    private final static String KEYCLOAK_CONFIG_RESOLVER_PROPERTY_NAME = "keycloak.config.resolver";

    private final static DolphinSecurityBootstrap INSTANCE = new DolphinSecurityBootstrap();

    private KeycloakSecurityContextExtractFilter extractFilter;

    public void init(final ServletContext servletContext, final PlatformConfiguration configuration) {
        Assert.requireNonNull(configuration, "configuration");
        final KeycloakConfiguration keycloakConfiguration = new KeycloakConfiguration(configuration);
        if(keycloakConfiguration.isSecurityActive()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Adding security to the following endpoint: {}", keycloakConfiguration.getSecureEndpoints().stream().reduce("", (a, b) -> a + ", " + b));
            }
            this.extractFilter = new KeycloakSecurityContextExtractFilter();
            DolphinKeycloakConfigResolver.setConfiguration(keycloakConfiguration);
            if(!new DolphinKeycloakConfigResolver().resolve(null).isConfigured()) {
                LOG.error("SecurityContext is not configured!");
            }
            final FilterRegistration.Dynamic keycloakSecurityFilter = servletContext.addFilter(FILTER_NAME, new KeycloakOIDCFilter());
            keycloakSecurityFilter.setInitParameter(KEYCLOAK_CONFIG_RESOLVER_PROPERTY_NAME, DolphinKeycloakConfigResolver.class.getName());
            keycloakSecurityFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, keycloakConfiguration.getSecureEndpointsArray());
            final FilterRegistration.Dynamic keycloakExtractorFilter = servletContext.addFilter(EXTRACTOR_FILTER_NAME, extractFilter);
            keycloakExtractorFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        }
    }

    public SecurityContext getSecurityForCurrentRequest() {
        return extractFilter.getSecurity();
    }

    public static DolphinSecurityBootstrap getInstance() {
        return INSTANCE;
    }
}
