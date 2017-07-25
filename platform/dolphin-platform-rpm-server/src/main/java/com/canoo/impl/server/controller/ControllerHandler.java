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

import com.canoo.dolphin.converter.ValueConverterException;
import com.canoo.dolphin.impl.Converters;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.platform.core.ReflectionHelper;
import com.canoo.dp.impl.server.beans.ManagedBeanFactory;
import com.canoo.dp.impl.server.beans.PostConstructInterceptor;
import com.canoo.dp.impl.server.mbean.DolphinContextMBeanRegistry;
import com.canoo.dp.impl.server.mbean.beans.ModelProvider;
import com.canoo.impl.server.model.ServerBeanBuilder;
import com.canoo.platform.core.functional.Subscription;
import com.canoo.platform.server.DolphinAction;
import com.canoo.platform.server.DolphinModel;
import com.canoo.platform.server.Param;
import com.canoo.platform.server.ParentController;
import com.canoo.platform.server.PostChildCreated;
import com.canoo.platform.server.PreChildDestroyed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * This class wrapps the complete Dolphin Platform controller handling.
 * It defines the methods to create or destroy controllers and to interact with them.
 */
public class ControllerHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerHandler.class);

    private final Map<String, Object> controllers = new HashMap<>();

    private final Map<String, Class> controllerClassMapping = new HashMap<>();

    private final Map<String, Subscription> mBeanSubscriptions = new HashMap<>();

    private final Map<String, Object> models = new HashMap<>();

    private final Map<String, List<String>> parentChildRelations = new HashMap<>();

    private final Map<String, String> childToParentRelations = new HashMap<>();

    private final ManagedBeanFactory beanFactory;

    private final ServerBeanBuilder beanBuilder;

    private final ControllerRepository controllerRepository;

    private final DolphinContextMBeanRegistry mBeanRegistry;

    private final BeanRepository beanRepository;

    private final Converters converters;

    public ControllerHandler(final DolphinContextMBeanRegistry mBeanRegistry, final ManagedBeanFactory beanFactory, final ServerBeanBuilder beanBuilder, final BeanRepository beanRepository, final ControllerRepository controllerRepository, final Converters converters) {
        this.mBeanRegistry = Assert.requireNonNull(mBeanRegistry, "mBeanRegistry");
        this.beanFactory = Assert.requireNonNull(beanFactory, "beanFactory");
        this.beanBuilder = Assert.requireNonNull(beanBuilder, "beanBuilder");
        this.controllerRepository = Assert.requireNonNull(controllerRepository, "controllerRepository");
        this.beanRepository = Assert.requireNonNull(beanRepository, "beanRepository");
        this.converters = Assert.requireNonNull(converters, "converters");
    }

    public Object getControllerModel(String id) {
        return models.get(id);
    }

    public String createController(final String name, final String parentControllerId) {
        Assert.requireNonBlank(name, "name");
        final Class<?> controllerClass = controllerRepository.getControllerClassForName(name);

        if(controllerClass == null) {
            throw new ControllerCreationException("Can not find controller class for name " + name);
        }

        final String id = UUID.randomUUID().toString();
        final Object instance = beanFactory.createDependendInstance(controllerClass, new PostConstructInterceptor() {
            @Override
            public void intercept(Object controller) {
                attachModel(id, controller);
                if(parentControllerId != null) {
                    attachParent(id, controller, parentControllerId);
                }
            }
        });
        controllers.put(id, instance);
        controllerClassMapping.put(id, controllerClass);

        mBeanSubscriptions.put(id, mBeanRegistry.registerController(controllerClass, id, new ModelProvider() {
            @Override
            public Object getModel() {
                return models.get(id);
            }
        }));

        if(parentControllerId != null) {
            final Object parentController = controllers.get(parentControllerId);
            Assert.requireNonNull(parentController, "parentController");
            firePostChildCreated(parentController, instance);
        }

        LOG.trace("Created Controller of type %s and id %s for name %s", controllerClass.getName(), id, name);

        return id;
    }

    public void destroyController(final String id) {
        Assert.requireNonBlank(id, "id");

        final List<String> childControllerIds = parentChildRelations.remove(id);
        if(childControllerIds != null && !childControllerIds.isEmpty()) {
            for(String childControllerId : childControllerIds) {
                destroyController(childControllerId);
            }
        }

        final Object controller = controllers.remove(id);
        Assert.requireNonNull(controller, "controller");

        final String parentControllerId = childToParentRelations.remove(id);
        if(parentControllerId != null) {
            Object parentController = controllers.get(parentControllerId);
            Assert.requireNonNull(parentController, "parentController");
            firePreChildDestroyed(parentController, controller);
        }

        final Class controllerClass = controllerClassMapping.remove(id);
        beanFactory.destroyDependendInstance(controller, controllerClass);

        final Object model = models.remove(id);
        if (model != null) {
            beanRepository.delete(model);
        }

        final Subscription subscription = mBeanSubscriptions.remove(id);
        if(subscription != null) {
            subscription.unsubscribe();
        }
    }

    public void destroyAllControllers() {
        List<String> currentControllerIds = new ArrayList<>(getAllControllerIds());
        for(String id : currentControllerIds) {
            destroyController(id);
        }
    }

    private void firePostChildCreated(final Object parentController, final Object childController) {
        Assert.requireNonNull(parentController, "parentController");
        Assert.requireNonNull(childController, "childController");

        final List<Method> allMethods = ReflectionHelper.getInheritedDeclaredMethods(parentController.getClass());

        for(Method method : allMethods) {
            if(method.isAnnotationPresent(PostChildCreated.class)) {
                if(method.getParameters()[0].getType().isAssignableFrom(childController.getClass())) {
                    ReflectionHelper.invokePrivileged(method, parentController, childController);
                }
            }
        }
    }

    private void firePreChildDestroyed(final Object parentController, final Object childController) {
        final List<Method> allMethods = ReflectionHelper.getInheritedDeclaredMethods(parentController.getClass());

        for(Method method : allMethods) {
            if(method.isAnnotationPresent(PreChildDestroyed.class)) {
                if(method.getParameters()[0].getType().isAssignableFrom(childController.getClass())) {
                    ReflectionHelper.invokePrivileged(method, parentController, childController);
                }
            }
        }
    }

    private void attachModel(final String controllerId, final Object controller) {
        Assert.requireNonNull(controllerId, "controllerId");
        Assert.requireNonNull(controller, "controller");

        List<Field> allFields = ReflectionHelper.getInheritedDeclaredFields(controller.getClass());

        Field modelField = null;

        for (Field field : allFields) {
            if (field.isAnnotationPresent(DolphinModel.class)) {
                if (modelField != null) {
                    throw new RuntimeException("More than one Model was found for controller " + controller.getClass().getName());
                }
                modelField = field;
            }
        }

        if (modelField != null) {
            Object model = beanBuilder.createRootModel(modelField.getType());
            ReflectionHelper.setPrivileged(modelField, controller, model);
            models.put(controllerId, model);
        }
    }

    private void attachParent(final String controllerId, final Object controller, final String parentControllerId) {
        Assert.requireNonNull(controllerId, "controllerId");
        Assert.requireNonNull(controller, "controller");
        Assert.requireNonNull(parentControllerId, "parentControllerId");

        final List<Field> allFields = ReflectionHelper.getInheritedDeclaredFields(controller.getClass());

        Field parentField = null;

        for (Field field : allFields) {
            if (field.isAnnotationPresent(ParentController.class)) {
                if (parentField != null) {
                    throw new RuntimeException("More than one parent was found for controller " + controller.getClass().getName());
                }
                parentField = field;
            }
        }
        if (parentField != null) {
            final Object parentController = controllers.get(parentControllerId);
            Assert.requireNonNull(parentController, "parentController");

            if(!parentField.getType().getClass().isAssignableFrom(parentController.getClass())) {
                throw new RuntimeException("Parent controller in " + controller.getClass() + " defined of wrong type. Should be " + parentController.getClass());
            }
            ReflectionHelper.setPrivileged(parentField, controller, parentController);
            if(parentChildRelations.get(parentControllerId) == null) {
                parentChildRelations.put(parentControllerId, new ArrayList<String>());
            }
            parentChildRelations.get(parentControllerId).add(controllerId);
            childToParentRelations.put(controllerId, parentControllerId);
        }
    }

    public void invokeAction(final String controllerId, final String actionName, final Map<String, Object> params) throws InvokeActionException {
        Assert.requireNonBlank(controllerId, "controllerId");
        Assert.requireNonBlank(actionName, "actionName");
        Assert.requireNonNull(params, "params");

        try {
            final Object controller = controllers.get(controllerId);
            if(controller == null) {
                throw new InvokeActionException("No controller for id " + controllerId + " found");
            }
            final Class controllerClass = controllerClassMapping.get(controllerId);
            if(controllerClass == null) {
                throw new InvokeActionException("No controllerClass for id " + controllerId + " found");
            }
            final Method actionMethod = getActionMethod(controllerClass, actionName);
            if(actionMethod == null) {
                throw new InvokeActionException("No actionMethod with name " + actionName + " in controller class " + controllerClass.getName() + " found");
            }
            final List<Object> args = getArgs(actionMethod, params);
            LOG.debug("Will call {} action for controller {} ({}.{}) with {} params.", actionName, controllerId, controllerClass, actionMethod.getName(), args.size());
            if(LOG.isTraceEnabled()) {
                int index = 1;
                for(Object param : args) {
                    if(param != null) {
                        LOG.trace("Action param {}: {} with type {} is called with value \"{}\" and type {}", index, actionMethod.getParameters()[index - 1].getName(), actionMethod.getParameters()[index - 1].getType().getSimpleName(), param, param.getClass());
                    } else {
                        LOG.trace("Action param {}: {} with type {} is called with value null", index, actionMethod.getParameters()[index - 1].getName(), actionMethod.getParameters()[index - 1].getType().getSimpleName());
                    }
                    index++;
                }
            }
            ReflectionHelper.invokePrivileged(actionMethod, controller, args.toArray());
        } catch (InvokeActionException e) {
          throw e;
        } catch (Exception e) {
            throw new InvokeActionException("Can not call action '" + actionName + "'", e);
        }
    }

    private List<Object> getArgs(final Method method, final Map<String, Object> params) throws ValueConverterException {
        Assert.requireNonNull(method, "method");
        Assert.requireNonNull(params, "params");

        final int n = method.getParameterTypes().length;
        final List<Object> args = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            String paramName = Integer.toString(i);
            for (Annotation annotation : method.getParameterAnnotations()[i]) {
                if (annotation.annotationType().equals(Param.class)) {
                    final Param param = (Param) annotation;
                    if (param.value() != null && !param.value().isEmpty()) {
                        paramName = param.value();
                    }
                }
            }
            if(!params.containsKey(paramName)) {
                throw new IllegalArgumentException("No value for param " + paramName + " specified!");
            }
            Object value = params.get(paramName);
            Class<?> type = method.getParameters()[i].getType();
            if(value != null) {
                LOG.trace("Param check of value {} with type {} for param with type {}", value, value.getClass(), type);
                args.add(converters.getConverter(type).convertFromDolphin(value));
            } else {
                if(type.isPrimitive()) {
                    throw new IllegalArgumentException("Can not use 'null' for primitive type of parameter '" + paramName + "'");
                }
                args.add(null);
            }
        }
        return args;
    }

    public Set<String> getAllControllerIds() {
        return Collections.unmodifiableSet(controllers.keySet());
    }

    private <T> Method getActionMethod(Class<T> controllerClass, String actionName) {
        Assert.requireNonNull(controllerClass, "controllerClass");
        Assert.requireNonNull(actionName, "actionName");

        List<Method> allMethods = ReflectionHelper.getInheritedDeclaredMethods(controllerClass);
        Method foundMethod = null;
        for (Method method : allMethods) {
            if (method.isAnnotationPresent(DolphinAction.class)) {
                DolphinAction actionAnnotation = method.getAnnotation(DolphinAction.class);
                String currentActionName = method.getName();
                if (actionAnnotation.value() != null && !actionAnnotation.value().trim().isEmpty()) {
                    currentActionName = actionAnnotation.value();
                }
                if (currentActionName.equals(actionName)) {
                    if (foundMethod != null) {
                        throw new RuntimeException("More than one method for action " + actionName + " found in " + controllerClass);
                    }
                    foundMethod = method;
                }
            }
        }
        return foundMethod;
    }

    @SuppressWarnings("unchecked")
    public <T> List<? extends T> getAllControllersThatImplement(Class<T> cls) {
        final List<T> ret = new ArrayList<>();
        for (Object controller : controllers.values()) {
            if (cls.isAssignableFrom(controller.getClass())) {
                ret.add((T) controller);
            }
        }
        return ret;
    }

    public <T> T getControllerById(String id) {
        return (T) controllers.get(id);
    }
}
