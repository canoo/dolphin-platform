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
package com.canoo.platform.server.spi;

import com.canoo.dp.impl.server.beans.ManagedBeanFactory;
import com.canoo.platform.core.PlatformThreadFactory;

import javax.servlet.ServletContext;

public interface ServerCoreComponents {

    ServletContext getServletContext();

    PlatformConfiguration getConfiguration();

    PlatformThreadFactory getThreadFactory();

    ClasspathScanner getClasspathScanner();

    ManagedBeanFactory getManagedBeanFactory();

    <T> void provideInstance(Class<T> cls, T instance);

    <T> T getInstance(Class<T> cls);
}
