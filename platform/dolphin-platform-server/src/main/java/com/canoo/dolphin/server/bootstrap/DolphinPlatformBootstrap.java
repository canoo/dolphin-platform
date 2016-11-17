/*
 * Copyright 2015-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dolphin.server.bootstrap;

import com.canoo.dolphin.server.DolphinSession;
import com.canoo.dolphin.server.config.DolphinPlatformConfiguration;
import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.context.DefaultDolphinContextFactory;
import com.canoo.dolphin.server.context.DolphinContext;
import com.canoo.dolphin.server.context.DolphinContextCommunicationHandler;
import com.canoo.dolphin.server.context.DolphinContextFactory;
import com.canoo.dolphin.server.context.DolphinContextFilter;
import com.canoo.dolphin.server.context.DolphinContextProvider;
import com.canoo.dolphin.server.context.DolphinContextUtils;
import com.canoo.dolphin.server.context.DolphinHttpSessionListener;
import com.canoo.dolphin.server.context.DolphinSessionListenerProvider;
import com.canoo.dolphin.server.event.impl.DolphinEventBusImpl;
import com.canoo.dolphin.server.impl.ClasspathScanner;
import com.canoo.dolphin.server.mbean.MBeanRegistry;
import com.canoo.dolphin.server.servlet.CrossSiteOriginFilter;
import com.canoo.dolphin.server.servlet.DolphinPlatformServlet;
import com.canoo.dolphin.server.servlet.HealthCheckServlet;
import com.canoo.dolphin.util.Assert;
import org.opendolphin.server.adapter.InvalidationServlet;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.logging.Logger;

/**
 * This class defines the bootstrap for Dolphin Platform.
 */
public class DolphinPlatformBootstrap implements DolphinContextProvider {

    private static final DolphinPlatformBootstrap INSTANCE = new DolphinPlatformBootstrap();

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(DolphinPlatformBootstrap.class);

    public static final String DOLPHIN_SERVLET_NAME = "dolphin-platform-servlet";

    public static final String DOLPHIN_CROSS_SITE_FILTER_NAME = "dolphinCrossSiteFilter";

    public static final String DOLPHIN_HEALTH_CHECK_SERVLET_NAME = "dolphinHealthCheckServlet";

    public static final String DOLPHIN_INVALIDATION_SERVLET_NAME = "dolphin-platform-invalidation-servlet";

    public static final String DOLPHIN_CLIENT_ID_FILTER_NAME = "dolphin-platform-client-id-filter";

    public static final String DEFAULT_DOLPHIN_INVALIDATION_SERVLET_MAPPING = "/dolphininvalidate";

    private final DolphinEventBusImpl dolphinEventBus;

    private boolean up = false;

    private DolphinPlatformBootstrap() {
        dolphinEventBus = new DolphinEventBusImpl(this);
    }

    /**
     * This methods starts the Dolphin Platform server runtime
     *
     * @param servletContext the servlet context
     */
    public void start(ServletContext servletContext, DolphinPlatformConfiguration configuration) {
        Assert.requireNonNull(servletContext, "servletContext");
        Assert.requireNonNull(configuration, "configuration");

        LOG.debug("Dolphin Platform starts with value for useCrossSiteOriginFilter=" + configuration.isUseCrossSiteOriginFilter());
        LOG.debug("Dolphin Platform starts with value for dolphinPlatformServletMapping=" + configuration.getDolphinPlatformServletMapping());
        LOG.debug("Dolphin Platform starts with value for openDolphinLogLevel=" + configuration.getOpenDolphinLogLevel());

        final ClasspathScanner classpathScanner = new ClasspathScanner(configuration.getRootPackageForClasspathScan());

        MBeanRegistry.getInstance().setMbeanSupport(configuration.isMBeanRegistration());

        final ContainerManager containerManager = findManager();
        containerManager.init(servletContext);

        final DolphinContextCommunicationHandler communicationHandler = new DolphinContextCommunicationHandler(configuration, this);

        final DolphinSessionListenerProvider dolphinSessionListenerProvider = new DolphinSessionListenerProvider(containerManager, classpathScanner);

        DolphinContextFactory dolphinContextFactory = new DefaultDolphinContextFactory(containerManager, dolphinEventBus, classpathScanner);
        servletContext.addServlet(DOLPHIN_SERVLET_NAME, new DolphinPlatformServlet(communicationHandler)).addMapping(configuration.getDolphinPlatformServletMapping());
        if (configuration.isUseSessionInvalidationServlet()) {
            servletContext.addServlet(DOLPHIN_INVALIDATION_SERVLET_NAME, new InvalidationServlet()).addMapping(DEFAULT_DOLPHIN_INVALIDATION_SERVLET_MAPPING);
        }
        if (configuration.isUseCrossSiteOriginFilter()) {
            servletContext.addFilter(DOLPHIN_CROSS_SITE_FILTER_NAME, new CrossSiteOriginFilter()).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        }
        if (configuration.isUseHealthCheck()) {
            servletContext.addServlet(DOLPHIN_HEALTH_CHECK_SERVLET_NAME, new HealthCheckServlet()).addMapping(configuration.getDolphinHealthCheckServletMapping());
        }

        servletContext.addFilter(DOLPHIN_CLIENT_ID_FILTER_NAME, new DolphinContextFilter(configuration, containerManager, dolphinContextFactory, dolphinSessionListenerProvider)).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, configuration.getIdFilterUrlMappings().toArray(new String[configuration.getIdFilterUrlMappings().size()]));

        LOG.debug("Dolphin Platform initialized under context \"" + servletContext.getContextPath() + "\"");
        LOG.debug("Dolphin Platform endpoint defined as " + configuration.getDolphinPlatformServletMapping());

        DolphinHttpSessionListener contextCleaner = new DolphinHttpSessionListener();
        contextCleaner.init(configuration);
        servletContext.addListener(contextCleaner);

        java.util.logging.Logger openDolphinLogger = Logger.getLogger("org.opendolphin");
        openDolphinLogger.setLevel(configuration.getOpenDolphinLogLevel());
        up = true;
    }

    public DolphinContext getCurrentContext() {
        return DolphinContextUtils.getContextForCurrentThread();
    }

    public DolphinSession getCurrentDolphinSession() {
        DolphinContext context = getCurrentContext();
        if (context == null) {
            return null;
        }
        return context.getCurrentDolphinSession();
    }

    private ContainerManager findManager() {
        final ServiceLoader<ContainerManager> serviceLoader = ServiceLoader.load(ContainerManager.class);
        final Iterator<ContainerManager> serviceIterator = serviceLoader.iterator();
        if (serviceIterator.hasNext()) {
            final ContainerManager containerManager = serviceIterator.next();
            if (serviceIterator.hasNext()) {
                throw new IllegalStateException("More than 1 " + ContainerManager.class + " found!");
            }
            return containerManager;
        } else {
            throw new IllegalStateException("No " + ContainerManager.class + " found!");
        }
    }

    public static DolphinPlatformBootstrap getInstance() {
        return INSTANCE;
    }

    public DolphinEventBusImpl getDolphinEventBus() {
        return dolphinEventBus;
    }

    public boolean isUp() {
        return up;
    }
}
