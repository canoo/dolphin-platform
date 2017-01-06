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
package com.canoo.implementation.dolphin.server.container;

import com.canoo.dolphin.server.session.DolphinListener;
import com.canoo.dolphin.server.session.DolphinSessionListener;

import javax.servlet.ServletContext;

/**
 * <p>
 * This interface defines the platform / container specific controller management.
 * By default Dolphin Platform provides 2 implementations of this interface for Spring and JavaEE that
 * can be found in the specific modules. If you want to add the support for a different platform you need to
 * provide a custom implementation of this interface.
 * </p>
 * <p>
 * Here is a short overview how the architecture is defined:
 * <br>
 * <center><img src="doc-files/platform-impl.png" alt="model is synchronized between client and server"></center>
 * </p>
 */
public interface ContainerManager {

    /**
     * This method must be called before the {@link ContainerManager} instance can be used. Some specific implementations
     * needs access to the {@link ServletContext} that is set by calling this method.
     * @param servletContext the servlet context
     */
    void init(ServletContext servletContext);

    /**
     * Creates a new managed instance for the given controller class.
     * @param controllerClass the class of the controller
     * @param modelInjector a injector that will be called to inject the model in the controller
     * @param <T> type of the controller
     * @return the new controller instance
     */
    <T> T createManagedController(Class<T> controllerClass, ModelInjector modelInjector);

    /**
     * Creates a new managed instance. See {@link DolphinListener} and
     * {@link DolphinSessionListener} for more information
     * @param listenerClass the class of the listenerClass
     * @param <T> type of the listener
     * @return the new listener instance
     */
    <T> T createListener(Class<T> listenerClass);

    /**
     * Destroyes the given controller instance
     * @param instance controller instance
     * @param controllerClass type of controller
     */
    void destroyController(Object instance, Class controllerClass);

}
