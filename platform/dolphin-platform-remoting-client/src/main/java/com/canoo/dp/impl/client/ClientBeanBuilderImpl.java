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
package com.canoo.dp.impl.client;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.remoting.AbstractBeanBuilder;
import com.canoo.dp.impl.remoting.BeanRepository;
import com.canoo.dp.impl.remoting.ClassRepository;
import com.canoo.dp.impl.remoting.EventDispatcher;
import com.canoo.dp.impl.remoting.ListMapper;
import com.canoo.dp.impl.remoting.PresentationModelBuilderFactory;
import com.canoo.dp.impl.remoting.PropertyImpl;
import com.canoo.dp.impl.remoting.collections.ObservableArrayList;
import com.canoo.dp.impl.remoting.info.PropertyInfo;
import com.canoo.dp.impl.remoting.legacy.core.Attribute;
import com.canoo.dp.impl.remoting.legacy.core.PresentationModel;
import com.canoo.platform.remoting.ListChangeEvent;
import com.canoo.platform.remoting.ObservableList;
import com.canoo.platform.remoting.Property;

public class ClientBeanBuilderImpl extends AbstractBeanBuilder {

    public ClientBeanBuilderImpl(ClassRepository classRepository, BeanRepository beanRepository, ListMapper listMapper, PresentationModelBuilderFactory builderFactory, EventDispatcher dispatcher) {
        super(classRepository, beanRepository, listMapper, builderFactory, dispatcher);
    }

    protected ObservableList create(final PropertyInfo observableListInfo, final PresentationModel model, final ListMapper listMapper) {
        Assert.requireNonNull(model, "model");
        Assert.requireNonNull(listMapper, "listMapper");
        return new ObservableArrayList() {
            @Override
            protected void notifyInternalListeners(ListChangeEvent event) {
                listMapper.processEvent(observableListInfo, model.getId(), event);
            }
        };
    }


    protected Property create(final Attribute attribute, final PropertyInfo propertyInfo) {
        return new PropertyImpl<>(attribute, propertyInfo);
    }
}
