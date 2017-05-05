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
package com.canoo.dolphin.integration.server.action;

import com.canoo.dolphin.integration.action.ActionTestBean;
import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;
import com.canoo.dolphin.server.DolphinModel;
import com.canoo.dolphin.server.Param;

import static com.canoo.dolphin.integration.action.ActionTestConstants.ACTION_CONTROLLER_NAME;
import static com.canoo.dolphin.integration.action.ActionTestConstants.PARAM_NAME;
import static com.canoo.dolphin.integration.action.ActionTestConstants.PARAM_NAME_1;
import static com.canoo.dolphin.integration.action.ActionTestConstants.PARAM_NAME_2;
import static com.canoo.dolphin.integration.action.ActionTestConstants.PARAM_NAME_3;
import static com.canoo.dolphin.integration.action.ActionTestConstants.PRIVATE_ACTION;
import static com.canoo.dolphin.integration.action.ActionTestConstants.RESET_MODEL_ACTION;
import static com.canoo.dolphin.integration.action.ActionTestConstants.PUBLIC_ACTION;
import static com.canoo.dolphin.integration.action.ActionTestConstants.WITH_EXCEPTION_ACTION;
import static com.canoo.dolphin.integration.action.ActionTestConstants.WITH_SEVERAL_PARAMS_ACTION;
import static com.canoo.dolphin.integration.action.ActionTestConstants.WITH_STRING_PARAM_ACTION;

@DolphinController(ACTION_CONTROLLER_NAME)
public class ActionTestController {

    @DolphinModel
    private ActionTestBean model;

    @DolphinAction(RESET_MODEL_ACTION)
    public void resetModel() {
        model.setBooleanValue(null);
        model.setStringValue(null);
    }

    @DolphinAction(PUBLIC_ACTION)
    public void publicAction() {
        model.setBooleanValue(true);
    }

    @DolphinAction(PRIVATE_ACTION)
    private void privateAction() {
        model.setBooleanValue(true);
    }

    @DolphinAction(WITH_STRING_PARAM_ACTION)
    private void withStringParam(@Param(PARAM_NAME) String value) {
        model.setBooleanValue(true);
        model.setStringValue(value);
    }

    @DolphinAction(WITH_SEVERAL_PARAMS_ACTION)
    private void withSeveralParams(@Param(PARAM_NAME_1) String value1, @Param(PARAM_NAME_2) String value2, @Param(PARAM_NAME_3) int value3) {
        model.setBooleanValue(true);
        model.setStringValue(value1 + value2 + value3);
    }

    @DolphinAction(WITH_EXCEPTION_ACTION)
    private void withException() {
        throw new RuntimeException("huhu");
    }
}
