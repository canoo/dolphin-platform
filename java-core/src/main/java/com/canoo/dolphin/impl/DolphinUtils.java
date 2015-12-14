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

import com.canoo.dolphin.internal.util.Assert;
import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.DolphinProperty;
import com.canoo.dolphin.mapping.Property;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import static com.canoo.dolphin.impl.ClassRepositoryImpl.FieldType.BASIC_TYPE;
import static com.canoo.dolphin.impl.ClassRepositoryImpl.FieldType.DOLPHIN_BEAN;

/**
 * The class {@code DolphinUtils} is a horrible class that we should get rid of asap.
 */
public class DolphinUtils {

    private DolphinUtils() {
    }

    public static String getDolphinAttributeName(PropertyDescriptor descriptor) {
        Assert.requireNonNull(descriptor, "descriptor");
        if (ReflectionHelper.isProperty(descriptor)) {
            return descriptor.getName().substring(0, descriptor.getName().length() - "Property".length());
        }
        return descriptor.getName();
    }

    public static String getDolphinAttributePropertyNameForField(Field propertyField) {
        Assert.requireNonNull(propertyField, "propertyField");
        String attributeName = propertyField.getName();
        DolphinProperty propertyAnnotation = propertyField.getAnnotation(DolphinProperty.class);
        if (propertyAnnotation != null && !propertyAnnotation.value().isEmpty()) {
            attributeName = propertyAnnotation.value();
        }
        return attributeName;
    }

    public static String getDolphinPresentationModelTypeForClass(Class<?> beanClass) {
        Assert.requireNonNull(beanClass, "beanClass");
        final DolphinBean beanAnnotation = beanClass.getAnnotation(DolphinBean.class);
        return beanAnnotation == null || beanAnnotation.value().isEmpty() ? beanClass.getName() : beanAnnotation.value();
    }

    public static <T> Property<T> getProperty(Object bean, String name) throws IllegalAccessException {
        Assert.requireNonNull(bean, "bean");
        Assert.requireNonNull(name, "name");
        for (Field field : ReflectionHelper.getInheritedDeclaredFields(bean.getClass())) {
            if (Property.class.isAssignableFrom(field.getType()) && name.equals(getDolphinAttributePropertyNameForField(field))) {
                return (Property<T>) ReflectionHelper.getPrivileged(field, bean);
            }
        }
        return null;
    }

    public static Object mapFieldTypeToDolphin(ClassRepositoryImpl.FieldType fieldType) {
        Assert.requireNonNull(fieldType, "fieldType");
        return fieldType.ordinal();
    }

    public static ClassRepositoryImpl.FieldType mapFieldTypeFromDolphin(Object value) {
        try {
            return ClassRepositoryImpl.FieldType.values()[(Integer) value];
        } catch (NullPointerException | ClassCastException | IndexOutOfBoundsException ex) {
            return ClassRepositoryImpl.FieldType.UNKNOWN;
        }
    }

    public static ClassRepositoryImpl.FieldType getFieldType(Object value) {
        if (value == null) {
            return ClassRepositoryImpl.FieldType.UNKNOWN;
        }
        return ReflectionHelper.isBasicType(value.getClass()) ? BASIC_TYPE : DOLPHIN_BEAN;
    }

}
