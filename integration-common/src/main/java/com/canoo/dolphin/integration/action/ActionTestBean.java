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
package com.canoo.dolphin.integration.action;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean
public class ActionTestBean {

    private Property<String> stringValue;

    private Property<Boolean> booleanValue;

    public Property<String> stringValueProperty() {
        return stringValue;
    }

    public Property<Boolean> booleanValueProperty() {
        return booleanValue;
    }

    public String getStringValue() {
        return stringValue.get();
    }

    public Boolean getBooleanValue() {
        return booleanValue.get();
    }

    public void setStringValue(String stringValue) {
        this.stringValue.set(stringValue);
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue.set(booleanValue);
    }

}
