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
package com.canoo.dolphin.server.impl.gc;

import com.canoo.dolphin.collections.ListChangeEvent;
import com.canoo.implementation.dolphin.collections.ObservableArrayList;
import com.canoo.implementation.dolphin.server.gc.GarbageCollector;

/**
 * Created by hendrikebbers on 20.01.16.
 */
public class ObservableListWithGcSupport<E> extends ObservableArrayList<E> {

    private final GarbageCollector garbageCollector;

    public ObservableListWithGcSupport(final GarbageCollector garbageCollector) {
        this.garbageCollector = garbageCollector;
    }

    protected void notifyInternalListeners(final ListChangeEvent<E> e) {
        for (ListChangeEvent.Change<? extends E> c : e.getChanges()) {
            if (c.isRemoved()) {
                for (E elem : c.getRemovedElements()) {
                    garbageCollector.onRemovedFromList(e.getSource(), elem);
                }
            } else if (c.isAdded()) {
                for (int i = c.getFrom(); i < c.getTo(); i++) {
                    garbageCollector.onAddedToList(e.getSource(), e.getSource().get(i));
                }
            } else if (c.isReplaced()) {
                throw new RuntimeException("Not yet implemented!");
            }
        }
    }
}
