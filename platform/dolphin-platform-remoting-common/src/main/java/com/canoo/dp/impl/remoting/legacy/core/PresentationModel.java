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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A PresentationModel is a collection of {@link Attribute}s.
 * PresentationModels are not meant to be extended for the normal use, i.e. you typically don't need something like
 * a specialized "PersonPresentationModel" or so.
 */

public class PresentationModel<A extends Attribute> extends Observable {

    protected final List<A> attributes = new LinkedList<A>();

    private final String id;

    private       String presentationModelType;


    /**
     * @throws AssertionError if the list of attributes is null or empty
     */
    public PresentationModel(String id, List<A> attributes) {
        this.id = id;
        for (A attr : attributes) {
            _internal_addAttribute(attr);
        }
    }

    public void _internal_addAttribute(A attribute) {
        if (null == attribute || attributes.contains(attribute)) return;
        if (null != getAttribute(attribute.getPropertyName())) {
            throw new IllegalStateException("There already is an attribute with property name '"
                                            + attribute.getPropertyName()
                                            + "' in presentation model with id '" + this.id + "'.");
        }
        if (attribute.getQualifier() != null && this.findAttributeByQualifier(attribute.getQualifier()) != null) {
            throw  new IllegalStateException("There already is an attribute with qualifier '" + attribute.getQualifier()
                    + "' in presentation model with id '" + this.id + "'.");
        }
        ((Attribute)attribute).setPresentationModel(this);
        attributes.add(attribute);
    }

    public String getId() {
        return id;
    }

    public String getPresentationModelType() {
        return presentationModelType;
    }

    public void setPresentationModelType(String presentationModelType) {
        this.presentationModelType = presentationModelType;
    }

    /**
     * @return the immutable internal representation
     */
    public List<A> getAttributes() {
        return Collections.unmodifiableList(attributes);
    }

    public A getAttribute(String propertyName) {
        if (null == propertyName) return null;
        for (A attribute : attributes) {
            if (propertyName.equals(attribute.getPropertyName())) {
                return attribute;
            }
        }
        return null;
    }

    public A findAttributeByQualifier(String qualifier) {
        if (null == qualifier) return null;
        for (A attribute : attributes) {
            if (qualifier.equals(attribute.getQualifier())) {
                return attribute;
            }
        }
        return null;
    }

    public A findAttributeById(String id) {
        for (A attribute : attributes) {
            if (attribute.getId().equals(id)) {
                return attribute;
            }
        }
        return null;
    }
}