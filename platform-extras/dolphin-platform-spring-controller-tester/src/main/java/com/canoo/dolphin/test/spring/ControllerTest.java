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
package com.canoo.dolphin.test.spring;

/**
 * Basic interface for testing a Dolpgin Platform controller (see {@link com.canoo.dolphin.server.DolphinController}).
 * The interface provides testable controllers.
 */
public interface ControllerTest {

    /**
     * Creates a {@link ControllerUnderTest} for the given controller name. See {@link com.canoo.dolphin.server.DolphinController}
     * for the name definition. The {@link ControllerUnderTest} instance that is created by this method can be used to
     * interact with the controller or access the model.
     * @param controllerName the controller name
     * @param <T> type of the model
     * @return the created {@link ControllerUnderTest} instance.
     */
    <T> ControllerUnderTest<T> createController(String controllerName);

}
