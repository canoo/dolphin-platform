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
package com.canoo.platform.remoting;

import com.canoo.platform.core.functional.Subscription;

import java.util.Collection;
import java.util.List;

/**
 * This interface extends the default {@link List} interface and adds the possibility to observe the list.
 * By adding a {@link ListChangeListener} (see {@link #onChanged(ListChangeListener)}) all mutations of the list can be observed.
 * In addition this class provides some convenience methods.
 * <p>
 * The {@link ObservableList} interface is part of the Dolphin Platform model API. Since the lifecylce of models will be
 * managed by the Dolphin Platform the API don't provide a public implementation for this interface. When defining models
 * a developer only need to use the interface like it is already descriped in the {@link Property}
 * class.
 * Like the {@link Property} a {@link ObservableList} can be used in any Dolphin Platform model
 * (see {@link DolphinBean}).
 * <p>
 * Example:
 * <p>
 * <blockquote>
 * <pre>
 *     {@literal @}DolphinBean
 *     public class MyModel {
 *
 *         {@code private ObservableList<String> values;}
 *
 *         {@code public ObservableList<String> getValues() {
 *              return values;
 *          }
 *         }
 *     }
 * </pre>
 * </blockquote>
 *
 * @param <E> type of elements in the list
 * @author Hendrik Ebbers
 */
public interface ObservableList<E> extends List<E> {

    /**
     * Clears the ObservableList and add all elements from the collection.
     *
     * @param col the collection with elements that will be added to this observableArrayList
     * @return true (as specified by Collection.add(E))
     * @throws NullPointerException if the specified collection contains one or more null elements
     */
    boolean setAll(Collection<? extends E> col);

    /**
     * Adds a change lister to the list that will be fired whenever the content of the list changes. This
     * will happen if an element is added or removed to the list, for example.
     *
     * @param listener The listener that will be registered
     * @return a {@link Subscription} instance that can be used to deregister the listener.
     */
    Subscription onChanged(ListChangeListener<? super E> listener);

    /**
     * Appends all elements to the end of this list
     *
     * @param elements the elements that should be added to the list
     * @return <tt>true</tt> if the list changed as a result of the call
     */
    boolean addAll(E... elements);

    /**
     * Clears the ObservableList and add all the given elements.
     *
     * @param elements the elements that should be set to the list
     * @return <tt>true</tt> if the list changed as a result of the call
     */
    boolean setAll(E... elements);

    /**
     * Removes all given elements from the list
     *
     * @param elements the elements that should be removed from the list
     * @return <tt>true</tt> if the list changed as a result of the call
     */
    boolean removeAll(E... elements);

    /**
     * A var-arg convenience method for the retain method
     * @param elements the elements to be retained
     * @return <tt>true</tt> if list changed as a result of the call
     */
    boolean retainAll(E... elements);

    /**
     * Removes all elements from the list from {@param from} inclusive up to {@param to} exclusive;
     * Essentially a convenience method for sublist(from, to).clear()
     * @param from the start of the range to remove (inclusive)
     * @param to the end of the range to remove (exclusive)
     * @throws IndexOutofBoundsException if an illegal range is provided
     */
    void remove(int from, int to);
}
