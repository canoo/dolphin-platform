/*
 * Copyright 2015-2017 Canoo Engineering AG.
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
package com.canoo.dp.impl.server;

import com.canoo.platform.core.DolphinRuntimeException;
import com.canoo.platform.server.spi.components.ManagedBeanFactory;
import com.canoo.dp.impl.server.bootstrap.modules.ClientSessionModule;
import com.canoo.dp.impl.server.client.ClientSessionLifecycleHandler;
import com.canoo.dp.impl.server.client.ClientSessionProvider;
import com.canoo.dp.impl.server.config.RemotingConfiguration;
import com.canoo.dp.impl.server.context.DefaultDolphinContextFactory;
import com.canoo.dp.impl.server.context.DolphinContext;
import com.canoo.dp.impl.server.context.DolphinContextCommunicationHandler;
import com.canoo.dp.impl.server.context.DolphinContextFactory;
import com.canoo.dp.impl.server.context.DolphinContextProvider;
import com.canoo.dp.impl.server.controller.ControllerValidationException;
import com.canoo.dp.impl.server.event.AbstractEventBus;
import com.canoo.dp.impl.server.servlet.DolphinPlatformServlet;
import com.canoo.dp.impl.server.servlet.InterruptServlet;
import com.canoo.platform.remoting.server.event.RemotingEventBus;
import com.canoo.platform.remoting.server.spi.EventBusProvider;
import com.canoo.platform.server.client.ClientSession;
import com.canoo.platform.server.spi.components.ClasspathScanner;
import com.canoo.platform.server.spi.ModuleDefinition;
import com.canoo.platform.server.spi.ModuleInitializationException;
import com.canoo.platform.core.PlatformConfiguration;
import com.canoo.platform.server.spi.ServerCoreComponents;
import com.canoo.platform.server.spi.ServerModule;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import static com.canoo.dp.impl.server.servlet.ServletConstants.DOLPHIN_SERVLET_NAME;
import static com.canoo.dp.impl.server.servlet.ServletConstants.INTERRUPT_SERVLET_NAME;
import static org.apiguardian.api.API.Status.INTERNAL;

@ModuleDefinition(value = "RPM", order = 101)
@API(since = "0.x", status = INTERNAL)
public class RemotingModule implements ServerModule {

    private static final Logger LOG = LoggerFactory.getLogger(RemotingModule.class);

    @Override
    public List<String> getModuleDependencies() {
        return Collections.singletonList(ClientSessionModule.CLIENT_SESSION_MODULE);
    }

    @Override
    public boolean shouldBoot(PlatformConfiguration configuration) {
        final RemotingConfiguration remotingConfiguration = new RemotingConfiguration(configuration);
        return remotingConfiguration.isRemotingActive();
    }

    @Override
    public void initialize(ServerCoreComponents coreComponents) throws ModuleInitializationException {
        LOG.info("Starting Dolphin Platform");
        try{
            final ServletContext servletContext = coreComponents.getInstance(ServletContext.class).
                    orElseThrow(() -> new DolphinRuntimeException("Can not create " + RemotingModule.class));
            final ClasspathScanner classpathScanner = coreComponents.getInstance(ClasspathScanner.class).
                    orElseThrow(() -> new DolphinRuntimeException("Can not create " + RemotingModule.class));
            final ManagedBeanFactory beanFactory = coreComponents.getInstance(ManagedBeanFactory.class).
                    orElseThrow(() -> new DolphinRuntimeException("Can not create " + RemotingModule.class));
            final RemotingConfiguration configuration = new RemotingConfiguration(coreComponents.getConfiguration());
            final ClientSessionProvider sessionProvider = coreComponents.getInstance(ClientSessionProvider.class).
                    orElseThrow(() -> new DolphinRuntimeException("Can not create " + RemotingModule.class));
            final DolphinContextFactory dolphinContextFactory = new DefaultDolphinContextFactory(configuration, sessionProvider, beanFactory, classpathScanner);
            final DolphinContextCommunicationHandler communicationHandler = new DolphinContextCommunicationHandler(sessionProvider, dolphinContextFactory);
            final DolphinContextProvider contextProvider = new DolphinContextProvider() {
                @Override
                public DolphinContext getContext(final ClientSession clientSession) {
                    return communicationHandler.getContext(clientSession);
                }

                @Override
                public DolphinContext getContextById(String clientSessionId) {
                    return communicationHandler.getContextById(clientSessionId);
                }

                @Override
                public DolphinContext getCurrentDolphinContext() {
                    return communicationHandler.getCurrentDolphinContext();
                }
            };
            coreComponents.provideInstance(DolphinContextProvider.class, contextProvider);

            final ClientSessionLifecycleHandler lifecycleHandler = coreComponents.getInstance(ClientSessionLifecycleHandler.class).
                    orElseThrow(() -> new DolphinRuntimeException("Can not create " + RemotingModule.class));

            servletContext.addServlet(DOLPHIN_SERVLET_NAME, new DolphinPlatformServlet(communicationHandler)).addMapping(configuration.getDolphinPlatformServletMapping());

            servletContext.addServlet(INTERRUPT_SERVLET_NAME, new InterruptServlet(contextProvider)).addMapping(configuration.getDolphinPlatformInterruptServletMapping());

            LOG.debug("Dolphin Platform initialized under context \"" + servletContext.getContextPath() + "\"");
            LOG.debug("Dolphin Platform endpoint defined as " + configuration.getDolphinPlatformServletMapping());

            Iterator<EventBusProvider> iterator = ServiceLoader.load(EventBusProvider.class).iterator();
            boolean providerFound = false;
            while (iterator.hasNext()) {
                EventBusProvider provider = iterator.next();
                if (configuration.getEventbusType().equals(provider.getType())) {
                    if(providerFound) {
                        throw new IllegalStateException("More than 1 event bus provider found");
                    }
                    LOG.debug("Using event bus of type {} with provider class {}", provider.getType(), provider.getClass());
                    providerFound = true;
                    RemotingEventBus eventBus = provider.create(configuration);
                    if(eventBus instanceof AbstractEventBus) {
                        ((AbstractEventBus) eventBus).init(contextProvider, lifecycleHandler);
                    }
                    coreComponents.provideInstance(RemotingEventBus.class, eventBus);
                }
            }
        }catch (ControllerValidationException cve){
            throw new ModuleInitializationException("Can not start Remote Presentation Model support based on bad controller definition", cve);
        }
    }
}
