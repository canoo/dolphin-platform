/*
 * Copyright 2015 Canoo Engineering AG.
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
package com.canoo.dolphin.internal.info;

import com.canoo.dolphin.impl.ClassRepositoryImpl;
import com.canoo.dolphin.impl.Converters;
import com.canoo.dolphin.impl.DolphinUtils;
import com.canoo.dolphin.impl.ReflectionHelper;
import com.canoo.dolphin.internal.util.Assert;
import org.opendolphin.core.Attribute;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static com.canoo.dolphin.impl.ClassRepositoryImpl.FieldType.BASIC_TYPE;
import static com.canoo.dolphin.impl.ClassRepositoryImpl.FieldType.DOLPHIN_BEAN;
import static com.canoo.dolphin.impl.ClassRepositoryImpl.FieldType.UNKNOWN;

public abstract class PropertyInfo implements PropertyChangeListener {

    private final Attribute attribute;
    private final String attributeName;

    private Converters.Converter converter;

    public PropertyInfo(Attribute attribute, String attributeName, Converters.Converter converter) {
        Assert.requireNonNull(attribute, "attribute");
        Assert.requireNonNull(converter, "converter");

        this.attribute = attribute;
        this.attributeName = attributeName;
        this.converter = converter;

        this.attribute.addPropertyChangeListener(this);
    }

    public String getAttributeName() {
        return attributeName;
    }

    public abstract Object getPrivileged(Object bean);

    public abstract void setPriviliged(Object bean, Object value);

    public Object convertFromDolphin(Object value) {
        return converter.convertFromDolphin(value);
    }

    public Object convertToDolphin(Object value) {
        if (converter.getFieldType() == UNKNOWN && value != null) {
            final ClassRepositoryImpl.FieldType fieldType = ReflectionHelper.isBasicType(value.getClass()) ? BASIC_TYPE : DOLPHIN_BEAN;
            updateFieldType(fieldType);
        }
        return converter.convertToDolphin(value);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        final ClassRepositoryImpl.FieldType fieldType = DolphinUtils.mapFieldTypeFromDolphin(event.getNewValue());
        updateFieldType(fieldType);
    }

    protected void updateFieldType(ClassRepositoryImpl.FieldType fieldType) {
        if (fieldType != UNKNOWN) {
            converter = converter.updateConverter(fieldType);
            attribute.setValue(DolphinUtils.mapFieldTypeToDolphin(fieldType));
            attribute.removePropertyChangeListener(Attribute.VALUE, this);
        }
    }
}
