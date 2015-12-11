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
import com.canoo.dolphin.mapping.Property;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectionHelper {


    public static Object getPrivileged(final Field field, final Object bean) {
        Assert.requireNonNull(field, "field");
        Assert.requireNonNull(bean, "bean");
        return AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                boolean wasAccessible = field.isAccessible();
                try {
                    field.setAccessible(true);
                    return field.get(bean);
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    throw new IllegalStateException("Cannot set field: "
                            + field, ex);
                } finally {
                    field.setAccessible(wasAccessible);
                }
            }
        });
    }

    public static void setPrivileged(final Field field, final Object bean,
                                     final Object value) {
        Assert.requireNonNull(field, "field");
        Assert.requireNonNull(bean, "bean");
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                boolean wasAccessible = field.isAccessible();
                try {
                    field.setAccessible(true);
                    field.set(bean, value);
                    return null; // return nothing...
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    throw new IllegalStateException("Cannot set field: "
                            + field, ex);
                } finally {
                    field.setAccessible(wasAccessible);
                }
            }
        });
    }

    public static void invokePrivileged(final Method method, final Object instance, final Object... args) {
        Assert.requireNonNull(method, "method");
        Assert.requireNonNull(instance, "instance");
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                boolean wasAccessible = method.isAccessible();
                try {
                    method.setAccessible(true);
                    method.invoke(instance, args);
                    return null; // return nothing...
                } catch (InvocationTargetException | IllegalAccessException ex) {
                    throw new IllegalStateException("Cannot invoke method: "
                            + method, ex);
                } finally {
                    method.setAccessible(wasAccessible);
                }
            }
        });
    }

    public static List<Field> getInheritedDeclaredFields(Class<?> type) {
        List<Field> result = new ArrayList<>();
        Class<?> i = type;
        while (i != null && i != Object.class) {
            result.addAll(Arrays.asList(i.getDeclaredFields()));
            i = i.getSuperclass();
        }
        return result;
    }

    public static List<Method> getInheritedDeclaredMethods(Class<?> type) {
        List<Method> result = new ArrayList<>();
        Class<?> i = type;
        while (i != null && i != Object.class) {
            result.addAll(Arrays.asList(i.getDeclaredMethods()));
            i = i.getSuperclass();
        }
        return result;
    }

    public static boolean isProperty(PropertyDescriptor descriptor) {
        Assert.requireNonNull(descriptor, "descriptor");
        return isProperty(descriptor.getPropertyType());
    }

    public static boolean isProperty(Class<?> propertyType) {
        return Property.class.isAssignableFrom(propertyType);
    }

    public static boolean isEnumType(Class<?> cls) {
        Assert.requireNonNull(cls, "cls");
        return cls.isEnum();
    }

    public static boolean isAllowedForUnmanaged(Class<?> cls) {
        return isBasicType(cls) || isProperty(cls) || isEnumType(cls);
    }

    public static boolean isBasicType(Class<?> cls) {
        Assert.requireNonNull(cls, "cls");
        return cls.isPrimitive() || cls.equals(String.class) || cls.equals(Boolean.class) || cls.equals(Byte.class) || Number.class.isAssignableFrom(cls);
    }

    public static boolean isProxyInstance(Object bean) {
        Assert.requireNonNull(bean, "bean");
        return Proxy.isProxyClass(bean.getClass());
    }
}
