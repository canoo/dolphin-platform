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
package com.canoo.implementation.dolphin.server.context;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.event.Subscription;
import com.canoo.implementation.dolphin.BeanManagerImpl;
import com.canoo.implementation.dolphin.ClassRepositoryImpl;
import com.canoo.implementation.dolphin.Converters;
import com.canoo.implementation.dolphin.InternalAttributesBean;
import com.canoo.implementation.dolphin.PlatformConstants;
import com.canoo.implementation.dolphin.PresentationModelBuilderFactory;
import com.canoo.implementation.dolphin.collections.ListMapperImpl;
import com.canoo.implementation.dolphin.ClassRepository;
import com.canoo.implementation.dolphin.EventDispatcher;
import com.canoo.implementation.dolphin.collections.ListMapper;
import com.canoo.dolphin.server.session.DolphinSession;
import com.canoo.implementation.dolphin.server.config.DolphinPlatformConfiguration;
import com.canoo.implementation.dolphin.server.container.ContainerManager;
import com.canoo.implementation.dolphin.server.controller.ControllerHandler;
import com.canoo.implementation.dolphin.server.controller.ControllerRepository;
import com.canoo.implementation.dolphin.server.ServerBeanBuilder;
import com.canoo.implementation.dolphin.server.ServerBeanBuilderImpl;
import com.canoo.implementation.dolphin.server.ServerBeanRepository;
import com.canoo.implementation.dolphin.server.ServerBeanRepositoryImpl;
import com.canoo.implementation.dolphin.server.ServerControllerActionCallBean;
import com.canoo.implementation.dolphin.server.ServerEventDispatcher;
import com.canoo.implementation.dolphin.server.ServerPlatformBeanRepository;
import com.canoo.implementation.dolphin.server.ServerPresentationModelBuilderFactory;
import com.canoo.implementation.dolphin.server.UnstableFeatureFlags;
import com.canoo.implementation.dolphin.server.gc.GarbageCollectionCallback;
import com.canoo.implementation.dolphin.server.gc.GarbageCollector;
import com.canoo.implementation.dolphin.server.gc.Instance;
import com.canoo.implementation.dolphin.server.mbean.DolphinContextMBeanRegistry;
import com.canoo.implementation.dolphin.util.Assert;
import com.canoo.implementation.dolphin.util.Callback;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.NamedCommand;
import org.opendolphin.core.server.DefaultServerDolphin;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * This class defines the central entry point for a Dolphin Platform session on the server.
 * Each Dolphin Platform client context on the client side is connected with one {@link DolphinContext}.
 */
public class DolphinContext {

    private static final Logger LOG = LoggerFactory.getLogger(DolphinContext.class);

    private final String id;

    private final DolphinPlatformConfiguration configuration;

    private final DefaultServerDolphin dolphin;

    private final ServerBeanRepository beanRepository;

    private final Converters converters;

    private final BeanManager beanManager;

    private final ControllerHandler controllerHandler;

    private final EventDispatcher dispatcher;

    private ServerPlatformBeanRepository platformBeanRepository;

    private final DolphinContextMBeanRegistry mBeanRegistry;

    private final Callback<DolphinContext> onDestroyCallback;

    private final Callback<DolphinContext> preDestroyCallback;

    private final Subscription mBeanSubscription;

    private final DolphinSession dolphinSession;

    private final GarbageCollector garbageCollector;

    private final DolphinContextTaskQueue taskQueue;

    public DolphinContext(final DolphinPlatformConfiguration configuration, DolphinSessionProvider dolphinSessionProvider, ContainerManager containerManager, ControllerRepository controllerRepository, OpenDolphinFactory dolphinFactory, Callback<DolphinContext> preDestroyCallback, Callback<DolphinContext> onDestroyCallback) {
        this.configuration = Assert.requireNonNull(configuration, "configuration");
        Assert.requireNonNull(containerManager, "containerManager");
        Assert.requireNonNull(controllerRepository, "controllerRepository");
        Assert.requireNonNull(dolphinFactory, "dolphinFactory");
        this.preDestroyCallback = Assert.requireNonNull(preDestroyCallback, "preDestroyCallback");
        this.onDestroyCallback = Assert.requireNonNull(onDestroyCallback, "onDestroyCallback");

        //ID
        id = UUID.randomUUID().toString();
        //Init Open Dolphin
        dolphin = dolphinFactory.create();

        //Init Garbage Collection
        garbageCollector = new GarbageCollector(new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {
                for (Instance instance : instances) {
                    beanRepository.onGarbageCollectionRejection(instance.getBean());
                }
            }
        });

        taskQueue = new DolphinContextTaskQueue(id, dolphinSessionProvider, configuration.getMaxPollTime(), TimeUnit.MILLISECONDS);

        //Init BeanRepository
        dispatcher = new ServerEventDispatcher(dolphin);
        beanRepository = new ServerBeanRepositoryImpl(dolphin, dispatcher, garbageCollector);
        converters = new Converters(beanRepository);

        //Init BeanManager
        final PresentationModelBuilderFactory builderFactory = new ServerPresentationModelBuilderFactory(dolphin);
        final ClassRepository classRepository = new ClassRepositoryImpl(dolphin, converters, builderFactory);
        final ListMapper listMapper = new ListMapperImpl(dolphin, classRepository, beanRepository, builderFactory, dispatcher);
        final ServerBeanBuilder beanBuilder = new ServerBeanBuilderImpl(classRepository, beanRepository, listMapper, builderFactory, dispatcher, garbageCollector);
        beanManager = new BeanManagerImpl(beanRepository, beanBuilder);

        //Init MBean Support
        mBeanRegistry = new DolphinContextMBeanRegistry(id);

        //Init ControllerHandler
        controllerHandler = new ControllerHandler(mBeanRegistry, containerManager, beanBuilder, beanRepository, controllerRepository);

        dolphinSession = new DolphinSessionImpl(id, new Executor() {
            @Override
            public void execute(final Runnable command) {
                runLater(command);
            }
        });

        //Register commands
        registerDolphinPlatformDefaultCommands();

        mBeanSubscription = mBeanRegistry.registerDolphinContext(dolphinSession, garbageCollector);
    }

    private void registerDolphinPlatformDefaultCommands() {
        dolphin.register(new DolphinServerAction() {
            @Override
            public void registerIn(ActionRegistry registry) {

                registry.register(PlatformConstants.INIT_CONTEXT_COMMAND_NAME, new CommandHandler() {
                    @Override
                    public void handleCommand(Command command, List response) {
                        LOG.trace("Handling {} for DolphinContext {}", PlatformConstants.INIT_CONTEXT_COMMAND_NAME, getId());
                        onInitContext();
                    }
                });

                registry.register(PlatformConstants.DESTROY_CONTEXT_COMMAND_NAME, new CommandHandler() {
                    @Override
                    public void handleCommand(Command command, List response) {
                        LOG.trace("Handling {} for DolphinContext {}", PlatformConstants.DESTROY_CONTEXT_COMMAND_NAME, getId());
                        onDestroyContext();
                    }
                });

                registry.register(PlatformConstants.REGISTER_CONTROLLER_COMMAND_NAME, new CommandHandler() {
                    @Override
                    public void handleCommand(Command command, List response) {
                        LOG.trace("Handling {} for DolphinContext {}", PlatformConstants.REGISTER_CONTROLLER_COMMAND_NAME, getId());
                        onRegisterController();
                    }
                });

                registry.register(PlatformConstants.DESTROY_CONTROLLER_COMMAND_NAME, new CommandHandler() {
                    @Override
                    public void handleCommand(Command command, List response) {
                        LOG.trace("Handling {} for DolphinContext {}", PlatformConstants.DESTROY_CONTROLLER_COMMAND_NAME, getId());
                        onDestroyController();
                    }
                });

                registry.register(PlatformConstants.CALL_CONTROLLER_ACTION_COMMAND_NAME, new CommandHandler() {
                    @Override
                    public void handleCommand(Command command, List response) {
                        LOG.trace("Handling {} for DolphinContext {}", PlatformConstants.CALL_CONTROLLER_ACTION_COMMAND_NAME, getId());
                        onCallControllerAction();
                    }
                });

                registry.register(PlatformConstants.POLL_EVENT_BUS_COMMAND_NAME, new CommandHandler() {
                    @Override
                    public void handleCommand(Command command, List response) {
                        LOG.trace("Handling {} for DolphinContext {}", PlatformConstants.POLL_EVENT_BUS_COMMAND_NAME, getId());
                        onPollEventBus();
                    }
                });

                registry.register(PlatformConstants.RELEASE_EVENT_BUS_COMMAND_NAME, new CommandHandler() {
                    @Override
                    public void handleCommand(Command command, List response) {
                        LOG.trace("Handling {} for DolphinContext {}", PlatformConstants.RELEASE_EVENT_BUS_COMMAND_NAME, getId());
                        onReleaseEventBus();
                    }
                });

                registry.register(PlatformConstants.GARBAGE_COLLECTION_COMMAND_NAME, new CommandHandler() {
                    @Override
                    public void handleCommand(Command command, List response) {
                        LOG.trace("Handling {} for DolphinContext {}", PlatformConstants.GARBAGE_COLLECTION_COMMAND_NAME, getId());
                        onGarbageCollection();
                    }
                });
            }
        });
    }

    private void onInitContext() {
        platformBeanRepository = new ServerPlatformBeanRepository(dolphin, beanRepository, dispatcher, converters);
    }

    private void onDestroyContext() {
        destroy();
    }

    public void destroy() {
        preDestroyCallback.call(this);

        //Deregister from event bus
        //dolphinEventBus.unsubscribeSession(getId());

        //Destroy all controllers
        controllerHandler.destroyAllControllers();

        if (mBeanSubscription != null) {
            mBeanSubscription.unsubscribe();
        }

        onDestroyCallback.call(this);
    }

    private void onRegisterController() {
        if (platformBeanRepository == null) {
            throw new IllegalStateException("An action was called before the init-command was sent.");
        }
        final InternalAttributesBean bean = platformBeanRepository.getInternalAttributesBean();
        String controllerId = controllerHandler.createController(bean.getControllerName());
        bean.setControllerId(controllerId);
        Object model = controllerHandler.getControllerModel(controllerId);
        if (model != null) {
            bean.setModel(model);
        }
    }

    private void onDestroyController() {
        if (platformBeanRepository == null) {
            throw new IllegalStateException("An action was called before the init-command was sent.");
        }
        final InternalAttributesBean bean = platformBeanRepository.getInternalAttributesBean();
        controllerHandler.destroyController(bean.getControllerId());
    }

    private void onCallControllerAction() {
        if (platformBeanRepository == null) {
            throw new IllegalStateException("An action was called before the init-command was sent.");
        }
        final ServerControllerActionCallBean bean = platformBeanRepository.getControllerActionCallBean();
        try {
            controllerHandler.invokeAction(bean);
        } catch (Exception e) {
            LOG.error("Unexpected exception while invoking action {} on controller {}",
                    bean.getActionName(), bean.getControllerId(), e);
            bean.setError(true);
        }
    }

    private void onReleaseEventBus() {
        taskQueue.interrupt();
    }

    private void onPollEventBus() {
        taskQueue.executeTasks();
    }

    private void onGarbageCollection() {
        garbageCollector.gc();
    }

    public DefaultServerDolphin getDolphin() {
        return dolphin;
    }

    public BeanManager getBeanManager() {
        return beanManager;
    }

    public String getId() {
        return id;
    }

    public List<Command> handle(List<Command> commands) {
        List<Command> results = new LinkedList<>();
        for (Command command : commands) {
            results.addAll(dolphin.getServerConnector().receive(command));
        }

        if (UnstableFeatureFlags.isUseGc()) {
            if (commands.size() != 1 || !PlatformConstants.RELEASE_EVENT_BUS_COMMAND_NAME.equals(commands.get(0).getId())) {
                NamedCommand garbageCollectionCommand = new NamedCommand(PlatformConstants.GARBAGE_COLLECTION_COMMAND_NAME);
                results.addAll(dolphin.getServerConnector().receive(garbageCollectionCommand));
            }
        }
        return results;
    }



    public DolphinSession getDolphinSession() {
        return dolphinSession;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DolphinContext that = (DolphinContext) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    private void runLater(final Runnable runnable) {
        taskQueue.addTask(runnable);
    }
}
