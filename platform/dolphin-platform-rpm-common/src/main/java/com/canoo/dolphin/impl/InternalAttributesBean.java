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
package com.canoo.dolphin.impl;

import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.PresentationModelBuilder;
import com.canoo.dp.impl.platform.core.Assert;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;

public class InternalAttributesBean {

    private static final String CONTROLLER_NAME = "controllerName";
    private static final String CONTROLLER_ID = "controllerId";
    private static final String MODEL = "model";

    private final BeanRepository beanRepository;
    private final Attribute controllerNameAttribute;
    private final Attribute controllerIdAttribute;
    private final Attribute modelAttribute;

    public InternalAttributesBean(final BeanRepository beanRepository, final PresentationModel pm) {
        this.beanRepository = Assert.requireNonNull(beanRepository, "beanRepository");
        Assert.requireNonNull(pm, "pm");
        controllerNameAttribute = pm.getAttribute(CONTROLLER_NAME);
        controllerIdAttribute = pm.getAttribute(CONTROLLER_ID);
        modelAttribute = pm.getAttribute(MODEL);
    }

    public InternalAttributesBean(final BeanRepository beanRepository, final PresentationModelBuilder builder) {
        this(
            beanRepository,
                Assert.requireNonNull(builder, "builder").withType(PlatformRemotingConstants.INTERNAL_ATTRIBUTES_BEAN_NAME)
                .withAttribute(CONTROLLER_NAME)
                .withAttribute(CONTROLLER_ID)
                .withAttribute(MODEL)
                .create()
        );
    }

    public String getControllerName() {
        return (String) controllerNameAttribute.getValue();
    }

    public void setControllerName(String controllerName) {
        this.controllerNameAttribute.setValue(controllerName);
    }

    public String getControllerId() {
        return (String) controllerIdAttribute.getValue();
    }

    public void setControllerId(String controllerId) {
        this.controllerIdAttribute.setValue(controllerId);
    }

    public <T> T getModel() {
        if(modelAttribute.getValue() == null) {
            throw new RuntimeException("Dolphin Platform internal error: No model defined");
        }
        return (T) beanRepository.getBean(modelAttribute.getValue().toString());
    }

    public void setModel(Object model) {
        this.modelAttribute.setValue(beanRepository.getDolphinId(model));
    }
}
