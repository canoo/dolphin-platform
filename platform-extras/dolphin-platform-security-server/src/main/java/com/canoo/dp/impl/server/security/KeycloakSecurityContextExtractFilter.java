package com.canoo.dp.impl.server.security;

import com.canoo.dp.impl.platform.core.Assert;
import org.keycloak.KeycloakSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class KeycloakSecurityContextExtractFilter implements Filter, AccessDeniedCallback {

    private final static Logger LOG = LoggerFactory.getLogger(KeycloakSecurityContextExtractFilter.class);

    private final ThreadLocal<KeycloakSecurityContext> contextHolder = new ThreadLocal<KeycloakSecurityContext>();

    private final ThreadLocal<Boolean> accessDenied = new ThreadLocal<Boolean>();

    private final KeyCloakSecurityExtractor keyCloakSecurityExtractor = new KeyCloakSecurityExtractor();

    public void init(final FilterConfig filterConfig) throws ServletException {}

    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        Assert.requireNonNull(chain, "chain");
        contextHolder.set(keyCloakSecurityExtractor.extractContext(request));
        accessDenied.set(false);
        try {
            chain.doFilter(request, response);
        }catch (Exception e) {
            if(!accessDenied.get()) {
                throw e;
            } else {
                LOG.error("SecurityContext error in request", e);
            }
        } finally {
            contextHolder.set(null);
            boolean sendAccessDenied = accessDenied.get();
            accessDenied.set(false);
            if(sendAccessDenied) {
                ((HttpServletResponse)response).sendError(403, "Access Denied");
            }
        }
    }

    public void destroy() {}

    public SecurityContextKeycloakImpl getSecurity() {
        return new SecurityContextKeycloakImpl(contextHolder.get(), this);
    }

    @Override
    public void onAccessDenied() {
        accessDenied.set(true);
    }
}
