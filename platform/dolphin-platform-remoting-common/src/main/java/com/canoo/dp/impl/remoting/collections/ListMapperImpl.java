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
package com.canoo.dp.impl.remoting.collections;

import com.canoo.dp.impl.remoting.BeanRepository;
import com.canoo.dp.impl.remoting.ClassRepository;
import com.canoo.dp.impl.remoting.DolphinEventHandler;
import com.canoo.dp.impl.remoting.EventDispatcher;
import com.canoo.dp.impl.remoting.ListMapper;
import com.canoo.dp.impl.remoting.MappingException;
import com.canoo.dp.impl.remoting.PlatformRemotingConstants;
import com.canoo.dp.impl.remoting.PresentationModelBuilder;
import com.canoo.dp.impl.remoting.PresentationModelBuilderFactory;
import com.canoo.dp.impl.remoting.info.ClassInfo;
import com.canoo.dp.impl.remoting.info.PropertyInfo;
import com.canoo.dp.impl.remoting.legacy.core.PresentationModel;
import com.canoo.dp.impl.remoting.legacy.core.ModelStore;
import com.canoo.platform.remoting.ListChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ListMapperImpl implements ListMapper {

    private static final Logger LOG = LoggerFactory.getLogger(ListMapperImpl.class);

    private final BeanRepository beanRepository;
    private final ClassRepository classRepository;
    protected final PresentationModelBuilderFactory builderFactory;

    public ListMapperImpl(final ModelStore modelStore, ClassRepository classRepository, BeanRepository beanRepository, PresentationModelBuilderFactory builderFactory, EventDispatcher dispatcher) {
        this.beanRepository = beanRepository;
        this.classRepository = classRepository;
        this.builderFactory = builderFactory;

        dispatcher.addListSpliceHandler(new DolphinEventHandler() {
            @Override
            @SuppressWarnings("unchecked")
            public void onEvent(PresentationModel model) {
                try {
                    final String sourceId = model.getAttribute("source").getValue().toString();
                    final String attributeName = model.getAttribute("attribute").getValue().toString();

                    final Object bean = ListMapperImpl.this.beanRepository.getBean(sourceId);
                    final ClassInfo classInfo = ListMapperImpl.this.classRepository.getOrCreateClassInfo(bean.getClass());
                    final PropertyInfo observableListInfo = classInfo.getObservableListInfo(attributeName);

                    final ObservableArrayList list = (ObservableArrayList) observableListInfo.getPrivileged(bean);

                    final int from = ((Number) model.getAttribute("from").getValue()).intValue();
                    final int to = ((Number) model.getAttribute("to").getValue()).intValue();
                    final int count = ((Number) model.getAttribute("count").getValue()).intValue();

                    final List<Object> newElements = new ArrayList<Object>(count);
                    for (int i = 0; i < count; i++) {
                        final Object dolphinValue = model.getAttribute(Integer.toString(i)).getValue();
                        final Object value = observableListInfo.convertFromDolphin(dolphinValue);
                        newElements.add(value);
                    }

                    list.internalSplice(from, to, newElements);
                } catch (Exception ex) {
                    //TODO: This exception must be handled!
                    LOG.error("Invalid LIST_SPLICE command received: " + model, ex);
                } finally {
                    if (model != null) {
                        modelStore.remove(model);
                    }
                }

            }
        });
    }

    @Override
    public void processEvent(PropertyInfo observableListInfo, String sourceId, ListChangeEvent<?> event) {
        final String attributeName = observableListInfo.getAttributeName();

        for (final ListChangeEvent.Change<?> change : event.getChanges()) {

            final int from = change.getFrom();
            final int to = from + change.getRemovedElements().size();
            final List<?> newElements = event.getSource().subList(from, change.getTo());
            final int count = newElements.size();

            final PresentationModelBuilder builder = builderFactory.createBuilder();
            builder.withType(PlatformRemotingConstants.LIST_SPLICE)
                    .withAttribute("source", sourceId)
                    .withAttribute("attribute", attributeName)
                    .withAttribute("from", from)
                    .withAttribute("to", to)
                    .withAttribute("count", count);

            int i = 0;
            for (final Object current : newElements) {
                try {
                    builder.withAttribute(Integer.toString(i++), observableListInfo.convertToDolphin(current));
                }catch (Exception e) {
                    throw new MappingException("Error in event processing!", e);
                }
            }

            builder.create();
        }
    }
}
