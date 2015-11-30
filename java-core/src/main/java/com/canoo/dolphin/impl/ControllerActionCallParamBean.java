/*
 * Copyright 2015 Canoo Engineering AG.
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
package com.canoo.dolphin.impl;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

/**
 * Internal PM that is used to define params for an action call
 */
@DolphinBean(PlatformConstants.CONTROLLER_ACTION_CALL_PARAM_BEAN_NAME)
public class ControllerActionCallParamBean {

    /**
     * Name of the param
     */
    private Property<String> paramName;

    /**
     * value of the param (if the value is a PM this contains the unique PM id)
     */
    private Property value;

    /**
     * Type of the param (see the ordinal value of {@link ClassRepositoryImpl.FieldType})
     */
    private Property valueType;

    /**
     * Unique id of the action. See {@link ControllerActionCallBean#id}
     */
    private Property<String> actionId;

    public Object getValue() {
        return value.get();
    }

    public void setValue(Object value) {
        this.value.set(value);
    }

    public Object getValueType() {
        return valueType.get();
    }

    public void setValueType(Object valueType) {
        this.valueType.set(valueType);
    }

    public String getActionId() {
        return actionId.get();
    }

    public void setActionId(String actionId) {
        this.actionId.set(actionId);
    }

    public String getParamName() {
        return paramName.get();
    }

    public void setParamName(String paramName) {
        this.paramName.set(paramName);
    }
}
