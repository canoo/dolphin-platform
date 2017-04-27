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
package org.opendolphin.core;

/**
 * A presentation model contains a list of Attribute objects.
 * Every Attribute object belongs to only one presentation model.
 * <p/>
 * Each Attribute is uniquely identified by a property name and a tag value (which if not specified defaults to "VALUE".)
 * <p/>
 * Each Attribute stores a value and a base (or original) value.  An Attribute also maintains a dirty flag, which is
 * true whenever the current value and the base (original) value are different.
 * <p/>
 * In addition, a qualifier string may be specified for an attribute; a qualifier has application-specific
 * semantics, but is generally used to identify all attributes (regardless of the presentation model to which
 * they belong) which refer to the same domain model object.
 */
public interface Attribute extends Observable {

    /** Returns the current value of this attribute. */
    Object getValue();

    /** Sets the current value of this attribute. */
    void setValue(Object value);

    /** Returns the property name of this attribute.  Several attributes in a presentation model may share the
     * same property name, but these attributes must all have different tags.
     */
    String getPropertyName();

    /** Returns the qualifier (if any) of this attribute. */
    String getQualifier();

    /** Returns a string which uniquely identifies every attribute.  Acts as an atom which can be passed between
     * client and server dolphin layers to reference a common attribute.  This string is created by the Attribute
     * constructor and cannot be changed.
     */
    String getId();

    /**
     * Every attribute belongs to at most one presentation model.  If it has been added to a presentation model,
     * then this method returns that model, otherwise null.
     * @return the presentation model to which this attribute belongs.
     */
    PresentationModel getPresentationModel();
}