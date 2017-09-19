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
package com.canoo.dp.impl.client;

import com.canoo.dp.impl.client.legacy.ClientModelStore;
import com.canoo.dp.impl.remoting.BeanRepository;
import com.canoo.dp.impl.remoting.Converters;
import com.canoo.dp.impl.remoting.DolphinEventHandler;
import com.canoo.dp.impl.remoting.EventDispatcher;
import com.canoo.dp.impl.remoting.InternalAttributesBean;
import com.canoo.dp.impl.remoting.legacy.core.PresentationModel;
import com.canoo.platform.remoting.client.Param;

public class ClientPlatformBeanRepository {

    private final ClientModelStore modelStore;
    private final Converters converters;

    private InternalAttributesBean internalAttributesBean;

    public ClientPlatformBeanRepository(final ClientModelStore modelStore, final BeanRepository beanRepository, final EventDispatcher dispatcher, final Converters converters) {
        this.modelStore = modelStore;
        this.converters = converters;

        dispatcher.onceInternalAttributesBeanAddedHandler(new DolphinEventHandler() {
            @Override
            public void onEvent(PresentationModel model) {
                internalAttributesBean = new InternalAttributesBean(beanRepository, model);
            }
        });
    }

    public ClientControllerActionCallBean createControllerActionCallBean(String controllerId, String actionName, Param... params) {
        return new ClientControllerActionCallBean(modelStore, converters, controllerId, actionName, params);
    }

    public InternalAttributesBean getInternalAttributesBean() {
        if (internalAttributesBean == null) {
            throw new IllegalStateException("InternalAttributesBean was not initialized yet");
        }
        return internalAttributesBean;
    }
}
