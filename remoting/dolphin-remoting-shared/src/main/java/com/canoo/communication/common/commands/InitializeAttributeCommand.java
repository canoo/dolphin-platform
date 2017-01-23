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
package com.canoo.communication.common.commands;

public class InitializeAttributeCommand extends Command {

    private String pmId;

    private String propertyName;

    private String qualifier;

    private Object newValue;

    private String pmType;

    public InitializeAttributeCommand() {
    }

    public InitializeAttributeCommand(String pmId, String propertyName, String qualifier, Object newValue) {
        this.pmId = pmId;
        this.propertyName = propertyName;
        this.qualifier = qualifier;
        this.newValue = newValue;
    }

    public InitializeAttributeCommand(String pmId, String propertyName, String qualifier, Object newValue, String pmType) {
        this.pmId = pmId;
        this.propertyName = propertyName;
        this.qualifier = qualifier;
        this.newValue = newValue;
        this.pmType = pmType;
    }

    public String getPmId() {
        return pmId;
    }

    public void setPmId(String pmId) {
        this.pmId = pmId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }

    public String getPmType() {
        return pmType;
    }

    public void setPmType(String pmType) {
        this.pmType = pmType;
    }

    @Override
    public String toString() {
        return super.toString() + " pm \'" + pmId + "\' pmType\'" + pmType + "\' property \'" + propertyName + "\' initial value \'" + String.valueOf(newValue) + "\' qualifier " + qualifier;
    }
}
