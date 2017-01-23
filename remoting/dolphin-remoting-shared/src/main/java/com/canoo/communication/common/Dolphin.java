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

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface Dolphin<A extends Attribute, P extends PresentationModel<A>> {

    ModelStore<A, P> getModelStore();

    boolean addPresentationModel(P model);

    boolean removePresentationModel(P model);

    A findAttributeById(String id);

    List<A> findAllAttributesByQualifier(String qualifier);

    Set<String> listPresentationModelIds();

    Collection<P> listPresentationModels();

    List<P> findAllPresentationModelsByType(String presentationModelType);

    P getPresentationModel(String id);

    void removeModelStoreListener(ModelStoreListener listener);

    void removeModelStoreListener(String presentationModelType, ModelStoreListener listener);

    boolean hasModelStoreListener(ModelStoreListener listener);

    void addModelStoreListener(String presentationModelType, ModelStoreListener listener);

    boolean hasModelStoreListener(String presentationModelType, ModelStoreListener listener);

    void addModelStoreListener(ModelStoreListener listener);

    void updateQualifiers(P presentationModel);
}
