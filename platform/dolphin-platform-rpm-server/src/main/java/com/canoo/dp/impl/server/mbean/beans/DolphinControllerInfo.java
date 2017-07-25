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
package com.canoo.dp.impl.server.mbean.beans;

import java.lang.ref.WeakReference;

/**
 * MBean implementation for the {@link DolphinControllerInfoMBean} MBean interface
 */
public class DolphinControllerInfo implements DolphinControllerInfoMBean {

    private String controllerClass;

    private String id;

    private String dolphinSessionId;

    private WeakReference<ModelProvider> weakModelProvider;

    public DolphinControllerInfo(String dolphinSessionId, Class<?> controllerClass, String id, ModelProvider modelProvider) {
        this.controllerClass = controllerClass.getName();
        this.dolphinSessionId = dolphinSessionId;
        this.id = id;
        this.weakModelProvider = new WeakReference<ModelProvider>(modelProvider);
    }

    public String getControllerClass() {
        return controllerClass;
    }

    @Override
    public String dumpModel() {
        ModelProvider provider = weakModelProvider.get();
        if(provider != null) {
            Object model = provider.getModel();
            if(model != null) {
                return ModelJsonSerializer.toJson(model).toString();
            }
        }
        return null;
    }

    @Override
    public String getDolphinSessionId() {
        return dolphinSessionId;
    }

    public String getId() {
        return id;
    }
}
