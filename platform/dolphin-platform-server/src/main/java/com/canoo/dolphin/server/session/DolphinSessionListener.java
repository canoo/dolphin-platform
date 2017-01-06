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
package com.canoo.dolphin.server.session;

/**
 * A listener interface to observe the lifecycle of a {@link DolphinSession}. Each implemenetation of this
 * interface that is annotated by {@link DolphinListener} will be created at runtime
 * and fired whenever a {@link DolphinSession} has been created or before it will be destroyed.
 * As long as the underlying platform supports it (like JavaEE or Spring) CDI is supported in listener implementations.
 */
public interface DolphinSessionListener {

    /**
     * This method will be called whenever a new {@link DolphinSession} has been created.
     * @param dolphinSession the dolphin session
     */
    void sessionCreated(DolphinSession dolphinSession);

    /**
     * This method will be called whenever a {@link DolphinSession} will be destroyed.
     * @param dolphinSession the dolphin session
     */
    void sessionDestroyed(DolphinSession dolphinSession);

}
