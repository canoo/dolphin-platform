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
package com.canoo.communication.common;

import java.util.List;

/**
 * A presentation model is uniquely identified by a string ID, and consists of a list of attributes.
 * The presentation model may also be given a type.
 * @param <A>
 * @see Attribute
 */
public interface PresentationModel<A extends Attribute> extends Observable {

    /**
     *
     * @return the presentation model's unique ID.
     */
    String getId();

    /**
     *
     * @return the presentation model's list of attributes.
     */
    List<A> getAttributes();

    /**
     * Convenience (shorthand) method for finding a value attribute by property name.
     * @see #findAttributeByPropertyName(String)
     * @param propertyName attribute's property name
     * @return value attribute for the given property; null if non-existent
     */
    A getAttribute(String propertyName);

    /**
     * Returns the first attribute whose qualifier matches the supplied value.
     */
    A findAttributeByQualifier(String qualifier);

    /**
     * Returns the attribute whose ID matches the supplied value.
     */
    A findAttributeById(String id);

    /**
     * Returns the type (a String value) of the presentation model.  The type defaults to null.
     * @return
     */
    String getPresentationModelType();

    /**
     * Warning: should only be called from the open-dolphin command layer, not from applications,
     * since it does not register all required listeners. Consider using ClientDolphin.addAttributeToModel().
     * @param attribute
     */
    void _internal_addAttribute(A attribute);

}
