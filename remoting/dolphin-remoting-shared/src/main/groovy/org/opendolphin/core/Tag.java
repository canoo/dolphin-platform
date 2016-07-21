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
package org.opendolphin.core;

import lombok.Getter;
import org.opendolphin.util.MapWithDefault;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tags are used to differentiate various data and metadata associated with an Attribute.
 * Every attribute has a tag; by default, the VALUE tag is used.
 * <p>
 * By creating multiple attributes with the same property name but different tags, any amount of metadata can be kept
 * for the property.  This metadata can consist of any additional information that the view layer might find useful,
 * for example, the localized label to be displayed next to the property, the tooltip for the property, validation
 * criteria for the property, whether the UI control for the property should be visible or enabled, and so on.
 * <p>
 * Note that Dolphin itself does not take any special action based on the tag type, except to assume a tag type of VALUE
 * if none is otherwise specified.  The view code must supply the logic to enforce tag meanings.
 * Therefore, additional tags may be created as needed with no change to Dolphin itself.
 * The UI toolkit can bind against these other tag attributes just as it can against the "value" attribute.
 * <p>
 * Tags are essentially statically typed Strings, and you can make your own by using the "tagFor" factory or
 * by subclassing.
 * <p>
 * Several tags are predeclared; see documentation for suggested usage.
 *
 * @see Attribute
 */
public final class Tag {
    @Getter
    final String name;

    protected Tag(String name) {
        this.name = name;
    }

    public final String toString() {
        return name;
    }

    /** Factory method with flyweight pattern */
    public static final Map<String, Tag> tagFor = MapWithDefault.newInstance(new LinkedHashMap<>(), Tag::new);

    /** The actual value of the attribute. This is the default if no tag is given. */
    public static final Tag VALUE = tagFor.get("VALUE");

    /** the to-be-displayed String, not the key. Internationalization happens on the server. */
    public static final Tag LABEL = tagFor.get("LABEL");

    /** a single text; e.g. "textArea" if the String value should be displayed in a text area instead of a textField */
    public static final Tag WIDGET_HINT = tagFor.get("WIDGET_HINT");

    /** a single text; e.g. "java.util.Date" if the value String represents a date */
    public static final Tag VALUE_TYPE = tagFor.get("VALUE_TYPE");

    /** regular expression for local, syntactical constraints like in "rejectField" */
    public static final Tag REGEX = tagFor.get("REGEX");

    /** Url.toExternalForm() */
    public static final Tag HELP_URL = tagFor.get("HELP_URL");

    /** Tooltip text for the attribute. */
    public static final Tag TOOLTIP = tagFor.get("TOOLTIP");

    /** "true" or "false"; maps to Grails constraint nullable:false */
    public static final Tag MANDATORY = tagFor.get("MANDATORY");

    /** "true" or "false"; maps to Grails constraint display:true */
    public static final Tag VISIBLE = tagFor.get("VISIBLE");

    /** "true" or "false" */
    public static final Tag ENABLED = tagFor.get("ENABLED");
}
