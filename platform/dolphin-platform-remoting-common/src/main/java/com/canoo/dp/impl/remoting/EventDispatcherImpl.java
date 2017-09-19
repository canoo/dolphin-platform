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
package com.canoo.dp.impl.remoting;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.remoting.legacy.RemotingConstants;
import com.canoo.dp.impl.remoting.legacy.core.PresentationModel;
import com.canoo.dp.impl.remoting.legacy.core.ModelStore;
import com.canoo.dp.impl.remoting.legacy.core.ModelStoreEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class EventDispatcherImpl implements EventDispatcher {

    private final List<DolphinEventHandler> modelAddedHandlers = new ArrayList<>(1);
    private final List<DolphinEventHandler> modelRemovedHandlers = new ArrayList<>(1);
    private final List<DolphinEventHandler> listSpliceHandlers = new ArrayList<>(1);
    private final List<DolphinEventHandler> controllerActionCallBeanAddedHandlers = new ArrayList<>(1);
    private final List<DolphinEventHandler> controllerActionCallBeanRemovedHandlers = new ArrayList<>(1);
    private final List<DolphinEventHandler> internalAttributesBeanAddedHandlers = new ArrayList<>(1);

    public EventDispatcherImpl(final ModelStore modelStore) {
        Assert.requireNonNull(modelStore, "modelStore").addModelStoreListener(this);
    }

    @Override
    public void addAddedHandler(DolphinEventHandler handler) {
        modelAddedHandlers.add(handler);
    }

    @Override
    public void addRemovedHandler(DolphinEventHandler handler) {
        modelRemovedHandlers.add(handler);
    }

    @Override
    public void addListSpliceHandler(DolphinEventHandler handler) {
        listSpliceHandlers.add(handler);
    }

    @Override
    public void addControllerActionCallBeanAddedHandler(DolphinEventHandler handler) {
        controllerActionCallBeanAddedHandlers.add(handler);
    }

    @Override
    public void addControllerActionCallBeanRemovedHandler(DolphinEventHandler handler) {
        controllerActionCallBeanRemovedHandlers.add(handler);
    }

    @Override
    public void onceInternalAttributesBeanAddedHandler(DolphinEventHandler handler) {
        internalAttributesBeanAddedHandlers.add(handler);
    }

    @Override
    public void modelStoreChanged(ModelStoreEvent event) {
        Assert.requireNonNull(event, "event");
        final PresentationModel model = event.getPresentationModel();
        if (!isLocalChange(model)) {
            if (ModelStoreEvent.Type.ADDED == event.getType()) {
                onAddedHandler(model);
            } else if (ModelStoreEvent.Type.REMOVED == event.getType()) {
                onRemovedHandler(model);
            }
        }
    }

    private void onAddedHandler(PresentationModel model) {
        Assert.requireNonNull(model, "model");
        final String type = model.getPresentationModelType();
        switch (type) {
            case PlatformRemotingConstants.DOLPHIN_BEAN:
                // ignore
                break;
            case PlatformRemotingConstants.CONTROLLER_ACTION_CALL_BEAN_NAME:
                for (final DolphinEventHandler handler : controllerActionCallBeanAddedHandlers) {
                    handler.onEvent(model);
                }
                break;
            case PlatformRemotingConstants.INTERNAL_ATTRIBUTES_BEAN_NAME:
                for (final DolphinEventHandler handler : internalAttributesBeanAddedHandlers) {
                    handler.onEvent(model);
                }
                internalAttributesBeanAddedHandlers.clear();
                break;
            case PlatformRemotingConstants.LIST_SPLICE:
                for (final DolphinEventHandler handler : listSpliceHandlers) {
                    handler.onEvent(model);
                }
                break;
            default:
                for (final DolphinEventHandler handler : modelAddedHandlers) {
                    handler.onEvent(model);
                }
                break;
        }
    }

    private void onRemovedHandler(PresentationModel model) {
        Assert.requireNonNull(model, "model");
        final String type = model.getPresentationModelType();
        switch (type) {
            case PlatformRemotingConstants.DOLPHIN_BEAN:
            case PlatformRemotingConstants.LIST_SPLICE:
            case PlatformRemotingConstants.INTERNAL_ATTRIBUTES_BEAN_NAME:
                // ignore
                break;
            case PlatformRemotingConstants.CONTROLLER_ACTION_CALL_BEAN_NAME:
                for (final DolphinEventHandler handler : controllerActionCallBeanRemovedHandlers) {
                    handler.onEvent(model);
                }
                break;
            default:
                for (final DolphinEventHandler handler : modelRemovedHandlers) {
                    handler.onEvent(model);
                }
                break;
        }
    }

    private boolean isLocalChange(PresentationModel model) {
        Assert.requireNonNull(model, "model");
        final Object value = model.getAttribute(RemotingConstants.SOURCE_SYSTEM).getValue();
        return getLocalSystemIdentifier().equals(value);
    }

    public abstract String getLocalSystemIdentifier();
}
