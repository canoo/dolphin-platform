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
package com.canoo.dolphin.impl;

import com.canoo.dolphin.event.BeanAddedListener;
import com.canoo.dolphin.event.BeanRemovedListener;
import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.DolphinEventHandler;
import com.canoo.dolphin.internal.EventDispatcher;
import com.canoo.dolphin.internal.UpdateSource;
import com.canoo.dolphin.internal.util.Assert;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.PresentationModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@code BeanRepository} keeps a list of all registered Dolphin Beans and the mapping between OpenDolphin IDs and
 * the associated Dolphin Bean.
 *
 * A new bean needs to be registered with the {@link #registerBean(Object, PresentationModel, UpdateSource)} method and can be deleted
 * with the {@link #delete(Object)} method.
 */
// TODO mapDolphinToObject() does not really fit here, we should probably move it to Converters, but first we need to fix scopes
public class BeanRepositoryImpl implements BeanRepository{

    private final Map<Object, PresentationModel> objectPmToDolphinPm = new HashMap<>();
    private final Map<String, Object> dolphinIdToObjectPm = new HashMap<>();
    private final Dolphin dolphin;
    private final Multimap<Class<?>, BeanAddedListener<?>> beanAddedListenerMap = ArrayListMultimap.create();
    private List<BeanAddedListener<Object>> anyBeanAddedListeners = new ArrayList<>();
    private final Multimap<Class<?>, BeanRemovedListener<?>> beanRemovedListenerMap = ArrayListMultimap.create();
    private List<BeanRemovedListener<Object>> anyBeanRemovedListeners = new ArrayList<>();

    public BeanRepositoryImpl(Dolphin dolphin, EventDispatcher dispatcher) {
        Assert.requireNonNull(dolphin, "dolphin");
        Assert.requireNonNull(dispatcher, "dispatcher");
        this.dolphin = dolphin;

        dispatcher.addRemovedHandler(new DolphinEventHandler() {
            @SuppressWarnings("unchecked")
            @Override
            public void onEvent(PresentationModel model) {
                final Object bean = dolphinIdToObjectPm.remove(model.getId());
                if (bean != null) {
                    objectPmToDolphinPm.remove(bean);
                    for (final BeanRemovedListener beanRemovedListener : beanRemovedListenerMap.get(bean.getClass())) {
                        beanRemovedListener.beanDestructed(bean);
                    }
                    for (final BeanRemovedListener beanRemovedListener : anyBeanRemovedListeners) {
                        beanRemovedListener.beanDestructed(bean);
                    }
                }
            }
        });
    }

    public <T> Subscription addOnAddedListener(final Class<T> clazz, final BeanAddedListener<? super T> listener) {
        beanAddedListenerMap.put(clazz, listener);
        return new Subscription() {
            @Override
            public void unsubscribe() {
                beanAddedListenerMap.remove(clazz, listener);
            }
        };
    }

    public Subscription addOnAddedListener(final BeanAddedListener<Object> listener) {
        anyBeanAddedListeners.add(listener);
        return new Subscription() {
            @Override
            public void unsubscribe() {
                anyBeanAddedListeners.remove(listener);
            }
        };
    }

    public <T> Subscription addOnRemovedListener(final Class<T> clazz, final BeanRemovedListener<? super T> listener) {
        beanRemovedListenerMap.put(clazz, listener);
        return new Subscription() {
            @Override
            public void unsubscribe() {
                beanRemovedListenerMap.remove(clazz, listener);
            }
        };
    }

    public Subscription addOnRemovedListener(final BeanRemovedListener<Object> listener) {
        anyBeanRemovedListeners.add(listener);
        return new Subscription() {
            @Override
            public void unsubscribe() {
                anyBeanRemovedListeners.remove(listener);
            }
        };
    }

    public boolean isManaged(Object bean) {
        return objectPmToDolphinPm.containsKey(bean);
    }

    public <T> void delete(T bean) {
        final PresentationModel model = objectPmToDolphinPm.remove(bean);
        if (model != null) {
            dolphinIdToObjectPm.remove(model.getId());
            dolphin.remove(model);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> findAll(Class<T> beanClass) {
        final List<T> result = new ArrayList<>();
        final List<PresentationModel> presentationModels = dolphin.findAllPresentationModelsByType(DolphinUtils.getDolphinPresentationModelTypeForClass(beanClass));
        for (PresentationModel model : presentationModels) {
            result.add((T) dolphinIdToObjectPm.get(model.getId()));
        }
        return result;
    }

    public Object getBean(String sourceId) {
        return dolphinIdToObjectPm.get(sourceId);
    }

    public String getDolphinId(Object bean) {
        try {
            return objectPmToDolphinPm.get(bean).getId();
        } catch (NullPointerException ex) {
            throw new IllegalArgumentException("Only managed Dolphin Beans can be used.", ex);
        }
    }

    public Object mapDolphinToObject(Object value, ClassRepositoryImpl.FieldType fieldType) {
        return fieldType == ClassRepositoryImpl.FieldType.DOLPHIN_BEAN? dolphinIdToObjectPm.get(value) : value;
    }

    @SuppressWarnings("unchecked")
    public void registerBean(Object bean, PresentationModel model, UpdateSource source) {
        objectPmToDolphinPm.put(bean, model);
        dolphinIdToObjectPm.put(model.getId(), bean);

        if (source == UpdateSource.OTHER) {
            for (final BeanAddedListener beanAddedListener : beanAddedListenerMap.get(bean.getClass())) {
                beanAddedListener.beanCreated(bean);
            }
            for (final BeanAddedListener beanAddedListener : anyBeanAddedListeners) {
                beanAddedListener.beanCreated(bean);
            }
        }
    }
}
