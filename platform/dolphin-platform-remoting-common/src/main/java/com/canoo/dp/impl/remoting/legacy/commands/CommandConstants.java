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

public interface CommandConstants {

    String START_LONG_POLL_COMMAND_ID = "StartLongPoll";
    String INTERRUPT_LONG_POLL_COMMAND_ID = "InterruptLongPoll";
    String CREATE_CONTEXT_COMMAND_ID = "CreateContext";
    String DESTROY_CONTEXT_COMMAND_ID = "DestroyContext";
    String VALUE_CHANGED_COMMAND_ID = "ValueChanged";
    String CREATE_PRESENTATION_MODEL_COMMAND_ID = "CreatePresentationModel";
    String CREATE_CONTROLLER_COMMAND_ID = "CreateController";
    String DESTROY_CONTROLLER_COMMAND_ID = "DestroyController";
    String CALL_ACTION_COMMAND_ID = "CallAction";
    String CHANGE_ATTRIBUTE_METADATA_COMMAND_ID = "ChangeAttributeMetadata";
    String ATTRIBUTE_METADATA_CHANGED_COMMAND_ID = "AttributeMetadataChanged";
    String EMPTY_COMMAND_ID = "Empty";
    String DELETE_PRESENTATION_MODEL_COMMAND_ID = "DeletePresentationModelCommand";

    String ID = "id";
    String ATTRIBUTE_ID = "a_id";
    String PM_ID = "p_id";
    String CONTROLLER_ID = "c_id";
    String PM_TYPE = "t";
    String NAME = "n";
    String VALUE = "v";
    String PARAMS = "p";
    String PM_ATTRIBUTES = "a";
}
