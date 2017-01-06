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
package com.canoo.implementation.dolphin.collections;

import com.canoo.dolphin.collections.ListChangeEvent;
import com.canoo.dolphin.collections.ObservableList;

import java.util.Collections;
import java.util.List;

public class ListChangeEventImpl<E> implements ListChangeEvent<E> {

    private final ObservableList<E> source;
    private final List<Change<E>> changes;

    public ListChangeEventImpl(ObservableList<E> source, int from, int to, List<E> removedElements) {
        this(source, Collections.<Change<E>>singletonList(new ChangeImpl<>(from, to, removedElements)));
    }

    public ListChangeEventImpl(ObservableList<E> source, List<Change<E>> changes) {
        if (source == null || changes == null) {
            throw new NullPointerException("Parameters 'source' and 'changes' cannot be null");
        }
        if (changes.isEmpty()) {
            throw new IllegalArgumentException("ChangeList cannot be empty");
        }
        this.source = source;
        this.changes = changes;
    }

    @Override
    public ObservableList<E> getSource() {
        return source;
    }

    @Override
    public List<Change<E>> getChanges() {
        return changes;
    }

    public static class ChangeImpl<S> implements Change<S> {

        private final int from;
        private final int to;
        private final List<S> removedElements;

        public ChangeImpl(int from, int to, List<S> removedElements) {
            if (from < 0) {
                throw new IllegalArgumentException("Parameter 'from' cannot be negative");
            }
            if (to < from) {
                throw new IllegalArgumentException("Parameter 'to' cannot be smaller than 'from'");
            }
            if (removedElements == null) {
                throw new NullPointerException("Parameter 'removedElements' cannot be null");
            }
            this.from = from;
            this.to = to;
            this.removedElements = removedElements;
        }

        @Override
        public int getFrom() {
            return from;
        }

        @Override
        public int getTo() {
            return to;
        }

        @Override
        public List<S> getRemovedElements() {
            return removedElements;
        }

        @Override
        public boolean isAdded() {
            return getTo() > getFrom() && getRemovedElements().isEmpty();
        }

        @Override
        public boolean isRemoved() {
            return getTo() == getFrom() && !getRemovedElements().isEmpty();
        }

        @Override
        public boolean isReplaced() {
            return getTo() > getFrom() && !getRemovedElements().isEmpty();
        }
    }
}
