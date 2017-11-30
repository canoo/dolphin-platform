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
package com.canoo.platform.remoting.server.binding;

import com.canoo.platform.core.functional.Binding;
import com.canoo.platform.remoting.Property;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * A component that can be use to create bindings between properties (see {@link Property}). All properties that are
 * bound to the same qualifier (see {@link Qualifier}) will be automatically updated once one of the properties change
 * its value.
 *
 * @author Hendrik Ebbers
 */
@API(since = "0.x", status = EXPERIMENTAL)
@FunctionalInterface
public interface PropertyBinder {

    /**
     * Method to bind a property to a qualifier
     * @param property the property
     * @param qualifier the qualifier
     * @param <T> generic type of the property
     * @return a binding that can be used to unbind the property
     */
    <T> Binding bind(Property<T> property, Qualifier<T> qualifier);

}
