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
import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;
import com.canoo.dp.impl.platform.core.Assert;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

/**
 * The class {@code DolphinUtils} is a horrible class that we should get rid of asap.
 */
public class DolphinUtils {

    private DolphinUtils() {
    }

    public static String getDolphinAttributePropertyNameForField(Field propertyField) {
        return propertyField.getName();
    }

    public static String getDolphinPresentationModelTypeForClass(Class<?> beanClass) {
        return assertIsDolphinBean(beanClass).getName();
    }

    public static <T> T assertIsDolphinBean(T bean) {
        Assert.requireNonNull(bean, "bean");
        assertIsDolphinBean(bean.getClass());
        return bean;
    }

    public static <T> Class<T> assertIsDolphinBean(Class<T> beanClass) {
        if (!isDolphinBean(beanClass)) {
            throw new BeanDefinitionException(beanClass);
        }
        return beanClass;
    }

    public static boolean isDolphinBean(Class<?> beanClass) {
        Assert.requireNonNull(beanClass, "beanClass");
        return beanClass.isAnnotationPresent(DolphinBean.class);
    }


    public static boolean isEnumType(final Class<?> cls) {
        Assert.requireNonNull(cls, "cls");
        return cls.isEnum();
    }

    public static boolean isAllowedForUnmanaged(final Class<?> cls) {
        return isBasicType(cls) || isProperty(cls) || isEnumType(cls);
    }

    public static boolean isProperty(final PropertyDescriptor descriptor) {
        Assert.requireNonNull(descriptor, "descriptor");
        return isProperty(descriptor.getPropertyType());
    }

    public static boolean isProperty(final Class<?> propertyType) {
        return Property.class.isAssignableFrom(propertyType);
    }

    public static boolean isObservableList(final Class<?> propertyType) {
        return ObservableList.class.isAssignableFrom(propertyType);
    }


    public static boolean isBasicType(final Class<?> cls) {
        Assert.requireNonNull(cls, "cls");
        return cls.isPrimitive() || cls.equals(String.class) || cls.equals(Boolean.class) || cls.equals(Byte.class) || Number.class.isAssignableFrom(cls);
    }
}
