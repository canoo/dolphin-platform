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

public class AttributeCreatedNotification extends Command {

    private String pmId;

    private String attributeId;

    private String propertyName;

    private Object newValue;

    private String qualifier;

    public AttributeCreatedNotification() {
    }

    public AttributeCreatedNotification(String pmId, String attributeId, String propertyName, Object newValue, String qualifier) {
        this.pmId = pmId;
        this.attributeId = attributeId;
        this.propertyName = propertyName;
        this.newValue = newValue;
        this.qualifier = qualifier;
    }

    public String getPmId() {
        return pmId;
    }

    public void setPmId(String pmId) {
        this.pmId = pmId;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public String toString() {
        return super.toString() + " attr:" + attributeId + ", pm:" + pmId + ", property:" + propertyName + " value:" + String.valueOf(newValue) + " qualifier:" + qualifier;
    }

}
