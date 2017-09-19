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

//Deprecated because the only type that can change is the qualifier. We should name it to QualifierChangedCommand or something
@Deprecated
public final class ChangeAttributeMetadataCommand extends Command {

    private String attributeId;
    private String metadataName;
    private Object value;

    public ChangeAttributeMetadataCommand() {
        super(CommandConstants.CHANGE_ATTRIBUTE_METADATA_COMMAND_ID);
    }

    public ChangeAttributeMetadataCommand(final String attributeId, final String metadataName, final Object value) {
        this();
        this.attributeId = attributeId;
        this.metadataName = metadataName;
        this.value = value;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(final String attributeId) {
        this.attributeId = attributeId;
    }

    public String getMetadataName() {
        return metadataName;
    }

    public void setMetadataName(final String metadataName) {
        this.metadataName = metadataName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(final Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return super.toString() + " attr:" + attributeId + ", metadataName:" + metadataName + " value:" + String.valueOf(value);
    }
}
