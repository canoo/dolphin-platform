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

import com.canoo.platform.remoting.client.Param;
import com.canoo.platform.remoting.spi.converter.ValueConverterException;
import com.canoo.dp.impl.remoting.AbstractControllerActionCallBean;
import com.canoo.dp.impl.remoting.Converters;
import com.canoo.dp.impl.remoting.PlatformRemotingConstants;
import com.canoo.dp.impl.remoting.MappingException;
import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.client.legacy.ClientModelStore;
import com.canoo.dp.impl.client.legacy.ClientPresentationModel;

public class ClientControllerActionCallBean extends AbstractControllerActionCallBean {

    private final ClientModelStore modelStore;
    private ClientPresentationModel pm;

    public ClientControllerActionCallBean(ClientModelStore modelStore, Converters converters, String controllerId, String actionName, Param... params) {
        this.modelStore = Assert.requireNonNull(modelStore, "modelStore");

        final ClientPresentationModelBuilder builder = new ClientPresentationModelBuilder(modelStore);
        builder.withType(PlatformRemotingConstants.CONTROLLER_ACTION_CALL_BEAN_NAME)
                .withAttribute(CONTROLLER_ID, controllerId)
                .withAttribute(ACTION_NAME, actionName)
                .withAttribute(ERROR_CODE, null);

        for (final Param param : params) {
            final Object value = param.getValue();
            final Object dolphinValue;
            try {
                dolphinValue = value != null? converters.getConverter(value.getClass()).convertToDolphin(value) : null;
            } catch (ValueConverterException e) {
               throw new MappingException("Error in value conversion", e);
            }
            final String paramName = PARAM_PREFIX + param.getName();
            builder.withAttribute(paramName, dolphinValue);
        }

        this.pm = builder.create();
    }

    public boolean isError() {
        if (pm == null) {
            throw new IllegalStateException("ClientControllerActionCallBean was already unregistered");
        }
        return Boolean.TRUE.equals(pm.getAttribute(ERROR_CODE).getValue());
    }

    @SuppressWarnings("unchecked")
    public void unregister() {
        if (pm != null) {
            modelStore.remove(pm);
            pm = null;
        }
    }

}
