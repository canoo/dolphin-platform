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
package com.canoo.implementation.dolphin.client;

import com.canoo.dolphin.client.Param;
import com.canoo.implementation.dolphin.Converters;
import com.canoo.implementation.dolphin.InternalAttributesBean;
import com.canoo.implementation.dolphin.BeanRepository;
import com.canoo.implementation.dolphin.DolphinEventHandler;
import com.canoo.implementation.dolphin.EventDispatcher;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.client.ClientDolphin;

public class ClientPlatformBeanRepository {

    private final ClientDolphin dolphin;
    private final Converters converters;

    private InternalAttributesBean internalAttributesBean;

    public ClientPlatformBeanRepository(ClientDolphin dolphin, BeanRepository beanRepository, EventDispatcher dispatcher, Converters converters) {
        this.dolphin = dolphin;
        this.converters = converters;

        dispatcher.onceInternalAttributesBeanAddedHandler(new DolphinEventHandler() {
            @Override
            public void onEvent(PresentationModel model) {
                internalAttributesBean = new InternalAttributesBean(beanRepository, model);
            }
        });
    }

    public ClientControllerActionCallBean createControllerActionCallBean(String controllerId, String actionName, Param... params) {
        return new ClientControllerActionCallBean(dolphin, converters, controllerId, actionName, params);
    }

    public InternalAttributesBean getInternalAttributesBean() {
        if (internalAttributesBean == null) {
            throw new IllegalStateException("InternalAttributesBean was not initialized yet");
        }
        return internalAttributesBean;
    }
}
