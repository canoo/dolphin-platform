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
package com.canoo.impl.server.model;

import com.canoo.dolphin.collections.ListChangeEvent;
import com.canoo.dolphin.collections.ListChangeListener;
import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.event.ValueChangeEvent;
import com.canoo.dolphin.impl.AbstractBeanBuilder;
import com.canoo.dolphin.impl.PresentationModelBuilderFactory;
import com.canoo.dolphin.impl.PropertyImpl;
import com.canoo.dolphin.impl.collections.ObservableArrayList;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.ClassRepository;
import com.canoo.dolphin.internal.EventDispatcher;
import com.canoo.dolphin.internal.collections.ListMapper;
import com.canoo.dolphin.internal.info.PropertyInfo;
import com.canoo.dolphin.mapping.Property;
import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.impl.server.gc.GarbageCollector;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;

public class ServerBeanBuilderImpl extends AbstractBeanBuilder implements ServerBeanBuilder {

    private final GarbageCollector garbageCollector;

    public ServerBeanBuilderImpl(final ClassRepository classRepository, final BeanRepository beanRepository, final ListMapper listMapper, final PresentationModelBuilderFactory builderFactory, final EventDispatcher dispatcher, final GarbageCollector garbageCollector) {
        super(classRepository, beanRepository, listMapper, builderFactory, dispatcher);
        this.garbageCollector = Assert.requireNonNull(garbageCollector, "garbageCollector");
    }

    public <T> T createRootModel(Class<T> beanClass) {
        T bean = super.create(beanClass);
        garbageCollector.onBeanCreated(bean, true);
        return bean;
    }

    @Override
    public <T> T create(Class<T> beanClass) {
        T bean = super.create(beanClass);
        garbageCollector.onBeanCreated(bean, false);
        return bean;
    }

    protected <T> ObservableList<T> create(final PropertyInfo observableListInfo, final PresentationModel model, final ListMapper listMapper) {
        Assert.requireNonNull(model, "model");
        Assert.requireNonNull(listMapper, "listMapper");
        final ObservableList<T> list = new ObservableArrayList<T>() {
            @Override
            protected void notifyInternalListeners(ListChangeEvent<T> event) {
                listMapper.processEvent(observableListInfo, model.getId(), event);
            }
        };

        list.onChanged(new ListChangeListener<T>() {
            @Override
            public void listChanged(ListChangeEvent<? extends T> event) {
                for(ListChangeEvent.Change<? extends T> c : event.getChanges()) {
                    if(c.isAdded()) {
                        for(Object added : list.subList(c.getFrom(), c.getTo())) {
                            garbageCollector.onAddedToList(list, added);
                        }
                    }
                    if(c.isRemoved()) {
                        for(Object removed : c.getRemovedElements()) {
                            garbageCollector.onRemovedFromList(list, removed);
                        }
                    }
                    if(c.isReplaced()) {
                        //??? TODO
                    }
                }
            }
        });

        return list;
    }

    protected <T> Property<T> create(final Attribute attribute, final PropertyInfo propertyInfo) {
        return new PropertyImpl<T>(attribute, propertyInfo) {

            @Override
            protected void notifyInternalListeners(ValueChangeEvent event) {
                super.notifyInternalListeners(event);
                garbageCollector.onPropertyValueChanged(event.getSource(), event.getOldValue(), event.getNewValue());
            }
        };
    }
}

