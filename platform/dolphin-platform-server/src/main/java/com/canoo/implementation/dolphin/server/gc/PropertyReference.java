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
package com.canoo.implementation.dolphin.server.gc;

import com.canoo.dolphin.mapping.Property;

/**
 * A {@link Reference} that is defined by a {@link Property}.
 * Example for such a reference: Dolphin bean A contains a {@link Property} that value is dolphin bean B
 * For more information see {@link Reference} and {@link com.canoo.dolphin.mapping.DolphinBean}
 */
public class PropertyReference extends Reference {

    private Property property;

    /**
     * Constructor
     * @param parent the dolphin bean that contains the property
     * @param property the property
     * @param child the dolphin bean that is the internal value of the {@link Property}
     */
    public PropertyReference(Instance parent, Property property, Instance child) {
        super(parent, child);
        this.property = property;
    }

    /**
     * Returns the property
     * @return the property
     */
    public Property getProperty() {
        return property;
    }
}
