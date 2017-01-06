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
import com.canoo.dolphin.collections.ListChangeListener;
import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.event.Subscription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class ObservableArrayList<E> implements ObservableList<E> {

    private final ArrayList<E> list;
    private final List<ListChangeListener<? super E>> listeners = new CopyOnWriteArrayList<>();

    public ObservableArrayList() {
        list = new ArrayList<>();
    }

    public ObservableArrayList(int initialCapacity) {
        list = new ArrayList<>(initialCapacity);
    }

    public ObservableArrayList(Collection<? extends E> c) {
        list = new ArrayList<>(c);
    }

    @SafeVarargs
    public ObservableArrayList(E... elements) {
        this(Arrays.asList(elements));
    }

    protected void fireListChanged(ListChangeEvent<E> event) {
        notifyInternalListeners(event);
        notifyExternalListeners(event);
    }



    protected void notifyInternalListeners(ListChangeEvent<E> event) {

    }

    protected void notifyExternalListeners(ListChangeEvent<E> event) {
        for (final ListChangeListener<? super E> listener : listeners) {
            listener.listChanged(event);
        }
    }

    public void internalSplice(int from, int to, Collection<? extends E> newElements) {
        final List<E> slice = list.subList(from, to);
        final List<E> removedElements = new ArrayList<>(slice);
        slice.clear();
        list.addAll(from, newElements);
        notifyExternalListeners(new ListChangeEventImpl<E>(this, from, from + newElements.size(), removedElements));
    }

    @Override
    public Subscription onChanged(final ListChangeListener<? super E> listener) {
        listeners.add(listener);
        return new Subscription() {
            @Override
            public void unsubscribe() {
                listeners.remove(listener);
            }
        };
    }

    @Override
    public boolean addAll(E... elements) {
        return addAll(Arrays.asList(elements));
    }

    @Override
    public boolean setAll(E... elements) {
        return setAll(Arrays.asList(elements));
    }

    @Override
    public boolean removeAll(E... elements) {
        return removeAll(Arrays.asList(elements));
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return new ListIteratorWrapper(list.listIterator());
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(E e) {
        add(list.size(), e);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        final int index = list.indexOf(o);
        if (index >= 0) {
            remove(index);
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return addAll(list.size(), c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if (c.isEmpty()) {
            return false;
        }
        list.addAll(index, c);
        fireListChanged(new ListChangeEventImpl<>(this, index, index + c.size(), Collections.<E>emptyList()));
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        // TODO Implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        // TODO Implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void clear() {
        if (isEmpty()) {
            return;
        }
        final ArrayList<E> removed = new ArrayList<>(list);
        list.clear();
        fireListChanged(new ListChangeEventImpl<>(this, 0, 0, removed));
    }

    @Override
    public E get(int index) {
        return list.get(index);
    }

    @Override
    public E set(int index, E element) {
        final E oldElement = list.set(index, element);
        fireListChanged(new ListChangeEventImpl<>(this, index, index + 1, Collections.singletonList(oldElement)));
        return oldElement;
    }

    @Override
    public void add(int index, E element) {
        list.add(index, element);
        fireListChanged(new ListChangeEventImpl<>(this, index, index + 1, Collections.<E>emptyList()));
    }

    @Override
    public E remove(int index) {
        final E oldElement = list.remove(index);
        fireListChanged(new ListChangeEventImpl<>(this, index, index, Collections.singletonList(oldElement)));
        return oldElement;
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return new ListIteratorWrapper(list.listIterator());
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new ListIteratorWrapper(list.listIterator(index));
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        // TODO Implement wrapper to send events if sublist is modified
        return Collections.unmodifiableList(list.subList(fromIndex, toIndex));
    }

    private class ListIteratorWrapper implements ListIterator<E> {

        private final ListIterator<E> iterator;

        private ListIteratorWrapper (ListIterator<E> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public E next() {
            return iterator.next();
        }

        @Override
        public boolean hasPrevious() {
            return iterator.hasPrevious();
        }

        @Override
        public E previous() {
            return iterator.previous();
        }

        @Override
        public int nextIndex() {
            return iterator.nextIndex();
        }

        @Override
        public int previousIndex() {
            return iterator.previousIndex();
        }

        @Override
        public void remove() {
            // TODO Implement
            throw new UnsupportedOperationException("Not implemented yet");
        }

        @Override
        public void set(E e) {
            // TODO Implement
            throw new UnsupportedOperationException("Not implemented yet");
        }

        @Override
        public void add(E e) {
            // TODO Implement
            throw new UnsupportedOperationException("Not implemented yet");
        }
    }

    @Override
    public boolean setAll(Collection<? extends E> col) {
        clear();
        return addAll(col);
    }

    @Override
    public boolean equals(Object o) {
        return list.equals(o);
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }

    @Override
    public String toString() {
        return list.toString();
    }
}