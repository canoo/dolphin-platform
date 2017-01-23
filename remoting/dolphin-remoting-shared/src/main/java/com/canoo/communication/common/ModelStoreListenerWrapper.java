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
package com.canoo.communication.common;

import com.canoo.common.StringUtil;

public class ModelStoreListenerWrapper<A extends Attribute, P extends PresentationModel<A>> implements ModelStoreListener<A, P> {
    private static final String ANY_PRESENTATION_MODEL_TYPE = "*";
    private final String presentationModelType;
    private final ModelStoreListener delegate;

    ModelStoreListenerWrapper(String presentationModelType, ModelStoreListener<A, P> delegate) {
        this.presentationModelType = !StringUtil.isBlank(presentationModelType) ? presentationModelType : ANY_PRESENTATION_MODEL_TYPE;
        this.delegate = delegate;
    }

    private boolean presentationModelTypeMatches(String presentationModelType) {
        return ANY_PRESENTATION_MODEL_TYPE.equals(this.presentationModelType) || this.presentationModelType.equals(presentationModelType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (null == o) return false;

        if (o instanceof ModelStoreListenerWrapper) {
            ModelStoreListenerWrapper that = (ModelStoreListenerWrapper) o;
            return delegate.equals(that.delegate) && presentationModelType.equals(that.presentationModelType);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = presentationModelType.hashCode();
        result = 31 * result + delegate.hashCode();
        return result;
    }

    @Override
    public void modelStoreChanged(ModelStoreEvent<A, P> event) {
        String pmType = event.getPresentationModel().getPresentationModelType();
        if (presentationModelTypeMatches(pmType)) {
            delegate.modelStoreChanged(event);
        }
    }
}
