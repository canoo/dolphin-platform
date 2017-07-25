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
package com.canoo.dp.impl.server.beans;

import javax.servlet.ServletContext;

public interface ManagedBeanFactory {

    void init(ServletContext servletContext);

    <T> T createDependendInstance(Class<T> cls);

    <T> T createDependendInstance(Class<T> cls, PostConstructInterceptor<T> interceptor);

    <T> void destroyDependendInstance(T instance, Class<T> cls);
}
