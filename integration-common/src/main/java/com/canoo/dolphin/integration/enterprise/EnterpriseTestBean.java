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
package com.canoo.dolphin.integration.enterprise;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean
public class EnterpriseTestBean {

    private Property<Boolean> postConstructCalled;

    private Property<Boolean> preDestroyCalled;

    public Property<Boolean> postConstructCalledProperty() {
        return postConstructCalled;
    }

    public Property<Boolean> preDestroyCalledProperty() {
        return preDestroyCalled;
    }

    public void setPostConstructCalled(Boolean postConstructCalled) {
        this.postConstructCalled.set(postConstructCalled);
    }

    public void setPreDestroyCalled(Boolean preDestroyCalled) {
        this.preDestroyCalled.set(preDestroyCalled);
    }

    public Boolean getPostConstructCalled() {
        return postConstructCalled.get();
    }

    public Boolean getPreDestroyCalled() {
        return preDestroyCalled.get();
    }
}
