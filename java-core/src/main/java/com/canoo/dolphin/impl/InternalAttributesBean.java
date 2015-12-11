package com.canoo.dolphin.impl;

import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.PresentationModelBuilder;
import com.canoo.dolphin.internal.util.Assert;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;

public class InternalAttributesBean {

    private static final String CONTROLLER_NAME = "controllerName";
    private static final String CONTROLLER_ID = "controllerId";
    private static final String MODEL = "model";

    private final BeanRepository beanRepository;
    private final Attribute controllerName;
    private final Attribute controllerId;
    private final Attribute model;

    public InternalAttributesBean(BeanRepository beanRepository, PresentationModel pm) {
        Assert.requireNonNull(beanRepository, "beanRepository");
        Assert.requireNonNull(pm, "pm");
        this.beanRepository = beanRepository;
        controllerName = pm.findAttributeByPropertyName(CONTROLLER_NAME);
        controllerId = pm.findAttributeByPropertyName(CONTROLLER_ID);
        model = pm.findAttributeByPropertyName(MODEL);
    }

    public InternalAttributesBean(BeanRepository beanRepository, PresentationModelBuilder builder) {
        this(
            beanRepository,
                Assert.requireNonNull(builder, "builder").withType(PlatformConstants.INTERNAL_ATTRIBUTES_BEAN_NAME)
                .withAttribute(CONTROLLER_NAME)
                .withAttribute(CONTROLLER_ID)
                .withAttribute(MODEL)
                .create()
        );
    }

    public String getControllerName() {
        return (String) controllerName.getValue();
    }

    public void setControllerName(String controllerName) {
        this.controllerName.setValue(controllerName);
    }

    public String getControllerId() {
        return (String) controllerId.getValue();
    }

    public void setControllerId(String controllerId) {
        this.controllerId.setValue(controllerId);
    }

    public <T> T getModel() {
        if(model.getValue() == null) {
            throw new RuntimeException("Dolphin Platform internal error: No model defined");
        }
        return (T) beanRepository.getBean(model.getValue().toString());
    }

    public void setModel(Object model) {
        this.model.setValue(beanRepository.getDolphinId(model));
    }
}
