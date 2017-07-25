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
package com.canoo.impl.server.gc;

import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dp.impl.platform.core.IdentitySet;
import com.canoo.dolphin.mapping.Property;
import com.canoo.platform.server.DolphinModel;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds an instance of a dolphin bean (see {@link com.canoo.dolphin.mapping.DolphinBean}) for the internal
 * garbage collection (see {@link GarbageCollector}).
 */
public class Instance {

    private Object bean;

    private boolean rootBean;

    private IdentitySet<Property> properties;
    private IdentitySet<ObservableList> lists;

    private List<Reference> references;

    /**
     * Constructor
     * @param bean the dolphin bean
     * @param rootBean if true this is a root bean as defined by {@link DolphinModel}
     * @param properties a set that contains all properties of the given bean
     * @param lists a list that contains all lists of the given bean
     */
    public Instance(Object bean, boolean rootBean, IdentitySet<Property> properties, IdentitySet<ObservableList> lists) {
        this.bean = bean;
        this.rootBean = rootBean;
        this.properties = properties;
        this.lists = lists;
        references = new ArrayList<>();
    }

    /**
     * Getter for the internal dolphin bean
     * @return the bean
     */
    public Object getBean() {
        return bean;
    }

    /**
     * Returns all references of the bean
     * @return list of all references
     */
    public List<Reference> getReferences() {
        return references;
    }

    /**
     * Returns a set that contains all properties of the dolphin bean
     * @return a set that contains all properties of the dolphin bean
     */
    public IdentitySet<Property> getProperties() {
        return properties;
    }

    /**
     * Returns a set that contains all observable lists of the dolphin bean
     * @return a set that contains all observable lists of the dolphin bean
     */
    public IdentitySet<ObservableList> getLists() {
        return lists;
    }

    /**
     * Returns true if the dolphin bean instance is a root bean
     * @return true if this is a root bean
     */
    public boolean isRootBean() {
        return rootBean;
    }

    /**
     * Return true if the dolphin bean instance is a root bean or is referenced by a root bean
     * @return true if the dolphin bean instance is a root bean or is referenced by a root bean
     */
    public boolean isReferencedByRoot() {
        if(rootBean) {
            return true;
        }
        for(Reference reference : references) {
            Instance parent = reference.getParent();
            if(parent.isReferencedByRoot()) {
                return true;
            }
        }
        return false;
    }
}
