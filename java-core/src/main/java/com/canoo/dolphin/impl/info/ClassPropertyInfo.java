/**
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
package com.canoo.dolphin.impl.info;

import com.canoo.dolphin.impl.Converters;
import com.canoo.dolphin.impl.ReflectionHelper;
import com.canoo.dolphin.internal.info.PropertyInfo;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.opendolphin.core.Attribute;

import java.lang.reflect.Field;

public class ClassPropertyInfo extends PropertyInfo {

    private final Field field;
    private final Class nestingType;

    public ClassPropertyInfo(Attribute attribute, String attributeName, Converters.Converter converter, Field field) {
        this(attribute, attributeName, converter, field, null);
    }

    public ClassPropertyInfo(Attribute attribute, String attributeName, Converters.Converter converter, Field field, Class nestingType) {
        super(attribute, attributeName, converter);
        this.field = field;
        this.nestingType = nestingType;
    }

    @Override
    public Object getPrivileged(Object bean) {
        return ReflectionHelper.getPrivileged(field, bean);
    }

    @Override
    public void setPriviliged(Object bean, Object value) {
        ReflectionHelper.setPrivileged(field, bean, value);
    }

    @Override
    public Object convertFromDolphin(Object value) {
        value = super.convertFromDolphin(value);
        if (nestingType != null) {
            value = DefaultTypeTransformation.castToType(value, nestingType);
        }
        return value;
    }

    @Override
    public Object convertToDolphin(Object value) {
        if (nestingType != null) {
            value = DefaultTypeTransformation.castToType(value, nestingType);
        }
        return super.convertToDolphin(value);
    }
}
