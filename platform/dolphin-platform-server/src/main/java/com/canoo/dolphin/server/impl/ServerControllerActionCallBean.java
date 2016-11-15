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
package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.converter.ValueConverterException;
import com.canoo.dolphin.impl.AbstractControllerActionCallBean;
import com.canoo.dolphin.impl.Converters;
import com.canoo.dolphin.mapping.MappingException;
import com.canoo.dolphin.util.Assert;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.Tag;

public class ServerControllerActionCallBean extends AbstractControllerActionCallBean {

    private final Converters converters;
    private final PresentationModel pm;

    public ServerControllerActionCallBean(Converters converters, PresentationModel pm) {
        this.converters = Assert.requireNonNull(converters, "converters");
        this.pm = Assert.requireNonNull(pm, "pm");
    }

    public String getControllerId() {
        return (String) pm.findAttributeByPropertyName(CONTROLLER_ID).getValue();
    }

    public String getActionName() {
        return (String) pm.findAttributeByPropertyName(ACTION_NAME).getValue();
    }

    public void setError(boolean error) {
        pm.findAttributeByPropertyName(ERROR_CODE).setValue(error);
    }

    public Object getParam(String name, Class<?> type) {
        final String internalName = PARAM_PREFIX + name;
        final Attribute valueAttribute = pm.findAttributeByPropertyNameAndTag(internalName, Tag.VALUE);
        if (valueAttribute == null) {
            throw new IllegalArgumentException(String.format("Invoking DolphinAction requires parameter '%s', but it was not send", name));
        }
        try {
            return converters.getConverter(type).convertFromDolphin(valueAttribute.getValue());
        } catch (ValueConverterException e) {
            throw new MappingException("Error in conversion", e);
        }
    }
}
