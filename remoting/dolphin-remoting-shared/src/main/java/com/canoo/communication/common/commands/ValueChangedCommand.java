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

public class ValueChangedCommand extends Command {

    private String attributeId;

    private Object oldValue;

    private Object newValue;

    public ValueChangedCommand() {
    }

    public ValueChangedCommand(String attributeId, Object oldValue, Object newValue) {
        this.attributeId = attributeId;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }

    @Override
    public String toString() {
        return super.toString() + " attr:" + attributeId + ", " + String.valueOf(oldValue) + " -> " + String.valueOf(newValue);
    }

}
