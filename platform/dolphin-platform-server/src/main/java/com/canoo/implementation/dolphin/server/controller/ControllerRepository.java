/*
 * Copyright 2015-2016 Canoo Engineering AG.
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
package com.canoo.implementation.dolphin.server.controller;

import com.canoo.dolphin.server.DolphinController;
import com.canoo.implementation.dolphin.server.ClasspathScanner;
import com.canoo.implementation.dolphin.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This repository manages all Dolphin Platform controller classes (see {@link DolphinController}).
 * Internally the class uses the {@link ClasspathScanner} to find all controller classes.
 */
public class ControllerRepository {

    private Map<String, Class> controllersClasses;

    /**
     * Constructor
     */
    public ControllerRepository(final ClasspathScanner scanner) {
        Assert.requireNonNull(scanner, "scanner");

        controllersClasses = new HashMap<>();
        Set<Class<?>> foundControllerClasses = scanner.getTypesAnnotatedWith(DolphinController.class);
        for (Class<?> controllerClass : foundControllerClasses) {
            String name = controllerClass.getName();
            if (controllerClass.getAnnotation(DolphinController.class).value() != null && !controllerClass.getAnnotation(DolphinController.class).value().trim().isEmpty()) {
                name = controllerClass.getAnnotation(DolphinController.class).value();
            }
            controllersClasses.put(name, controllerClass);
        }
    }

    /**
     * Returns the controller class that is registered for the given name. For more information about controller
     * classes and the name definition see {@link DolphinController}
     * @param name the name
     * @return the controller class
     */
    public synchronized Class<?> getControllerClassForName(String name) {
        Assert.requireNonBlank(name, "name");
        Class<?> foundClass = controllersClasses.get(name);
        if(foundClass == null) {
            throw new IllegalArgumentException("Can't find controller type with name " + name);
        }
        return foundClass;
    }
}
