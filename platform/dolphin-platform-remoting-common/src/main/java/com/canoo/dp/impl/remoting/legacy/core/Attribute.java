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
package com.canoo.dp.impl.remoting.legacy.core;

/**
 * The value may be null as long as the Attribute is used as a "placeholder".
 */

public abstract class Attribute extends Observable {

    public static final String QUALIFIER_NAME = "qualifier";
    public static final String VALUE_NAME = "value";
    public static final String PROPERTY_NAME = "propertyName";
    public static final String ID = "id";
    private static long instanceCount = 0;

    private final String propertyName;

    private Object value;

    private PresentationModel presentationModel;

    private String id ;

    private String qualifier; // application specific semantics apply

    public Attribute(String propertyName, Object value) {
        this(propertyName, value, null);
    }

    public Attribute(String propertyName, Object value, String qualifier) {
        this.id = (instanceCount++) + getOrigin();
        this.propertyName = propertyName;
        this.value = value;
        this.qualifier = qualifier;
    }

    /**
     * @return 'C' for client or 'S' for server
     */
    protected abstract String getOrigin();

    public void setPresentationModel(PresentationModel presentationModel) {
        if (this.presentationModel != null) {
            throw new IllegalStateException("You can not set a presentation model for an attribute that is already bound.");
        }
        this.presentationModel = presentationModel;
    }

    public PresentationModel getPresentationModel() {
        return this.presentationModel;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object newValue) {
        if (isDifferent(value, newValue)) {
            firePropertyChange(VALUE_NAME, value, value = newValue);
        }
    }

    private boolean isDifferent(Object oldValue, Object newValue) {
        return oldValue == null ? newValue != null : !oldValue.equals(newValue);
    }

    public String toString() {
        return new StringBuilder()
                .append(id)
                .append(" : ")
                .append(propertyName)
                .append(" (")
                .append(qualifier).append(") ")
                .append(value).toString();
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        firePropertyChange(QUALIFIER_NAME, this.qualifier, this.qualifier = qualifier);
    }
}
