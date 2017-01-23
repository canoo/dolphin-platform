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

public abstract class AbstractDolphin<A extends Attribute, P extends PresentationModel<A>> implements Dolphin<A, P> {

    /**
     * Adds a presentation model to the model store.<br/>
     * Presentation model ids should be unique. This method guarantees this condition by disallowing
     * models with duplicate ids to be added.
     *
     * @param model the model to be added.
     * @return if the add operation was successful or not.
     */
    @Override
    public boolean addPresentationModel(P model) {
        return getModelStore().add(model);
    }

    /**
     * Removes a presentation model from the model store.<br/>
     *
     * @param model the model to be removed from the store.
     * @return if the removePresentationModel operation was successful or not.
     */
    @Override
    public boolean removePresentationModel(P model) {
        return getModelStore().remove(model);
    }

    /**
     * Finds an attribute by its id.<br/>
     * <strong>WARNING:</strong> this method may return {@code null} if no match is found.
     *
     * @param id the id to search for.
     * @return an attribute whose id matches the parameter, {@code null} otherwise.
     */
    @Override
    public A findAttributeById(String id) {
        return (A)getModelStore().findAttributeById(id);
    }

    /**
     * Returns a {@code List} of all attributes that share the same qualifier.<br/>
     * Never returns null. The returned {@code List} is immutable.
     *
     * @return a {@code List} of all attributes for which their qualifier was a match.
     */
    @Override
    public List<A> findAllAttributesByQualifier(String qualifier) {
        return (List<A>)getModelStore().findAllAttributesByQualifier(qualifier);
    }

    @Override
    public Set<String> listPresentationModelIds() {
        return getModelStore().listPresentationModelIds();
    }

    @Override
    public Collection<P> listPresentationModels() {
         return (Collection<P>) getModelStore().listPresentationModels();
    }

    @Override
    public List<P> findAllPresentationModelsByType(String presentationModelType) {
        return (List<P>) getModelStore().findAllPresentationModelsByType(presentationModelType);
    }

    /**
     * alias for findPresentationModelById
     */
    @Override
    public P getPresentationModel(String id) {
        return (P) getModelStore().findPresentationModelById(id);
    }

    @Override
    public void removeModelStoreListener(ModelStoreListener listener) {
        getModelStore().removeModelStoreListener(listener);
    }

    @Override
    public void removeModelStoreListener(String presentationModelType, ModelStoreListener listener) {
        getModelStore().removeModelStoreListener(presentationModelType, listener);
    }

    @Override
    public boolean hasModelStoreListener(ModelStoreListener listener) {
        return getModelStore().hasModelStoreListener(listener);
    }

    @Override
    public void addModelStoreListener(String presentationModelType, ModelStoreListener listener) {
        getModelStore().addModelStoreListener(presentationModelType, listener);
    }

    @Override
    public boolean hasModelStoreListener(String presentationModelType, ModelStoreListener listener) {
        return getModelStore().hasModelStoreListener(presentationModelType, listener);
    }

    @Override
    public void addModelStoreListener(ModelStoreListener listener) {
        getModelStore().addModelStoreListener(listener);
    }

    /**
     * For every attribute in the given presentation model, proliferate the attribute value to
     * all attributes that bear the same qualifier and tag.
     */
    // todo dk: not quite sure whether this should be called automatically in some handle() methods
    @Override
    public void updateQualifiers(P presentationModel) {
        for (A source : presentationModel.getAttributes()) {
            if (null == source.getQualifier()) continue;
            for (A target : getModelStore().findAllAttributesByQualifier(source.getQualifier())) {
                target.setValue(source.getValue());
            }
        }
    }
}
