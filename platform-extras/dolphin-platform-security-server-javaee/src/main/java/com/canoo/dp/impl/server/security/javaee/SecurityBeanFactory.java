package com.canoo.dp.impl.server.security.javaee;

import com.canoo.dp.impl.server.security.DolphinSecurityBootstrap;
import com.canoo.platform.server.security.SecurityContext;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class SecurityBeanFactory {

    @Produces
    @RequestScoped
    public SecurityContext createDolphinSession() {
        return DolphinSecurityBootstrap.getInstance().getSecurityForCurrentRequest();
    }
}
