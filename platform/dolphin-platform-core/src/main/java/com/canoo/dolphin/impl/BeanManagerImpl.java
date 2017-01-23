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

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.event.BeanAddedListener;
import com.canoo.dolphin.event.BeanRemovedListener;
import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.internal.BeanBuilder;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.common.Assert;

import java.util.Collection;
import java.util.List;

public class BeanManagerImpl implements BeanManager {

    protected final BeanRepository beanRepository;
    private final BeanBuilder beanBuilder;

    public BeanManagerImpl(final BeanRepository beanRepository, final BeanBuilder beanBuilder) {
        this.beanRepository = Assert.requireNonNull(beanRepository, "beanRepository");
        this.beanBuilder = Assert.requireNonNull(beanBuilder, "beanBuilder");
    }

    @Override
    public boolean isManaged(final Object bean) {
        DolphinUtils.assertIsDolphinBean(bean);
        return beanRepository.isManaged(bean);
    }

    @Override
    public <T> T create(final Class<T> beanClass) {
        DolphinUtils.assertIsDolphinBean(beanClass);
        return beanBuilder.create(beanClass);
    }

    @Override
    public void remove(final Object bean) {
        DolphinUtils.assertIsDolphinBean(bean);
        beanRepository.delete(bean);
    }

    @Override
    public void removeAll(final Class<?> beanClass) {
        DolphinUtils.assertIsDolphinBean(beanClass);
        for (Object bean : findAll(beanClass)) {
            DolphinUtils.assertIsDolphinBean(bean);
            beanRepository.delete(bean);
        }
    }

    @Override
    public void removeAll(final Object... beans) {
        Assert.requireNonNull(beans, "beans");
        for (final Object bean : beans) {
            DolphinUtils.assertIsDolphinBean(bean);
            remove(bean);
        }
    }

    @Override
    public void removeAll(final Collection<?> beans) {
        Assert.requireNonNull(beans, "beans");
        for (final Object bean : beans) {
            DolphinUtils.assertIsDolphinBean(bean);
            remove(bean);
        }
    }

    @Override
    public <T> List<T> findAll(final Class<T> beanClass) {
        DolphinUtils.assertIsDolphinBean(beanClass);
        return beanRepository.findAll(beanClass);
    }

    @Override
    public <T> Subscription onAdded(final Class<T> beanClass, final BeanAddedListener<? super T> listener) {
        DolphinUtils.assertIsDolphinBean(beanClass);
        return beanRepository.addOnAddedListener(beanClass, listener);
    }

    @Override
    public Subscription onAdded(final BeanAddedListener<Object> listener) {
        return beanRepository.addOnAddedListener(listener);
    }

    @Override
    public <T> Subscription onRemoved(final Class<T> beanClass, final BeanRemovedListener<? super T> listener) {
        DolphinUtils.assertIsDolphinBean(beanClass);
        return beanRepository.addOnRemovedListener(beanClass, listener);
    }

    @Override
    public Subscription onRemoved(final BeanRemovedListener<Object> listener) {
        return beanRepository.addOnRemovedListener(listener);
    }

}
