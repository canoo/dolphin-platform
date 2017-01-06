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
package com.canoo.implementation.dolphin.server;

import com.canoo.implementation.dolphin.BeanBuilder;

/**
 * Interface that defines the {@link BeanBuilder} for the server
 */
public interface ServerBeanBuilder extends BeanBuilder {

   /**
    * Method to create a root mode. A root model is the model that is defined as a controller in a MVC group.
    * All other models are submodels since they will be part of a model hierarchy that starts with a root model.
    * To create a model instance that is not a root model see {@link BeanBuilder#create(Class)}.
    *
    * @param beanClass the model class
    * @param <T> type of the model
    * @return the created model instance
     */
   <T> T createRootModel(Class<T> beanClass);
}
