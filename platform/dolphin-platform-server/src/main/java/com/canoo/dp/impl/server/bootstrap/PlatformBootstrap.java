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
package com.canoo.dp.impl.server.bootstrap;

import com.canoo.dp.impl.platform.core.ansi.AnsiOut;
import com.canoo.platform.core.PlatformThreadFactory;
import com.canoo.dp.impl.platform.core.SimpleDolphinPlatformThreadFactory;
import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.server.beans.ManagedBeanFactory;
import com.canoo.dp.impl.server.config.DefaultModuleConfig;
import com.canoo.dp.impl.server.config.DefaultPlatformConfiguration;
import com.canoo.dp.impl.server.mbean.MBeanRegistry;
import com.canoo.dp.impl.server.scanner.DefaultClasspathScanner;
import com.canoo.platform.server.spi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.util.*;

public class PlatformBootstrap {

    private static final Logger LOG = LoggerFactory.getLogger(PlatformBootstrap.class);

    private static final String CONFIGURATION_ATTRIBUTE_NAME = "dolphinPlatformConfiguration";

    private static ServerCoreComponents serverCoreComponents;

    public void init(final ServletContext servletContext, final DefaultPlatformConfiguration configuration) {
        Assert.requireNonNull(servletContext, "servletContext");
        Assert.requireNonNull(configuration, "configuration");

        if(DefaultModuleConfig.isActive(configuration)) {
            printLogo();
            try {
                LOG.info("Will boot Dolphin Plaform now");

                servletContext.setAttribute(CONFIGURATION_ATTRIBUTE_NAME, configuration);
                configuration.log();

                MBeanRegistry.getInstance().setMbeanSupport(DefaultModuleConfig.isMBeanRegistration(configuration));


                //TODO: We need to provide a container specific thread factory that contains managed threads
                //See https://github.com/canoo/dolphin-platform/issues/498
                final PlatformThreadFactory threadFactory = new SimpleDolphinPlatformThreadFactory();
                final ManagedBeanFactory beanFactory = getBeanFactory(servletContext);
                final DefaultClasspathScanner classpathScanner = new DefaultClasspathScanner(DefaultModuleConfig.getRootPackageForClasspathScan(configuration));
                serverCoreComponents = new ServerCoreComponentsImpl(servletContext, configuration, threadFactory, classpathScanner, beanFactory);

                final Set<Class<?>> moduleClasses = classpathScanner.getTypesAnnotatedWith(ModuleDefinition.class);

                final Map<String, ServerModule> modules = new HashMap<>();
                for (final Class<?> moduleClass : moduleClasses) {
                    ModuleDefinition moduleDefinition = moduleClass.getAnnotation(ModuleDefinition.class);
                    ServerModule instance = (ServerModule) moduleClass.newInstance();
                    modules.put(moduleDefinition.value(), (ServerModule) instance);
                }

                LOG.info("Found {} Dolphin Plaform modules", modules.size());
                if (LOG.isTraceEnabled()) {
                    for (final String moduleName : modules.keySet()) {
                        LOG.trace("Found Dolphin Plaform module {}", moduleName);
                    }
                }

                for (final Map.Entry<String, ServerModule> moduleEntry : modules.entrySet()) {
                    LOG.debug("Will initialize Dolphin Plaform module {}", moduleEntry.getKey());
                    final ServerModule module = moduleEntry.getValue();
                    if (module.shouldBoot(serverCoreComponents.getConfiguration())) {
                        final List<String> neededModules = module.getModuleDependencies();
                        for (final String neededModule : neededModules) {
                            if (!modules.containsKey(neededModule)) {
                                throw new ModuleInitializationException("Module " + moduleEntry.getKey() + " depends on missing module " + neededModule);
                            }
                        }
                        module.initialize(serverCoreComponents);
                    }
                }
                LOG.info("Dolphin Plaform booted");
            } catch (Exception e) {
                throw new RuntimeException("Can not boot Dolphin Platform", e);
            }
        } else {
            LOG.info("Dolphin Plaform is deactivated");
        }
    }


    private ManagedBeanFactory getBeanFactory(final ServletContext servletContext) {
        final ServiceLoader<ManagedBeanFactory> serviceLoader = ServiceLoader.load(ManagedBeanFactory.class);
        final Iterator<ManagedBeanFactory> serviceIterator = serviceLoader.iterator();
        if (serviceIterator.hasNext()) {
            final ManagedBeanFactory factory = serviceIterator.next();
            if (serviceIterator.hasNext()) {
                throw new IllegalStateException("More than 1 " + ManagedBeanFactory.class + " found!");
            }
            LOG.debug("Container Manager of type {} is used", factory.getClass().getSimpleName());
            factory.init(servletContext);
            return factory;
        } else {
            throw new IllegalStateException("No " + ManagedBeanFactory.class + " found!");
        }
    }

    public static ServerCoreComponents getServerCoreComponents() {
        return serverCoreComponents;
    }

    private void printLogo() {
        final String strokeColor = AnsiOut.ANSI_BLUE;
        final String textColor = AnsiOut.ANSI_RED;
        final String borderColor = AnsiOut.ANSI_GREEN;


        final String borderStart = AnsiOut.ANSI_BOLD + borderColor;
        final String borderEnd = AnsiOut.ANSI_RESET;
        final String borderPipe = borderStart + "|" + borderEnd;
        final String logoStart = AnsiOut.ANSI_BOLD + strokeColor;
        final String logoEnd = AnsiOut.ANSI_RESET;
        final String textStart = textColor;
        final String textEnd = AnsiOut.ANSI_RESET;
        final String boldTextStart = AnsiOut.ANSI_BOLD + textColor;
        final String boldTextEnd = AnsiOut.ANSI_RESET;

        System.out.println("");
        System.out.println("  " + borderStart + "_____________________________________________________________________________________" + borderEnd);
        System.out.println("  " + borderPipe + logoStart + "   _____        _       _     _       _____  _       _    __                       " + borderPipe);
        System.out.println("  " + borderPipe + logoStart + "  |  __ \\      | |     | |   (_)     |  __ \\| |     | |  / _|                      " + borderPipe);
        System.out.println("  " + borderPipe + logoStart + "  | |  | | ___ | |_ __ | |__  _ _ __ | |__) | | __ _| |_| |_ ___  _ __ _ __ ___    " + borderPipe);
        System.out.println("  " + borderPipe + logoStart + "  | |  | |/ _ \\| | '_ \\| '_ \\| | '_ \\|  ___/| |/ _` | __|  _/ _ \\| '__| '_ ` _ \\   " + borderPipe);
        System.out.println("  " + borderPipe + logoStart + "  | |  | | (_) | | |_) | | | | | | | | |    | | (_| | |_| || (_) | |  | | | | | |  " + borderPipe);
        System.out.println("  " + borderPipe + logoStart + "  | |__| |\\___/|_| .__/|_| |_|_|_| |_|_|    |_|\\__,_|\\__|_| \\___/|_|  |_| |_| | |  " + borderPipe);
        System.out.println("  " + borderPipe + logoStart + "  |_____/        | |                                                          |_|  " + borderPipe);
        System.out.println("  " + borderPipe + logoStart + "                 |_|   " + textStart + "supported by " + textEnd + boldTextStart + "canoo.com" + boldTextEnd + "                                      " + borderPipe);
        System.out.println("  " + borderPipe + borderStart + "___________________________________________________________________________________" + borderEnd + borderPipe);
        System.out.println("");
        System.out.println("");
    }
}
