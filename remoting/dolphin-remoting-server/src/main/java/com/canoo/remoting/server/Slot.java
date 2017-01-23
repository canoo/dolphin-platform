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
package com.canoo.remoting.server;

/**
 * A Slot corresponds to an attribute in the presentation model.
 * A slot consists of a propertyName (String), a value, a baseValue, a qualifier, and a tag.
 * A DTO (data transfer object) consists of a list of slots; the DTO is the equivalent of a presentation model.
 *
 * @see DTO
 */
public final class Slot {

    private String propertyName;

    private Object value;

    private String qualifier;

    public Slot(String propertyName, Object value) {
        this(propertyName, value, null);
    }

    /**
     * Convenience method with positional parameters to create an attribute specification from name/value pairs.
     * Especially useful when creating DTO objects.
     */
    public Slot(String propertyName, Object value, String qualifier) {
        this.propertyName = propertyName;
        this.value = value;
        this.qualifier = qualifier;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Object getValue() {
        return value;
    }

    public String getQualifier() {
        return qualifier;
    }

}
