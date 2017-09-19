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
package com.canoo.dp.impl.remoting.legacy.commands;


import com.canoo.dp.impl.remoting.legacy.core.Attribute;
import com.canoo.dp.impl.remoting.legacy.core.PresentationModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CreatePresentationModelCommand extends Command {

    private String pmId;

    private String pmType;

    private boolean clientSideOnly = false;

    private List<Map<String, Object>> attributes = new ArrayList<Map<String, Object>>();

    public CreatePresentationModelCommand(final String pmId, String pmType, final List<Map<String, Object>> attributes, final boolean clientSideOnly) {
        this();
        this.pmId = pmId;
        this.pmType = pmType;
        this.clientSideOnly = clientSideOnly;
        this.attributes = attributes;
    }

    public CreatePresentationModelCommand(final String pmId, final String pmType, final List<Map<String, Object>> attributes) {
        this();
        this.pmId = pmId;
        this.pmType = pmType;
        this.attributes = attributes;
    }

    public CreatePresentationModelCommand() {
        super(CommandConstants.CREATE_PRESENTATION_MODEL_COMMAND_ID);
    }

    /**
     * @deprecated use ServerFacade convenience methods (it is ok to use it from the client atm)
     */
    public static <T extends Attribute> CreatePresentationModelCommand makeFrom(final PresentationModel<T> model) {
        CreatePresentationModelCommand result = new CreatePresentationModelCommand();
        result.setPmId(model.getId());
        result.setPmType(model.getPresentationModelType());
        for (T attr : model.getAttributes()) {
            Map attributeMap = new HashMap();
            attributeMap.put("propertyName", attr.getPropertyName());
            attributeMap.put("id", attr.getId());
            attributeMap.put("qualifier", attr.getQualifier());
            attributeMap.put("value", attr.getValue());
            result.getAttributes().add(attributeMap);
        }

        return result;
    }

    @Override
    public String toString() {
        return super.toString() + " pmId " + pmId + " pmType " + pmType + (clientSideOnly ? "CLIENT-SIDE-ONLY!" : "") + " attributes " + attributes;
    }

    public String getPmId() {
        return pmId;
    }

    public void setPmId(final String pmId) {
        this.pmId = pmId;
    }

    public String getPmType() {
        return pmType;
    }

    public void setPmType(final String pmType) {
        this.pmType = pmType;
    }

    @Deprecated
    public boolean getClientSideOnly() {
        return clientSideOnly;
    }

    @Deprecated
    public boolean isClientSideOnly() {
        return clientSideOnly;
    }

    @Deprecated
    public void setClientSideOnly(final boolean clientSideOnly) {
        this.clientSideOnly = clientSideOnly;
    }

    public List<Map<String, Object>> getAttributes() {
        return attributes;
    }

    public void setAttributes(final List<Map<String, Object>> attributes) {
        this.attributes = attributes;
    }
}
