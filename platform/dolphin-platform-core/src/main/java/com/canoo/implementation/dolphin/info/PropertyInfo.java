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
package com.canoo.implementation.dolphin.info;

import com.canoo.dolphin.converter.Converter;
import com.canoo.dolphin.converter.ValueConverterException;

public abstract class PropertyInfo {

    private final String attributeName;
    private final Converter converter;

    public PropertyInfo(String attributeName, Converter converter) {
        this.attributeName = attributeName;
        this.converter = converter;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public abstract Object getPrivileged(Object bean);

    public abstract void setPriviliged(Object bean, Object value);

    public Object convertFromDolphin(Object value) throws ValueConverterException {
        return converter.convertFromDolphin(value);
    }

    public Object convertToDolphin(Object value) throws ValueConverterException {
        return converter.convertToDolphin(value);
    }
}
