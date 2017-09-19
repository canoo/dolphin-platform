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
package com.canoo.dp.impl.server.model;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.remoting.AbstractControllerActionCallBean;
import com.canoo.dp.impl.remoting.Converters;
import com.canoo.dp.impl.remoting.MappingException;
import com.canoo.dp.impl.remoting.legacy.core.Attribute;
import com.canoo.dp.impl.remoting.legacy.core.PresentationModel;
import com.canoo.platform.remoting.spi.converter.ValueConverterException;

public class ServerControllerActionCallBean extends AbstractControllerActionCallBean {

    private final Converters converters;
    private final PresentationModel pm;

    public ServerControllerActionCallBean(Converters converters, PresentationModel pm) {
        this.converters = Assert.requireNonNull(converters, "converters");
        this.pm = Assert.requireNonNull(pm, "pm");
    }

    public String getControllerId() {
        return (String) pm.getAttribute(CONTROLLER_ID).getValue();
    }

    public String getActionName() {
        return (String) pm.getAttribute(ACTION_NAME).getValue();
    }

    public void setError(boolean error) {
        pm.getAttribute(ERROR_CODE).setValue(error);
    }

    public Object getParam(String name, Class<?> type) {
        final String internalName = PARAM_PREFIX + name;
        final Attribute valueAttribute = pm.getAttribute(internalName);
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
