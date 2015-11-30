/*
 * Copyright 2015 Canoo Engineering AG.
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
import com.canoo.dolphin.impl.*;
import com.canoo.dolphin.impl.collections.ListMapperImpl;
import com.canoo.dolphin.internal.BeanBuilder;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.ClassRepository;
import com.canoo.dolphin.internal.EventDispatcher;
import com.canoo.dolphin.internal.collections.ListMapper;
import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.controller.ControllerHandler;
import com.canoo.dolphin.server.controller.InvokeActionException;
import com.canoo.dolphin.server.event.impl.DolphinContextTaskExecutor;
import com.canoo.dolphin.server.event.impl.DolphinEventBusImpl;
import com.canoo.dolphin.server.impl.ServerEventDispatcher;
import com.canoo.dolphin.server.impl.ServerPresentationModelBuilderFactory;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.JsonCodec;
import org.opendolphin.core.server.DefaultServerDolphin;
import org.opendolphin.core.server.ServerConnector;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerModelStore;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class DolphinContext {

    private final DefaultServerDolphin dolphin;

    private final BeanRepository beanRepository;

    private final BeanManager beanManager;

    private final ControllerHandler controllerHandler;

    private ContainerManager containerManager;

    private String id;

    private ServletContext servletContext;

    private DolphinContextTaskExecutor taskExecutor;

    public DolphinContext(ContainerManager containerManager, ServletContext servletContext) {
        this.containerManager = containerManager;
        this.servletContext = servletContext;

        //ID
        id = UUID.randomUUID().toString();

        //Init Open Dolphin
        final ServerModelStore modelStore = new ServerModelStore();
        final ServerConnector serverConnector = new ServerConnector();
        serverConnector.setCodec(new JsonCodec());
        serverConnector.setServerModelStore(modelStore);
        dolphin = new DefaultServerDolphin(modelStore, serverConnector);
        dolphin.registerDefaultActions();

        //Init BeanRepository
        final EventDispatcher dispatcher = new ServerEventDispatcher(dolphin);
        beanRepository = new BeanRepositoryImpl(dolphin, dispatcher);

        //Init BeanManager
        final PresentationModelBuilderFactory builderFactory = new ServerPresentationModelBuilderFactory(dolphin);
        final ClassRepository classRepository = new ClassRepositoryImpl(dolphin, beanRepository, builderFactory);
        final ListMapper listMapper = new ListMapperImpl(dolphin, classRepository, beanRepository, builderFactory, dispatcher);
        final BeanBuilder beanBuilder = new BeanBuilderImpl(classRepository, beanRepository, listMapper, builderFactory, dispatcher);
        beanManager = new BeanManagerImpl(beanRepository, beanBuilder);

        //Init ControllerHandler
        controllerHandler = new ControllerHandler(dolphin, containerManager, beanRepository, beanManager);

        //Init TaskExecutor
        taskExecutor = new DolphinContextTaskExecutor();

        //Register commands
        registerDolphinPlatformDefaultCommands();

    }

    private void registerDolphinPlatformDefaultCommands() {
        dolphin.register(new DolphinServerAction() {
            @Override
            public void registerIn(ActionRegistry registry) {

                registry.register(PlatformConstants.REGISTER_CONTROLLER_COMMAND_NAME, new CommandHandler() {
                    @Override
                    public void handleCommand(Command command, List response) {
                        onRegisterController();
                    }
                });

                registry.register(PlatformConstants.DESTROY_CONTROLLER_COMMAND_NAME, new CommandHandler() {
                    @Override
                    public void handleCommand(Command command, List response) {
                        onDestroyController();
                    }
                });

                registry.register(PlatformConstants.CALL_CONTROLLER_ACTION_COMMAND_NAME, new CommandHandler() {
                    @Override
                    public void handleCommand(Command command, List response) {
                        try {
                            onInvokeControllerAction();
                        } catch (Exception e) {
                            ControllerActionCallErrorBean errorBean = beanManager.create(ControllerActionCallErrorBean.class);
                            String controllerId = null;
                            String actionName = null;
                            String actionid = null;
                            try {
                                ControllerActionCallBean bean = beanManager.findAll(ControllerActionCallBean.class).get(0);
                                controllerId = bean.getControllerId();
                                actionName = bean.getActionName();
                                actionid = bean.getId();
                            } finally {
                                errorBean.setControllerid(controllerId);
                                errorBean.setActionName(actionName);
                                errorBean.setActionId(actionid);
                            }
                        }
                    }
                });

                registry.register(PlatformConstants.POLL_COMMAND_NAME, new CommandHandler() {
                    @Override
                    public void handleCommand(Command command, List response) {
                        onPollEventBus();
                    }
                });

                registry.register(PlatformConstants.RELEASE_COMMAND_NAME, new CommandHandler() {
                    @Override
                    public void handleCommand(Command command, List response) {
                        onReleaseEventBus();
                    }
                });

                registry.register(PlatformConstants.INIT_COMMAND_NAME, new CommandHandler() {
                    @Override
                    public void handleCommand(Command command, List response) {
                        //New Client
                        beanManager.create(ControllerRegistryBean.class);
                        beanManager.create(ControllerDestroyBean.class);
                        beanManager.create(ControllerActionCallBean.class);
                    }
                });

                registry.register(PlatformConstants.DISCONNECT_COMMAND_NAME, new CommandHandler() {
                    @Override
                    public void handleCommand(Command command, List response) {
                        //Disconnect Client
                        beanManager.removeAll(ControllerRegistryBean.class);
                        beanManager.removeAll(ControllerDestroyBean.class);
                        beanManager.removeAll(ControllerActionCallBean.class);

                        //TODO: How to disconnect?
                    }
                });
            }
        });
    }

    private void onRegisterController() {
        ControllerRegistryBean bean = beanManager.findAll(ControllerRegistryBean.class).get(0);
        String controllerId = controllerHandler.createController(bean.getControllerName());
        bean.setControllerId(controllerId);
        Object model = controllerHandler.getControllerModel(controllerId);
        if (model != null) {
            bean.setModel(model);
        }
    }

    private void onDestroyController() {
        ControllerDestroyBean bean = beanManager.findAll(ControllerDestroyBean.class).get(0);
        controllerHandler.destroyController(bean.getControllerId());
    }

    private void onInvokeControllerAction() throws InvokeActionException {
        ControllerActionCallBean bean = beanManager.findAll(ControllerActionCallBean.class).get(0);
        controllerHandler.invokeAction(bean.getControllerId(), bean.getActionName(), bean.getId());
    }

    private void onPollEventBus() {
        try {
            DolphinEventBusImpl.getInstance().longPoll();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void onReleaseEventBus() {
        DolphinEventBusImpl.getInstance().release();
    }

    public ServerDolphin getDolphin() {
        return dolphin;
    }

    public BeanManager getBeanManager() {
        return beanManager;
    }

    public ControllerHandler getControllerHandler() {
        return controllerHandler;
    }

    public ContainerManager getContainerManager() {
        return containerManager;
    }

    public BeanRepository getBeanRepository() {
        return beanRepository;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public DolphinContextTaskExecutor getTaskExecutor() {
        return taskExecutor;
    }

    public String getId() {
        return id;
    }

    public static DolphinContext getCurrentContext() {
        return DolphinContextHandler.getCurrentContext();
    }

    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws Exception {

        resp.setHeader(PlatformConstants.CLIENT_ID_HTTP_HEADER_NAME, id);

        //copied from DolphinServlet
        StringBuilder requestJson = new StringBuilder();
        String line = null;
        while ((line = req.getReader().readLine()) != null) {
            requestJson.append(line).append("\n");
        }
        List<Command> commands = dolphin.getServerConnector().getCodec().decode(requestJson.toString());
        List<Command> results = new LinkedList<>();
        for (Command command : commands) {
            results.addAll(dolphin.getServerConnector().receive(command));
        }
        String jsonResponse = dolphin.getServerConnector().getCodec().encode(results);
        resp.getOutputStream().print(jsonResponse);
    }
}
