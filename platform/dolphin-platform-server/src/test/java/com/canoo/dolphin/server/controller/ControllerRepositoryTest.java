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
package com.canoo.dolphin.server.controller;

import com.canoo.implementation.dolphin.server.ClasspathScanner;
import com.canoo.implementation.dolphin.server.controller.ControllerRepository;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created by hendrikebbers on 17.02.16.
 */
public class ControllerRepositoryTest {

    @Test
    public void testExistingControllers() {
        ControllerRepository controllerRepository = new ControllerRepository(new ClasspathScanner());
        Class<?> controllerClass = controllerRepository.getControllerClassForName(TestController.class.getName());
        assertNotNull(controllerClass);
        assertEquals(controllerClass, TestController.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testWrongControllersName() {
        ControllerRepository controllerRepository = new ControllerRepository(new ClasspathScanner());
        Class<?> controllerClass = controllerRepository.getControllerClassForName("WrongControllerName");
    }

}
