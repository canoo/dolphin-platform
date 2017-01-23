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
package com.canoo.dolphin.server.context;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.impl.BeanManagerImpl;
import com.canoo.dolphin.impl.ClassRepositoryImpl;
import com.canoo.dolphin.impl.Converters;
import com.canoo.dolphin.impl.InternalAttributesBean;
import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.impl.PresentationModelBuilderFactory;
import com.canoo.dolphin.impl.collections.ListMapperImpl;
import com.canoo.dolphin.internal.ClassRepository;
import com.canoo.dolphin.internal.EventDispatcher;
import com.canoo.dolphin.internal.collections.ListMapper;
import com.canoo.dolphin.server.DolphinSession;
import com.canoo.dolphin.server.config.DolphinPlatformConfiguration;
import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.controller.ControllerHandler;
import com.canoo.dolphin.server.controller.ControllerRepository;
import com.canoo.dolphin.server.impl.ServerBeanBuilder;
import com.canoo.dolphin.server.impl.ServerBeanBuilderImpl;
import com.canoo.dolphin.server.impl.ServerBeanRepository;
import com.canoo.dolphin.server.impl.ServerBeanRepositoryImpl;
import com.canoo.dolphin.server.impl.ServerControllerActionCallBean;
import com.canoo.dolphin.server.impl.ServerEventDispatcher;
import com.canoo.dolphin.server.impl.ServerPlatformBeanRepository;
import com.canoo.dolphin.server.impl.ServerPresentationModelBuilderFactory;
import com.canoo.dolphin.server.impl.gc.GarbageCollectionCallback;
import com.canoo.dolphin.server.impl.gc.GarbageCollector;
import com.canoo.dolphin.server.impl.gc.Instance;
import com.canoo.dolphin.server.mbean.DolphinContextMBeanRegistry;
import com.canoo.common.Assert;
import com.canoo.common.Callback;
import com.canoo.communication.common.commands.Command;
import com.canoo.remoting.server.DefaultServerDolphin;
import com.canoo.remoting.server.action.DolphinServerAction;
import com.canoo.remoting.server.communication.ActionRegistry;
import com.canoo.remoting.server.communication.CommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * This class defines the central entry point for a Dolphin Platform session on the server.
 * Each Dolphin Platform client context on the client side is connected with one {@link DolphinContext}.
 */
public class DolphinContext {

    private static final Logger LOG = LoggerFactory.getLogger(DolphinContext.class);

    private final DolphinPlatformConfiguration configuration;

    private final DefaultServerDolphin dolphin;

    private final ServerBeanRepository beanRepository;

    private final Converters converters;

    private final BeanManager beanManager;

    private final ControllerHandler controllerHandler;

    private final EventDispatcher dispatcher;

    private ServerPlatformBeanRepository platformBeanRepository;

    private final String id;

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
        garbageCollector = new GarbageCollector(configuration, new GarbageCollectionCallback() {
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
                        if(configuration.isUseGc()) {
                            LOG.trace("Handling GarbageCollection for DolphinContext {}", getId());
                            onGarbageCollection();
                        }

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

    public Future<Void> runLater(final Runnable runnable) {
        return taskQueue.addTask(runnable);
    }
}
