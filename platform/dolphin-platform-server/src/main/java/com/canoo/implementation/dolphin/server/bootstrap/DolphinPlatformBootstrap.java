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
package com.canoo.implementation.dolphin.server.bootstrap;

import com.canoo.dolphin.server.session.DolphinListener;
import com.canoo.dolphin.server.session.DolphinSession;
import com.canoo.dolphin.server.session.DolphinSessionListener;
import com.canoo.implementation.dolphin.server.config.DolphinPlatformConfiguration;
import com.canoo.implementation.dolphin.server.container.ContainerManager;
import com.canoo.implementation.dolphin.server.context.DefaultDolphinContextFactory;
import com.canoo.implementation.dolphin.server.context.DolphinContext;
import com.canoo.implementation.dolphin.server.context.DolphinContextCommunicationHandler;
import com.canoo.implementation.dolphin.server.context.DolphinContextFactory;
import com.canoo.implementation.dolphin.server.context.DolphinContextFilter;
import com.canoo.implementation.dolphin.server.context.DolphinContextProvider;
import com.canoo.implementation.dolphin.server.context.DolphinContextUtils;
import com.canoo.implementation.dolphin.server.context.DolphinHttpSessionListener;
import com.canoo.implementation.dolphin.server.context.DolphinSessionLifecycleHandler;
import com.canoo.implementation.dolphin.server.ClasspathScanner;
import com.canoo.implementation.dolphin.server.DolphinSessionLifecycleHandlerImpl;
import com.canoo.implementation.dolphin.server.mbean.MBeanRegistry;
import com.canoo.implementation.dolphin.server.servlet.CrossSiteOriginFilter;
import com.canoo.implementation.dolphin.server.servlet.DolphinPlatformServlet;
import com.canoo.implementation.dolphin.util.Assert;
import com.canoo.implementation.dolphin.util.Callback;
import com.canoo.implementation.dolphin.server.servlet.InvalidationServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;

import static com.canoo.implementation.dolphin.server.servlet.ServletConstants.DEFAULT_DOLPHIN_INVALIDATION_SERVLET_MAPPING;
import static com.canoo.implementation.dolphin.server.servlet.ServletConstants.DOLPHIN_CLIENT_ID_FILTER_NAME;
import static com.canoo.implementation.dolphin.server.servlet.ServletConstants.DOLPHIN_CROSS_SITE_FILTER_NAME;
import static com.canoo.implementation.dolphin.server.servlet.ServletConstants.DOLPHIN_INVALIDATION_SERVLET_NAME;
import static com.canoo.implementation.dolphin.server.servlet.ServletConstants.DOLPHIN_SERVLET_NAME;

/**
 * This class defines the bootstrap for Dolphin Platform.
 */
public class DolphinPlatformBootstrap {

    private static final Logger LOG = LoggerFactory.getLogger(DolphinPlatformBootstrap.class);

    private static final DolphinContextProvider contextProvider = new DolphinContextProvider() {

        @Override
        public DolphinSession getCurrentDolphinSession() {
            DolphinContext context = getCurrentContext();
            if (context == null) {
                return null;
            }
            return context.getDolphinSession();
        }

        @Override
        public DolphinContext getCurrentContext() {
            return DolphinContextUtils.getContextForCurrentThread();
        }
    };

    private static final DolphinSessionLifecycleHandler sessionLifecycleHandler = new DolphinSessionLifecycleHandlerImpl();

    private DolphinPlatformBootstrap() {
    }

    /**
     * This methods starts the Dolphin Platform server runtime
     *
     * @param servletContext the servlet context
     */
    public static final void start(final ServletContext servletContext, final DolphinPlatformConfiguration configuration) {
        Assert.requireNonNull(servletContext, "servletContext");
        Assert.requireNonNull(configuration, "configuration");

        LOG.debug("Dolphin Platform starts with value for useCrossSiteOriginFilter=" + configuration.isUseCrossSiteOriginFilter());
        LOG.debug("Dolphin Platform starts with value for dolphinPlatformServletMapping=" + configuration.getDolphinPlatformServletMapping());
        LOG.debug("Dolphin Platform starts with value for openDolphinLogLevel=" + configuration.getOpenDolphinLogLevel());

        final ClasspathScanner classpathScanner = new ClasspathScanner(configuration.getRootPackageForClasspathScan());

        MBeanRegistry.getInstance().setMbeanSupport(configuration.isMBeanRegistration());

        final ContainerManager containerManager = findManager();
        containerManager.init(servletContext);

        registerDolphinSessionListener(containerManager, classpathScanner);

        final DolphinContextCommunicationHandler communicationHandler = new DolphinContextCommunicationHandler(configuration, getContextProvider());

        DolphinContextFactory dolphinContextFactory = new DefaultDolphinContextFactory(configuration, getContextProvider(), containerManager, classpathScanner, getSessionLifecycleHandler());
        servletContext.addServlet(DOLPHIN_SERVLET_NAME, new DolphinPlatformServlet(communicationHandler)).addMapping(configuration.getDolphinPlatformServletMapping());
        if (configuration.isUseSessionInvalidationServlet()) {
            servletContext.addServlet(DOLPHIN_INVALIDATION_SERVLET_NAME, new InvalidationServlet()).addMapping(DEFAULT_DOLPHIN_INVALIDATION_SERVLET_MAPPING);
        }
        if (configuration.isUseCrossSiteOriginFilter()) {
            servletContext.addFilter(DOLPHIN_CROSS_SITE_FILTER_NAME, new CrossSiteOriginFilter()).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        }

        servletContext.addFilter(DOLPHIN_CLIENT_ID_FILTER_NAME, new DolphinContextFilter(configuration, containerManager, dolphinContextFactory, getSessionLifecycleHandler())).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, configuration.getIdFilterUrlMappings().toArray(new String[configuration.getIdFilterUrlMappings().size()]));

        LOG.debug("Dolphin Platform initialized under context \"" + servletContext.getContextPath() + "\"");
        LOG.debug("Dolphin Platform endpoint defined as " + configuration.getDolphinPlatformServletMapping());

        DolphinHttpSessionListener contextCleaner = new DolphinHttpSessionListener();
        contextCleaner.init(configuration);
        servletContext.addListener(contextCleaner);

        java.util.logging.Logger openDolphinLogger = java.util.logging.Logger.getLogger("org.opendolphin");
        openDolphinLogger.setLevel(configuration.getOpenDolphinLogLevel());
    }

    public static DolphinContextProvider getContextProvider() {
        return contextProvider;
    }

    public static DolphinSessionLifecycleHandler getSessionLifecycleHandler() {
        return sessionLifecycleHandler;
    }

    private static  ContainerManager findManager() {
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

    private static void registerDolphinSessionListener(final ContainerManager containerManager, final ClasspathScanner classpathScanner) {
        Assert.requireNonNull(containerManager, "containerManager");
        Assert.requireNonNull(classpathScanner, "classpathScanner");
        final Set<Class<?>> listeners = classpathScanner.getTypesAnnotatedWith(DolphinListener.class);
        for (final Class<?> listenerClass : listeners) {
            try {
                if (DolphinSessionListener.class.isAssignableFrom(listenerClass)) {
                    getSessionLifecycleHandler().addSessionDestroyedListener(new Callback<DolphinSession>() {
                        @Override
                        public void call(DolphinSession dolphinSession) {
                            final DolphinSessionListener listener = (DolphinSessionListener) containerManager.createListener(listenerClass);
                            listener.sessionDestroyed(dolphinSession);
                        }
                    });
                    getSessionLifecycleHandler().addSessionCreatedListener(new Callback<DolphinSession>() {
                        @Override
                        public void call(DolphinSession dolphinSession) {
                            final DolphinSessionListener listener = (DolphinSessionListener) containerManager.createListener(listenerClass);
                            listener.sessionCreated(dolphinSession);
                        }
                    });
                }
            } catch (Exception e) {
                throw new DolphinPlatformBoostrapException("Error in creating DolphinSessionListener " + listenerClass, e);
            }
        }
    }
}
