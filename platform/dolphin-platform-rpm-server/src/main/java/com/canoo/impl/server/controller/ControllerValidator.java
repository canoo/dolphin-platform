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
package com.canoo.impl.server.controller;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.platform.core.ReflectionHelper;
import com.canoo.platform.server.DolphinAction;
import com.canoo.platform.server.DolphinModel;
import com.canoo.platform.server.Param;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * This class validates the DolphinController
 */
public class ControllerValidator {

    public void validate(Class<?> clazz) throws ControllerValidationException {

        Assert.requireNonNull(clazz, "Controller class");

        if (isInterface(clazz)) {
            throw new ControllerValidationException("Dolphin Controller " + clazz.getName() + " must be a class.");
        }
        if (isAbstract(clazz)) {
            throw new ControllerValidationException("Dolphin Controller " + clazz.getName() + " can't be abstract.");
        }
        if (isFinal(clazz)) {
            throw new ControllerValidationException("Dolphin Controller " + clazz.getName() + " can't be final.");
        }
        if (postConstructContainsParameter(clazz)) {
            throw new ControllerValidationException("PostConstruct method should not contain parameter in Controller " + clazz.getName());
        }
        if (isMoreThanOnePostConstruct(clazz)) {
            throw new ControllerValidationException("Only one PostConstruct method is allowed in Controller " + clazz.getName());
        }
        if (preDestroyContainsParameter(clazz)) {
            throw new ControllerValidationException("PreDestroy method should not contain parameter in Controller " + clazz.getName());
        }
        if (isMoreThanOnePreDestroy(clazz)) {
            throw new ControllerValidationException("Only one PreDestroy method is allowed in Controller " + clazz.getName());
        }
        if (!isDolphinActionVoid(clazz)) {
            throw new ControllerValidationException("DolphinAction must be void in Controller " + clazz.getName());
        }
        if (!isAnnotatedWithParam(clazz)) {
            throw new ControllerValidationException("DolphinAction parameters must be annotated with @param in Controller " + clazz.getName());
        }
        if (!isDolphinModelPresent(clazz)) {
            throw new ControllerValidationException("Controller " + clazz.getName() + " must have a DolphinModel.");
        }
        if (isMoreThanOneDolphinModel(clazz)) {
            throw new ControllerValidationException("Controller " + clazz.getName() + " should not contain more than one DolphinModel.");
        }
    }

    private boolean isInterface(Class<?> clazz) {
        return clazz.isInterface();
    }

    private boolean isAbstract(Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    private boolean isFinal(Class<?> clazz) {
        return Modifier.isFinal(clazz.getModifiers());
    }

    private boolean postConstructContainsParameter(Class<?> clazz) {
        List<Method> methods = ReflectionHelper.getInheritedDeclaredMethods(clazz);
        for (Method method : methods) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                return checkParameterLength(method);
            }
        }
        return false;
    }

    private boolean preDestroyContainsParameter(Class<?> clazz) {
        List<Method> methods = ReflectionHelper.getInheritedDeclaredMethods(clazz);
        for (Method method : methods) {
            if (method.isAnnotationPresent(PreDestroy.class)) {
                return checkParameterLength(method);
            }
        }
        return false;
    }

    private boolean checkParameterLength(Method method) {
        if (method.getParameterTypes().length > 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isDolphinActionVoid(Class<?> clazz) {
        List<Method> methods = ReflectionHelper.getInheritedDeclaredMethods(clazz);
        for (Method method : methods) {
            if (method.isAnnotationPresent(DolphinAction.class)) {
                return method.getReturnType().equals(Void.TYPE);
            }
        }
        return true;
    }

    private boolean isAnnotatedWithParam(Class<?> clazz) {
        List<Method> methods = ReflectionHelper.getInheritedDeclaredMethods(clazz);
        for (Method method : methods) {
            if (method.isAnnotationPresent(DolphinAction.class)) {
                Annotation[][] paramAnnotations = method.getParameterAnnotations();
                for(Annotation[] annotations : paramAnnotations) {
                    boolean paramAnnotationFound = false;
                    for(Annotation annotation : annotations) {
                        if (annotation.annotationType().equals(Param.class)) {
                            paramAnnotationFound = true;
                            break;
                        }
                    }
                    if(!paramAnnotationFound) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean isDolphinModelPresent(Class<?> clazz) {
        List<Field> fields = ReflectionHelper.getInheritedDeclaredFields(clazz);
        for (Field field : fields) {
            if (field.isAnnotationPresent(DolphinModel.class)) {
                return true;
            }
        }
        return false;
    }

    private boolean isMoreThanOneDolphinModel(Class<?> clazz) {
        List<Field> fields = ReflectionHelper.getInheritedDeclaredFields(clazz);
        int count = 0;
        for (Field field : fields) {
            if (field.isAnnotationPresent(DolphinModel.class)) {
                count++;
            }
        }
        return count > 1;
    }


    private boolean isMoreThanOnePreDestroy(Class<?> clazz) {
        int count = 0;
        List<Method> methods = ReflectionHelper.getInheritedDeclaredMethods(clazz);
        for (Method method : methods) {
            if (method.isAnnotationPresent(PreDestroy.class)) {
                count++;
            }
        }
        return count > 1;
    }

    private boolean isMoreThanOnePostConstruct(Class<?> clazz) {
        int count = 0;
        List<Method> methods = ReflectionHelper.getInheritedDeclaredMethods(clazz);
        for (Method method : methods) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                count++;
            }
        }
        return count > 1;
    }
}
