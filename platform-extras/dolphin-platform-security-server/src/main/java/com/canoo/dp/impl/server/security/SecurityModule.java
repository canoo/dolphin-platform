package com.canoo.dp.impl.server.security;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.server.spi.AbstractBaseModule;
import com.canoo.platform.server.spi.ModuleDefinition;
import com.canoo.platform.server.spi.ModuleInitializationException;
import com.canoo.platform.server.spi.ServerCoreComponents;

@ModuleDefinition(SecurityModule.MODULE_NAME)
public class SecurityModule extends AbstractBaseModule {

    public final static String MODULE_NAME = "SecurityModule";

    private final static String MODULE_ACTIVE_PROPERTY= "security.active";

    @Override
    public void initialize(final ServerCoreComponents coreComponents) throws ModuleInitializationException {
        Assert.requireNonNull(coreComponents, "coreComponents");
        final KeycloakConfiguration configuration = new KeycloakConfiguration(coreComponents.getConfiguration());
        final DolphinSecurityBootstrap bootstrap = DolphinSecurityBootstrap.getInstance();
        bootstrap.init(coreComponents.getServletContext(), coreComponents.getConfiguration());
    }

    @Override
    protected String getActivePropertyName() {
        return MODULE_ACTIVE_PROPERTY;
    }
}
