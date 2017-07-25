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
package com.canoo.platform.client.javafx.view;

import com.canoo.platform.client.ClientContext;
import com.canoo.platform.client.ControllerActionException;
import com.canoo.platform.client.ControllerProxy;
import com.canoo.platform.client.Param;
import com.canoo.dp.impl.platform.core.Assert;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.util.concurrent.CompletableFuture;

/**
 * A abstract JavaFX view controller that can be used as a basic for a JavaFX based view. Each instance will automatically
 * trigger Dolphin Platform to create a controller instance on the server that is bound to the view instance and shares
 * a model (see {@link com.canoo.dolphin.mapping.DolphinBean}) with the view.
 * @param <M> type of the model
 */
public abstract class AbstractViewBinder<M> {

    private ControllerProxy<M> controllerProxy;

    private final ReadOnlyBooleanWrapper actionInProcess = new ReadOnlyBooleanWrapper(false);

    private final ReadOnlyObjectWrapper<M> model = new ReadOnlyObjectWrapper<>();

    private final ReadOnlyObjectWrapper<Throwable> initializationException = new ReadOnlyObjectWrapper<>();

    private final ReadOnlyObjectWrapper<ControllerActionException> invocationException = new ReadOnlyObjectWrapper<>();

    private final ClientContext clientContext;

    /**
     * Constructor that internally starts the Dolphin Platform workflow and triggers the controller creation on the server.
     * @param clientContext the client context
     * @param controllerName name of the controller (see annotation DolphinController in the Java server lib).
     */
    public AbstractViewBinder(ClientContext clientContext, String controllerName) {
        Assert.requireNonBlank(controllerName, "controllerName");
        this.clientContext = Assert.requireNonNull(clientContext, "clientContext");
        clientContext.<M>createController(controllerName).whenComplete((c, e) -> {
            if (e != null) {
                initializationException.set(e);
                onInitializationException(e);
            } else {
                try {
                    controllerProxy = c;
                    model.set(c.getModel());
                    init();
                } catch (Exception exception) {
                    onInitializationException(exception);
                }
            }
        });
    }

    /**
     * This method will automatically be called after the controller instance has been created on the server and the initial
     * model is snychronized between client and server. When this method is called the model can be accessed (by calling {@link #getModel()})
     * and actions can be triggered on the server controller instance (by calling {@link #invoke(String, Param...)}).
     */
    protected abstract void init();

    /**
     * By calling this method the MVC group will be destroyed. This means that the controller instance on the server will
     * be removed and the model that is managed and synchronized between client and server will be detached. After this method
     * is called the view should not be used anymore. It's important to call this method to removePresentationModel all the unneeded references on
     * the server.
     * @return a future can be used to react on the destroy
     */
    public CompletableFuture<Void> destroy() {
        CompletableFuture<Void> ret;
        if (controllerProxy != null) {
            ret = controllerProxy.destroy();
            controllerProxy = null;
        } else {
            ret = new CompletableFuture<>();
            ret.complete(null);
        }
        return ret;
    }

    /**
     * This invokes a action on the server side controller. For more information how an action can be defined in the
     * controller have a look at the DolphinAction annotation in the server module.
     * This method don't block and can be called from the Platform thread. To check if an server
     * @param actionName name of the action
     * @param params any parameters that should be passed to the action
     * @return a future can be used to check if the action invocation is still running
     */
    protected CompletableFuture<Void> invoke(String actionName, Param... params) {
        Assert.requireNonBlank(actionName, "actionName");
        actionInProcess.set(true);
        return controllerProxy.invoke(actionName, params).whenComplete((v, e) -> {
            try {
                if (e != null) {
                    invocationException.set(new ControllerActionException(e));
                    onInvocationException(new ControllerActionException(e));
                }
            } finally {
                actionInProcess.set(false);
            }
        });
    }

    /**
     * Returns true if an action invocation is running (see {@link #invoke(String, Param...)})
     * @return true if an action invocation is running
     */
    public boolean isActionInProcess() {
        return actionInProcess.get();
    }

    /**
     * Returns a read only property that can be used to check if an action invocation is running (see {@link #invoke(String, Param...)})
     * @return read only property
     */
    public ReadOnlyBooleanProperty actionInProcessProperty() {
        return actionInProcess.getReadOnlyProperty();
    }

    /**
     * Returns the model that is synchronized between client and server. For more information see {@link com.canoo.dolphin.mapping.DolphinBean}
     * @return the model
     */
    public M getModel() {
        return model.get();
    }

    /**
     * Returns a read only property that contains the model that is synchronized between client and server.
     * For more information see {@link com.canoo.dolphin.mapping.DolphinBean}
     * @return read only property
     */
    public ReadOnlyObjectProperty<M> modelProperty() {
        return model.getReadOnlyProperty();
    }

    /**
     * This method is deprected and will be removed in a future version. Use {@link #onInvocationException(ControllerActionException)} instead.
     * @return
     */
    @Deprecated
    public ControllerActionException getInvocationException() {
        return invocationException.get();
    }

    /**
     * This method is deprected and will be removed in a future version. Use {@link #onInvocationException(ControllerActionException)} instead.
     * @return
     */
    @Deprecated
    public ReadOnlyObjectProperty<ControllerActionException> invocationExceptionProperty() {
        return invocationException.getReadOnlyProperty();
    }

    /**
     * This method is deprected and will be removed in a future version. Use {@link #onInitializationException(Throwable)} instead.
     * @return
     */
    @Deprecated
    public Throwable getInitializationException() {
        return initializationException.get();
    }

    /**
     * This method is deprected and will be removed in a future version. Use {@link #onInitializationException(Throwable)} instead.
     * @return
     */
    @Deprecated
    public ReadOnlyObjectProperty<Throwable> initializationExceptionProperty() {
        return initializationException.getReadOnlyProperty();
    }

    /**
     * This method will be called if an exception is thrown in the initialization of this view.
     * @param t the exception
     */
    protected void onInitializationException(Throwable t) {

    }

    /**
     * This method will be called if an exception is thrown in an action invocation.
     * @param e the exception
     */
    protected void onInvocationException(ControllerActionException e) {

    }

    /**
     * Returns the client context
     * @return the client context
     */
    public ClientContext getClientContext() {
        return clientContext;
    }

    /**
     * Returns the root node of the view.
     * @return the root node.
     */
    public abstract Node getRootNode();

    /**
     * Usefull helper method that returns the root node (see {@link #getRootNode()}) as a {@link Parent} if the root node
     * extends {@link Parent} or throws an runtime exception. This can be used to simply add a {@link AbstractFXMLViewBinder}
     * based view to a scene that needs a {@link Parent} as a root node.
     * @return the root node
     */
    public Parent getParent() {
        Node rootNode = getRootNode();
        if(rootNode == null) {
            throw new NullPointerException("The root node is null");
        }
        if (!(rootNode instanceof Parent)) {
            throw new IllegalStateException("The root node of this view is not a Parent");
        }
        return (Parent) rootNode;
    }

    protected ControllerProxy<M> getControllerProxy() {
        return controllerProxy;
    }
}
