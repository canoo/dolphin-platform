package com.canoo.dp.impl.server.security.spring;

import com.canoo.dp.impl.server.security.DolphinSecurityBootstrap;
import com.canoo.platform.server.security.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class SecurityBeanFactory {

    @Bean("security")
    @RequestScope
    public SecurityContext createDolphinSession() {
        return DolphinSecurityBootstrap.getInstance().getSecurityForCurrentRequest();
    }
}
