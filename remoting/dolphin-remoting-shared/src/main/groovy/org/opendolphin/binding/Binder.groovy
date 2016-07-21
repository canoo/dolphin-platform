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
package org.opendolphin.binding

import groovy.transform.Canonical
import org.opendolphin.core.BasePresentationModel
import org.opendolphin.core.PresentationModel
import org.opendolphin.core.Tag

import java.beans.Introspector
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.util.function.Function
import java.util.logging.Level
import java.util.logging.Logger

class Binder {
    static BindOfAble bind(String sourcePropertyName) {
        new BindOfAble(sourcePropertyName, Tag.VALUE)
    }
    static BindOfAble bind(String sourcePropertyName, Tag tag) {
        new BindOfAble(sourcePropertyName, tag)
    }

    static BindPojoOfAble bindInfo(String sourcePropertyName) {
        new BindPojoOfAble(sourcePropertyName)
    }

    static UnbindOfAble unbind(String sourcePropertyName) {
        new UnbindOfAble(sourcePropertyName)
    }

    static UnbindInfoOfAble unbindInfo(String sourcePropertyName) {
        new UnbindInfoOfAble(sourcePropertyName)
    }
}

class UnbindOfAble {
    String sourcePropertyName

    UnbindOfAble(String sourcePropertyName) {
        this.sourcePropertyName = sourcePropertyName
    }

    UnbindFromAble of(PresentationModel source) {
        new UnbindFromAble(source, sourcePropertyName)
    }

    UnbindPojoFromAble of(Object source) {
        new UnbindPojoFromAble(source, sourcePropertyName)
    }
}

class UnbindFromAble {
    final PresentationModel source
    final String sourcePropertyName

    UnbindFromAble(PresentationModel source, String sourcePropertyName) {
        this.source = source
        this.sourcePropertyName = sourcePropertyName
    }

    UnbindTargetOfAble from(String targetPropertyName) {
        new UnbindTargetOfAble(source, sourcePropertyName, targetPropertyName)
    }
}

class UnbindTargetOfAble {
    final PresentationModel source
    final String sourcePropertyName
    final String targetPropertyName

    UnbindTargetOfAble(PresentationModel source, String sourcePropertyName, String targetPropertyName) {
        this.source = source
        this.sourcePropertyName = sourcePropertyName
        this.targetPropertyName = targetPropertyName
    }

    void of(Object target) {
        doOf(target, targetPropertyName)
    }

    protected void doOf(target, String actualTargetPropertyName) {
        def attribute = source.findAttributeByPropertyName(sourcePropertyName)
        if (!attribute) throw new IllegalArgumentException("there is no attribute for property name '$sourcePropertyName' in '${source.dump()}'")
        // find a BinderPropertyChangeListener that matches
        def listener = attribute.getPropertyChangeListeners('value').find {
            it instanceof BinderPropertyChangeListener && it.target == target && it.targetPropertyName == actualTargetPropertyName
        }
        // remove the listener; this operation is null safe
        attribute.removePropertyChangeListener('value', listener)
    }
}

class UnbindInfoOfAble {
    String sourcePropertyName

    UnbindInfoOfAble(String sourcePropertyName) {
        this.sourcePropertyName = sourcePropertyName
    }

    UnbindPojoFromAble of(Object source) {
        new UnbindPojoFromAble(source, sourcePropertyName)
    }
}

class UnbindPojoFromAble {
    final Object source
    final String sourcePropertyName

    UnbindPojoFromAble(Object source, String sourcePropertyName) {
        this.source = source
        this.sourcePropertyName = sourcePropertyName
    }

    UnbindPojoTargetOfAble from(String targetPropertyName) {
        new UnbindPojoTargetOfAble(source, sourcePropertyName, targetPropertyName)
    }
}

class UnbindPojoTargetOfAble {
    final Object source
    final String sourcePropertyName
    final String targetPropertyName

    UnbindPojoTargetOfAble(Object source, String sourcePropertyName, String targetPropertyName) {
        this.source = source
        this.sourcePropertyName = sourcePropertyName
        this.targetPropertyName = targetPropertyName
    }

    void of(PresentationModel target) {
        doOf(target[targetPropertyName], "value")
    }

    void of(Object target) {
        doOf(target, targetPropertyName)
    }

    protected void doOf(target, String actualTargetPropertyName) {
        def pd = Introspector.getBeanInfo(source.getClass()).getPropertyDescriptors().find { it.name == sourcePropertyName }
        if (!pd) throw new IllegalArgumentException("there is no property named '$sourcePropertyName' in '${source.dump()}'")
        // find a BinderPropertyChangeListener that matches
        def listener = source.getPropertyChangeListeners(sourcePropertyName).find {
            it instanceof BinderPropertyChangeListener && it.target == target && it.targetPropertyName == actualTargetPropertyName
        }
        // remove the listener
        if (listener) source.removePropertyChangeListener(sourcePropertyName, listener)
    }
}

class BindOfAble {
    String  sourcePropertyName
    Tag     tag

    BindOfAble(String sourcePropertyName, Tag tag) {
        this.sourcePropertyName = sourcePropertyName
        this.tag = tag
    }

    BindToAble of(PresentationModel source) {
        new BindToAble(source, sourcePropertyName, tag)
    }

    BindPojoToAble of(Object source) {
        new BindPojoToAble(source, sourcePropertyName)
    }
}

class BindToAble {
    final PresentationModel source
    final String sourcePropertyName
    final Tag    tag
    final Converter converter

    BindToAble(PresentationModel source, String sourcePropertyName, Tag tag, Converter converter = null) {
        this.source = source
        this.sourcePropertyName = sourcePropertyName
        this.tag = tag
        this.converter = converter
    }

    BindTargetOfAble to(String targetPropertyName) {
        new BindTargetOfAble(source, sourcePropertyName, tag, targetPropertyName, converter)
    }

    BindToAble using(Closure converter) {
        using(new ConverterAdapter(converter as Function))
    }

    BindToAble using(Converter converter) {
        return new BindToAble(source, sourcePropertyName, tag, converter)
    }
}

class BindTargetOfAble {
    final PresentationModel source
    final String sourcePropertyName
    final Tag    tag
    final String targetPropertyName
    final Converter converter

    private static final Logger log  = Logger.getLogger(BindTargetOfAble.class.getName())

    BindTargetOfAble(PresentationModel source, String sourcePropertyName, Tag tag, String targetPropertyName, Converter converter) {
        this.source = source
        this.sourcePropertyName = sourcePropertyName
        this.tag = tag
        this.targetPropertyName = targetPropertyName
        this.converter = converter
    }

    void of(PresentationModel target) {
        throw new IllegalArgumentException("You attempted to bind a presentation model attribute against a second one." +
                " This is not supported. Please use qualifiers for such a purpose. If you have a compelling use case for" +
                " this feature, please file a JIRA request.")
    }

    void of(Object target) {
        def attribute = ((BasePresentationModel)source).findAttributeByPropertyNameAndTag(sourcePropertyName, tag)
        if (!attribute) throw new IllegalArgumentException("there is no attribute for property name '$sourcePropertyName' and tag $tag in '${source.dump()}'")
        def changeListener = new BinderPropertyChangeListener(target, targetPropertyName, converter)
        target[targetPropertyName] = changeListener.convert(attribute.value) // set initial value
        // adding a listener is null and duplicate safe
        attribute.addPropertyChangeListener('value', changeListener)
    }

    @Deprecated // TODO (DOL-93) remove legacy code
    void of(Object target, Closure converter) {
        of target, new ConverterAdapter(converter as Function)
    }

    @Deprecated // TODO (DOL-93) remove legacy code
    void of(Object target, Converter converter) {
        if (log.isLoggable(Level.WARNING)) {
            log.warning("bind(<property>).of(<source>).to(<property>).of(<target>, <converter>) is deprecated! Please use: bind(<property>).of(<source>).using(<converter>).to(<property>).of(<target>)");
        }
        def attribute = ((BasePresentationModel)source).findAttributeByPropertyNameAndTag(sourcePropertyName, tag)
        if (!attribute) throw new IllegalArgumentException("there is no attribute for property name '$sourcePropertyName' and tag $tag in '${source.dump()}'")
        def changeListener = new BinderPropertyChangeListener(target, targetPropertyName, converter)
        target[targetPropertyName] = changeListener.convert(attribute.value) // set initial value
        // adding a listener is null and duplicate safe
        attribute.addPropertyChangeListener('value', changeListener)
    }
}

class BindPojoOfAble {
    String sourcePropertyName

    BindPojoOfAble(String sourcePropertyName) {
        this.sourcePropertyName = sourcePropertyName
    }

    BindPojoToAble of(Object source) {
        new BindPojoToAble(source, sourcePropertyName)
    }
}

class BindPojoToAble {
    final Object source
    final String sourcePropertyName
    final Converter converter

    BindPojoToAble(Object source, String sourcePropertyName, Converter converter = null) {
        this.source = source
        this.sourcePropertyName = sourcePropertyName
        this.converter = converter
    }

    BindPojoTargetOfAble to(String targetPropertyName) {
        new BindPojoTargetOfAble(source, sourcePropertyName, targetPropertyName, converter)
    }

    BindPojoToAble using(Closure converter) {
        using(new ConverterAdapter(converter as Function))
    }

    BindPojoToAble using(Converter converter) {
        new BindPojoToAble(source, sourcePropertyName, converter)
    }
}

class BindPojoTargetOfAble {
    final Object source
    final String sourcePropertyName
    final String targetPropertyName
    final Converter converter

    private static final Logger log  = Logger.getLogger(BindPojoTargetOfAble.class.getName())

    BindPojoTargetOfAble(Object source, String sourcePropertyName, String targetPropertyName, Converter converter) {
        this.source = source
        this.sourcePropertyName = sourcePropertyName
        this.targetPropertyName = targetPropertyName
        this.converter = converter
    }

    void of(Object target) {
        def changeListener = makeListener(target, targetPropertyName, converter)
        target[targetPropertyName] = changeListener.convert(source."$sourcePropertyName") // set initial value
        addListener(changeListener)
    }
    void of(PresentationModel target) {
        checkTargetAttributeExists(target, targetPropertyName)
        def changeListener = makeListener(target[targetPropertyName], 'value', converter)
        target[targetPropertyName].value = changeListener.convert(source."$sourcePropertyName") // set initial value
        addListener(changeListener)
    }

    @Deprecated // TODO (DOL-93) remove legacy code
    void of(Object target, Closure converter) {
        of target, new ConverterAdapter(converter as Function)
    }
    @Deprecated // TODO (DOL-93) remove legacy code
    void of(Object target, Converter converter) {
        if (log.isLoggable(Level.WARNING)) {
            log.warning("bind(<property>).of(<source>).to(<property>).of(<target>, <converter>) is deprecated! Please use: bind(<property>).of(<source>).using(<converter>).to(<property>).of(<target>)");
        }
        def changeListener = makeListener(target, targetPropertyName, converter)
        target[targetPropertyName] = changeListener.convert(source."$sourcePropertyName") // set initial value
        addListener(changeListener)
    }
    @Deprecated // TODO (DOL-93) remove legacy code
    void of(PresentationModel target, Closure converter) {
        of target, new ConverterAdapter(converter as Function)
    }
    @Deprecated // TODO (DOL-93) remove legacy code
    void of(PresentationModel target, Converter converter) {
        if (log.isLoggable(Level.WARNING)) {
            log.warning("bind(<property>).of(<source>).to(<property>).of(<target>, <converter>) is deprecated! Please use: bind(<property>).of(<source>).using(<converter>).to(<property>).of(<target>)");
        }
        checkTargetAttributeExists(target, targetPropertyName)
        def changeListener = makeListener(target[targetPropertyName], 'value', converter)
        target[targetPropertyName].value = changeListener.convert(source."$sourcePropertyName") // set initial value
        addListener(changeListener)
    }

    protected checkTargetAttributeExists(PresentationModel target, String targetPropName) {
        if (target[targetPropName] == null) {
            throw new IllegalArgumentException("there is no attribute named '$targetPropName' " +
                    "in presentation model with id '${target.id}', known attribute names are: " +
                    "${target.attributes.collect {it.propertyName}}")
        }
    }

    protected BinderPropertyChangeListener makeListener(eventProvider, String eventPropName, Converter converter) {
        def pd = Introspector.getBeanInfo(source.getClass()).getPropertyDescriptors().find { it.name == sourcePropertyName }
        if (!pd) throw new IllegalArgumentException("there is no property named '$sourcePropertyName' in '${source.dump()}'")
        return new BinderPropertyChangeListener(eventProvider, eventPropName, converter);
    }

    protected void addListener(changeListener) {
        if (!(changeListener in source.getPropertyChangeListeners(sourcePropertyName))) { // don't add the listener twice
            source.addPropertyChangeListener(sourcePropertyName, changeListener)
        }
    }

}

@Canonical
class BinderPropertyChangeListener implements PropertyChangeListener {
    Object target
    String targetPropertyName
    Converter converter // todo: needs hand-made ctor?

    void propertyChange(PropertyChangeEvent evt) {
        target[targetPropertyName] = convert(evt.newValue)
    }

    Object convert(Object value) {
        converter != null ? converter.convert(value) : value
    }
}
