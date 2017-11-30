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
package com.canoo.platform.core.functional;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * Defines a function interface that is used to handle a unsubscription or unregistration procedure.
 * Whenever you register for example a handler or listener in the Dolphin Platform API you will get a
 * {@link Subscription} instance as return value of the methods that does the registration. The {@link Subscription}
 * instance can be used to unregister / unsibscribe the registration by just calling the {@link #unsubscribe()} method.
 *
 * Example:
 *
 * <blockquote>
 * <pre>
 *     //Add a change handler to a property
 *     Subscription subscription = myProperty.onChange(e -> System.out.println("value changed"));
 *
 *     //Remove the change handler
 *     subscription.unsubscribe();
 * </pre>
 * </blockquote>
 *
 * @author Hendrik Ebbers
 *
 */
@API(since = "0.x", status = MAINTAINED)
@FunctionalInterface
public interface Subscription {

    /**
     * Unsusbscribe / unregister the handling that is defined by the {@link Subscription} instance.
     */
    void unsubscribe();
}
