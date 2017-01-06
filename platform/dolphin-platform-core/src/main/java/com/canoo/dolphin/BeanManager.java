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
package com.canoo.dolphin;

import com.canoo.dolphin.event.Subscription;
import com.canoo.implementation.dolphin.BeanAddedListener;
import com.canoo.implementation.dolphin.BeanRemovedListener;

import java.util.Collection;
import java.util.List;

/**
 * <p>The {@link BeanManager} defined the low level API of the Dolphin Platform to create synchronized models. A model
 * instance that is created by using the bean manager will automatically synchronized between the client and server.
 * <br>
 * <center><img src="doc-files/sync.png" alt="model is synchronized between client and server"></center>
 * </p>
 * <p>In the Dolphin Platform architecture an application is normally defined by several MVC groups with a server side
 * controller, a client side view and a synchronized presentation model. The {@link BeanManager}
 * defines the basic mechanism to handle synchronized models and can be used in the MVC group to mutate the defined model
 * or as a standalone API to create any kind of synchronized model.
 * <br>
 * <center><img src="doc-files/mvc-sync.png" alt="model is synchronized between client view and server controller"></center>
 * </p>
 * <p>When using the MVC API of Dolphin Platform the lifecycle of the model is defined by the MVC group and the model
 * will automatically be removed when the MVC group is removed. If the {@link BeanManager} is used as
 * standalone API the developer must handle the lifecycle of the models and removePresentationModel them by using the {@link BeanManager}</p>
 * <p>All synchronized models must follow some specific rules that are described in the {@link com.canoo.dolphin.mapping.DolphinBean} annotation</p>
 *
 * <p>By using the default Spring or JavaEE implementation of the Dolphin platform the {@link BeanManager}
 * will be provided as a managed bean and can be injected wherever the container allows injection.</p>
 *
 * <p>To create a new model the {@link BeanManager} provides the {@link #create(Class)} method that is
 * defined as a factory method for any kind of model. A model should never be instantiated by hand because in that case
 * the model won't be synchronized between client and server. Here is an example how a model can be created:
 * <blockquote>
 * <pre>
 *     MyModel model = beanManager.create(MyModel.class);
 * </pre>
 * </blockquote>
 * </p>
 * <p>The {@link BeanManager} provides several methods to observe the creation and deletion of models.
 * One example is the {@link #onAdded(Class, BeanAddedListener)} method. All the methods are
 * for using lambdas and therefore a handler can be easily added with only one line if code:
 * <blockquote>
 * <pre>
 *     beanManager.onAdded(MyModel.class, model -> System.out.println("Model of type MyModel added"));
 * </pre>
 * </blockquote>
 * There are no method to removePresentationModel registered handler from the {@link BeanManager}. Here Dolphin Platform
 * implement an approach by using the Subscription Pattern: Each hander registration returns a {@link com.canoo.dolphin.event.Subscription}
 * instance that provides the {@link com.canoo.dolphin.event.Subscription#unsubscribe()} method to removePresentationModel the handler.
 * </p>
 * <p>To deleta a synchronized model the {@link BeanManager} provides several methods. Here a developer can
 * for example choose to delete a specific instance (see {@link #remove(Object)}) or all instances for a given type (see {@link #removeAll(Class)}).</p>
 */
public interface BeanManager {

    /**
     * Checks if the given object is a dolphin bean that is synced with the client
     *
     * @param bean the object
     * @return true if the given object is a dolphin bean
     */
    boolean isManaged(Object bean);

    /**
     * Creates a new instance of the given dolphin bean class that will automatically be synced with the client.
     * The given class must be defined as a dolphin bean
     *
     * @param beanClass the bean class
     * @param <T>       bean type
     * @return the new bean instance
     */
    <T> T create(Class<T> beanClass);

    /**
     * Remove the given managed dolphin bean. by calling this method the given bean will become unmanaged and won't be
     * synced with the client.
     *
     * @param bean the bean
     * @deprecated with the new garbage collection support you should never removePresentationModel a bean by hand. Just set the property
     * value to null and the gc will removePresentationModel the bean.
     */
    @Deprecated
    void remove(Object bean);

    /**
     * Remove all beans of the given type.
     *
     * @param beanClass the class that defines the bean type.
     * @deprecated with the new garbage collection support you should never removePresentationModel a bean by hand. Just set the property
     * value to null and the gc will removePresentationModel the bean.
     */
    @Deprecated
    void removeAll(Class<?> beanClass);

    /**
     * Remove all given beans.
     *
     * @param beans the beans that should be removed.
     * @deprecated with the new garbage collection support you should never removePresentationModel a bean by hand. Just set the property
     * value to null and the gc will removePresentationModel the bean.
     */
    @Deprecated
    void removeAll(Object... beans);

    /**
     * Remove all beans of the given type.
     *
     * @param beans the beans that should be removed.
     * @deprecated with the new garbage collection support you should never removePresentationModel a bean by hand. Just set the property
     * value to null and the gc will removePresentationModel the bean.
     */
    @Deprecated
    void removeAll(Collection<?> beans);

    /**
     * Returns a list of all dolphin managed beans of the given type / class
     *
     * @param beanClass the bean type
     * @param <T>       the bean type
     * @return a list of all managed beans of the type
     */
    @Deprecated
    <T> List<T> findAll(Class<T> beanClass);

    /**
     * Subscribe a listener to all bean creation events for a specific class. A listener that is added to a client side
     * {@link BeanManager} will only fire events for beans that where created on the server side and vice versa. This means that the listener
     * isn't fired for a bean that was created by the same {@link BeanManager}.
     *
     * @param beanClass the class for which creation events should be received
     * @param listener the listener which receives the creation-events
     * @param <T> the bean type
     * @return the (@link com.canoo.dolphin.event.Subscription} that can be used to unsubscribe the listener
     */
    <T> Subscription onAdded(Class<T> beanClass, BeanAddedListener<? super T> listener);

    /**
     * Subscribe a listener to all bean creation events. A listener that is added to a client side
     * {@link BeanManager} will only fire events for beans that where created on the server side and vice versa. This means that the listener
     * isn't fired for a bean that was created by the same {@link BeanManager}.
     *
     * @param listener the listener which receives the creation events
     * @return the (@link com.canoo.dolphin.event.Subscription} that can be used to unsubscribe the listener
     */
    Subscription onAdded(BeanAddedListener<Object> listener);

    /**
     * Subscribe a listener to all bean destruction events for a specific class. A listener that is added to a client side
     * {@link BeanManager} will only fire events for beans that where removed on the server side and vice versa. This means that the listener
     * isn't fired for a bean that was removed by the same {@link BeanManager}.
     *
     * @param beanClass the class for which destruction events should be received
     * @param listener the listener which receives the destruction events
     * @return the (@link com.canoo.dolphin.event.Subscription} that can be used to unsubscribe the listener
     */
    <T> Subscription onRemoved(Class<T> beanClass, BeanRemovedListener<? super T> listener);

    /**
     * Subscribe a listener to all bean destruction events. A listener that is added to a client side
     * {@link BeanManager} will only fire events for beans that where removed on the server side and vice versa. This means that the listener
     * isn't fired for a bean that was removed by the same {@link BeanManager}.
     *
     * @param listener the listener which receives the destruction events
     * @return the (@link com.canoo.dolphin.event.Subscription} that can be used to unsubscribe the listener
     */
    Subscription onRemoved(BeanRemovedListener<Object> listener);

}
