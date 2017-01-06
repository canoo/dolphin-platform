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
package com.canoo.dolphin.validation;

import com.canoo.implementation.dolphin.MockedProperty;
import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@DolphinBean
public class TestBean {

    @NotNull
    private Property<String> value1 = new MockedProperty<>();

    @Null
    private Property<String> value2 = new MockedProperty<>();

    @AssertTrue
    private Property<Boolean> value3 = new MockedProperty<>();

    @AssertFalse
    private Property<Boolean> value4 = new MockedProperty<>();

    public Property<String> value1Property() {
        return value1;
    }

    public Property<String> value2Property() {
        return value2;
    }

    public Property<Boolean> value3Property() {
        return value3;
    }

    public Property<Boolean> value4Property() {
        return value4;
    }
}
