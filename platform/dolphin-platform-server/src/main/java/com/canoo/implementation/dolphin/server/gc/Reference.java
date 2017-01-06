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
package com.canoo.implementation.dolphin.server.gc;

/**
 * Describes a direct reference between 2 dolphin beans (see {@link com.canoo.dolphin.mapping.DolphinBean}). In each
 * reference one dolphin bean must be the parent that holds the reference to the other dolphin bean that is defined as child.
 * Internally the dolphin beans are defined by {@link Instance} instances that hold additional informationen next to the bean instance.
 */
public abstract class Reference {

    private Instance parent;

    private Instance child;

    /**
     * Constructor
     * @param parent the parent dolphin bean
     * @param child the child dolphin bean
     */
    public Reference(Instance parent, Instance child) {
        this.parent = parent;
        this.child = child;
    }

    /**
     * Returns the parent dolphin bean
     * @return the parent dolphin bean
     */
    public Instance getParent() {
        return parent;
    }

    /**
     * Returns true if this reference is part of a circular reference.
     * @return true if this reference is part of a circular reference.
     */
    public boolean hasCircularReference() {
        return recursiveCircularReferenceCheck(parent);
    }

    private boolean recursiveCircularReferenceCheck(Instance currentInstance) {
        if(currentInstance == this.child) {
            return true;
        }
        for(Reference reference : currentInstance.getReferences()) {
            if(recursiveCircularReferenceCheck(reference.getParent())) {
                return true;
            }
        }
        return false;
    }
}
