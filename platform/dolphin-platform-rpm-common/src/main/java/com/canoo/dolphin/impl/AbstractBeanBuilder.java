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
package com.canoo.dolphin.impl;

import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.internal.BeanBuilder;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.ClassRepository;
import com.canoo.dolphin.internal.DolphinEventHandler;
import com.canoo.dolphin.internal.EventDispatcher;
import com.canoo.dolphin.internal.PresentationModelBuilder;
import com.canoo.dolphin.internal.UpdateSource;
import com.canoo.dolphin.internal.collections.ListMapper;
import com.canoo.dolphin.internal.info.ClassInfo;
import com.canoo.dolphin.internal.info.PropertyInfo;
import com.canoo.dolphin.mapping.Property;
import com.canoo.dp.impl.platform.core.Assert;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;


/**
 * A {@code BeanBuilder} is responsible for building a Dolphin Bean that is specified as a class. The main
 * (and only public) method is {@link #create(Class)}, which expects the {@code Class} of the Dolphin Bean and
 * returns the generated Bean.
 *
 * The generated Dolphin Bean will be registered in the {@link BeanRepositoryImpl}.
 */
public abstract class AbstractBeanBuilder implements BeanBuilder {

    private final ClassRepository classRepository;
    private final BeanRepository beanRepository;
    private final ListMapper listMapper;
    private final PresentationModelBuilderFactory builderFactory;

    public AbstractBeanBuilder(final ClassRepository classRepository, final BeanRepository beanRepository, final ListMapper listMapper, final PresentationModelBuilderFactory builderFactory, final EventDispatcher dispatcher) {
        this.classRepository = Assert.requireNonNull(classRepository, "classRepository");
        this.beanRepository = Assert.requireNonNull(beanRepository, "beanRepository");
        this.listMapper = listMapper;
        this.builderFactory = Assert.requireNonNull(builderFactory, "builderFactory");;

        dispatcher.addAddedHandler(new DolphinEventHandler() {
            @Override
            public void onEvent(final PresentationModel model) {
                Assert.requireNonNull(model, "model");
                final ClassInfo classInfo = classRepository.getClassInfo(model.getPresentationModelType());

                Assert.requireNonNull(classInfo, "classInfo");
                final Class<?> beanClass = classInfo.getBeanClass();

                createInstanceForClass(classInfo, beanClass, model, UpdateSource.OTHER);
            }
        });
    }

    public <T> T create(final Class<T> beanClass) {
        final ClassInfo classInfo = classRepository.getOrCreateClassInfo(beanClass);
        final PresentationModel model = buildPresentationModel(classInfo);

        return createInstanceForClass(classInfo, beanClass, model, UpdateSource.SELF);
    }

    private <T> T createInstanceForClass(final ClassInfo classInfo, final Class<T> beanClass, final PresentationModel model, final UpdateSource source) {
        Assert.requireNonNull(beanClass, "beanClass");
        try {
            final T bean = beanClass.newInstance();

            setupProperties(classInfo, bean, model);
            setupObservableLists(classInfo, bean, model);

            beanRepository.registerBean(bean, model, source);
            return bean;

        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Cannot create bean", e);
        }
    }

    private PresentationModel buildPresentationModel(final ClassInfo classInfo) {
        Assert.requireNonNull(classInfo, "classInfo");
        final PresentationModelBuilder builder = builderFactory.createBuilder()
                .withType(classInfo.getModelType());

        classInfo.forEachProperty(new ClassInfo.PropertyIterator() {
            @Override
            public void call(final PropertyInfo propertyInfo) {
                Assert.requireNonNull(propertyInfo, "propertyInfo");
                builder.withAttribute(propertyInfo.getAttributeName());
            }
        });

        return builder.create();
    }

    private void setupProperties(final ClassInfo classInfo, final Object bean, final PresentationModel model) {
        Assert.requireNonNull(classInfo, "classInfo");
        Assert.requireNonNull(model, "model");
        classInfo.forEachProperty(new ClassInfo.PropertyIterator() {
            @Override
            public void call(final PropertyInfo propertyInfo) {
                Assert.requireNonNull(propertyInfo, "propertyInfo");
                final Attribute attribute = model.getAttribute(propertyInfo.getAttributeName());
                final Property property = create(attribute, propertyInfo);
                propertyInfo.setPriviliged(bean, property);
            }
        });
    }

    private void setupObservableLists(final ClassInfo classInfo, final Object bean, final PresentationModel model) {
        Assert.requireNonNull(classInfo, "classInfo");
        classInfo.forEachObservableList(new ClassInfo.PropertyIterator() {
            @Override
            public void call(final PropertyInfo observableListInfo) {
                Assert.requireNonNull(observableListInfo, "observableListInfo");
                final ObservableList observableList = create(observableListInfo, model, listMapper);
                observableListInfo.setPriviliged(bean, observableList);
            }
        });
    }

    protected abstract <T> ObservableList<T> create(final PropertyInfo observableListInfo, final PresentationModel model, final ListMapper listMapper);


    protected abstract <T> Property<T> create(final Attribute attribute, final PropertyInfo propertyInfo);
}
