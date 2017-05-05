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
package com.canoo.dolphin.integration.server.enterprise;

import com.canoo.dolphin.integration.enterprise.EnterpriseTestBean;
import com.canoo.dolphin.server.DolphinController;
import com.canoo.dolphin.server.DolphinModel;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static com.canoo.dolphin.integration.enterprise.EnterpriseTestConstants.ENTERPRISE_CONTROLLER_NAME;

@DolphinController(ENTERPRISE_CONTROLLER_NAME)
public class EnterpriseTestController {

    @DolphinModel
    private EnterpriseTestBean model;

    @PostConstruct
    private void init() {
        model.setPostConstructCalled(true);
    }

    @PreDestroy
    private void destroy() {
        model.setPreDestroyCalled(true);
    }
}
