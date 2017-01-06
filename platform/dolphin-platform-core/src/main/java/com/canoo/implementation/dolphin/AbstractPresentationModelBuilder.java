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
package com.canoo.implementation.dolphin;

import org.opendolphin.core.PresentationModel;

import java.util.UUID;

public abstract class AbstractPresentationModelBuilder<T extends PresentationModel> implements PresentationModelBuilder<T> {

    protected String type;

    protected String id;

    public AbstractPresentationModelBuilder() {
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public PresentationModelBuilder<T> withType(final String type) {
        this.type = type;
        return this;
    }

    @Override
    public PresentationModelBuilder<T> withId(final String id) {
        this.id = id;
        return this;
    }
}
