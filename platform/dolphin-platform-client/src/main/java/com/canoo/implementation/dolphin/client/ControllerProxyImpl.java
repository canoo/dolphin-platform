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

import com.canoo.dolphin.client.ControllerProxy;
import com.canoo.dolphin.client.Param;
import com.canoo.implementation.dolphin.InternalAttributesBean;
import com.canoo.implementation.dolphin.PlatformConstants;
import com.canoo.implementation.dolphin.util.Assert;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.comm.OnFinishedHandler;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ControllerProxyImpl<T> implements ControllerProxy<T> {

    private final String controllerId;

    private final ClientDolphin dolphin;

    private final ClientPlatformBeanRepository platformBeanRepository;

    private T model;

    private volatile boolean destroyed = false;

    public ControllerProxyImpl(String controllerId, T model, ClientDolphin dolphin, ClientPlatformBeanRepository platformBeanRepository) {
        this.dolphin = Assert.requireNonNull(dolphin, "dolphin");
        this.controllerId = Assert.requireNonBlank(controllerId, "controllerId");
        this.model = model;
        this.platformBeanRepository = Assert.requireNonNull(platformBeanRepository, "platformBeanRepository");
    }

    public String getControllerId() {
        return controllerId;
    }

    @Override
    public T getModel() {
        return model;
    }

    @Override
    public CompletableFuture<Void> invoke(String actionName, Param... params) {
        if (destroyed) {
            throw new IllegalStateException("The controller was already destroyed");
        }

        final ClientControllerActionCallBean bean = platformBeanRepository.createControllerActionCallBean(controllerId, actionName, params);


        final CompletableFuture<Void> result = new CompletableFuture<>();
        dolphin.send(PlatformConstants.CALL_CONTROLLER_ACTION_COMMAND_NAME, new OnFinishedHandler(){
            @Override
            public void onFinished(List<ClientPresentationModel> presentationModels) {
                if (bean.isError()) {
                    result.completeExceptionally(new ControllerActionException());
                } else {
                    result.complete(null);
                }
                bean.unregister();
            }
        });
        return result;
    }

    @Override
    public CompletableFuture<Void> destroy() {
        if (destroyed) {
            throw new IllegalStateException("The controller was already destroyed");
        }
        destroyed = true;

        final InternalAttributesBean bean = platformBeanRepository.getInternalAttributesBean();
        bean.setControllerId(controllerId);

        final CompletableFuture<Void> ret = new CompletableFuture<>();

        dolphin.send(PlatformConstants.DESTROY_CONTROLLER_COMMAND_NAME, new OnFinishedHandler() {
            @Override
            public void onFinished(List<ClientPresentationModel> presentationModels) {
                model = null;
                ret.complete(null);
            }
        });
        return ret;
    }
}
