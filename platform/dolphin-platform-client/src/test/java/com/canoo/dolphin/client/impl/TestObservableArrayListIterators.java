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
package com.canoo.dolphin.client.impl;

import com.canoo.implementation.dolphin.collections.ObservableArrayList;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TestObservableArrayListIterators {

    // TODO Test for modification (incl. listeners)

    //////////////////////////////////////////
    // iterator()
    //////////////////////////////////////////
    @Test
    public void iteratorHasNextOnEmptyList_shouldReturnFalse() {
        final ObservableArrayList<String> list = new ObservableArrayList<>();

        assertThat(list.iterator().hasNext(), is(false));
    }

    @Test (expectedExceptions = NoSuchElementException.class)
    public void iteratorNextOnEmptyList_shouldThrowException() {
        final ObservableArrayList<String> list = new ObservableArrayList<>();

        list.iterator().next();
    }

    @Test
    public void iteratorHasNextOnThreeElementsList_shouldReturnTrueForEachElement() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final Iterator<String> iterator = list.iterator();

        assertThat(iterator.hasNext(), is(true));
        iterator.next();
        assertThat(iterator.hasNext(), is(true));
        iterator.next();
        assertThat(iterator.hasNext(), is(true));
        iterator.next();
        assertThat(iterator.hasNext(), is(false));
    }

    @Test
    public void iteratorNextOnThreeElementsList_shouldReturnElements() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final Iterator<String> iterator = list.iterator();

        assertThat(iterator.next(), is("1"));
        assertThat(iterator.next(), is("2"));
        assertThat(iterator.next(), is("3"));
    }

    @Test (expectedExceptions = NoSuchElementException.class)
    public void iteratorNextOnThreeElementsList_shouldThrowExceptionAtTheEnd() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final Iterator<String> iterator = list.iterator();

        iterator.next();
        iterator.next();
        iterator.next();
        iterator.next();
    }





    //////////////////////////////////////////
    // listiterator()
    //////////////////////////////////////////
    @Test
    public void listIteratorHasNextAndHasPreviousOnEmptyList_shouldReturnFalse() {
        final ObservableArrayList<String> list = new ObservableArrayList<>();
        final ListIterator<String> iterator = list.listIterator();

        assertThat(iterator.hasNext(), is(false));
        assertThat(iterator.hasPrevious(), is(false));
    }

    @Test (expectedExceptions = NoSuchElementException.class)
    public void listIteratorNextOnEmptyList_shouldThrowException() {
        final ObservableArrayList<String> list = new ObservableArrayList<>();

        list.listIterator().next();
    }

    @Test (expectedExceptions = NoSuchElementException.class)
    public void listIteratorPreviousOnEmptyList_shouldThrowException() {
        final ObservableArrayList<String> list = new ObservableArrayList<>();

        list.listIterator().previous();
    }

    @Test
    public void listIteratorHasNextAndHasPreviousOnThreeElementsList_shouldReturnTrueForEachElement() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final ListIterator<String> iterator = list.listIterator();

        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.hasPrevious(), is(false));
        iterator.next();
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.hasPrevious(), is(true));
        iterator.next();
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.hasPrevious(), is(true));
        iterator.next();
        assertThat(iterator.hasNext(), is(false));
        assertThat(iterator.hasPrevious(), is(true));
        iterator.previous();
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.hasPrevious(), is(true));
        iterator.previous();
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.hasPrevious(), is(true));
        iterator.previous();
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.hasPrevious(), is(false));
    }

    @Test
    public void listIteratorNextOnThreeElementsList_shouldReturnElements() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final ListIterator<String> iterator = list.listIterator();

        assertThat(iterator.next(), is("1"));
        assertThat(iterator.next(), is("2"));
        assertThat(iterator.previous(), is("2"));
        assertThat(iterator.next(), is("2"));
        assertThat(iterator.next(), is("3"));
        assertThat(iterator.previous(), is("3"));
        assertThat(iterator.previous(), is("2"));
        assertThat(iterator.next(), is("2"));
        assertThat(iterator.previous(), is("2"));
        assertThat(iterator.previous(), is("1"));
    }

    @Test (expectedExceptions = NoSuchElementException.class)
    public void listIteratorNextOnThreeElementsList_shouldThrowExceptionAtTheEnd() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final ListIterator<String> iterator = list.listIterator();

        iterator.next();
        iterator.next();
        iterator.next();
        iterator.next();
    }

    @Test (expectedExceptions = NoSuchElementException.class)
    public void listIteratorPreviousOnThreeElementsList_shouldThrowExceptionAtTheBeginning() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final ListIterator<String> iterator = list.listIterator();

        iterator.previous();
    }

    @Test (expectedExceptions = NoSuchElementException.class)
    public void listIteratorNextOnThreeElementsListMovingBackAndForth_shouldThrowExceptionAtTheEnd() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final ListIterator<String> iterator = list.listIterator();

        iterator.next();
        iterator.previous();
        iterator.next();
        iterator.next();
        iterator.previous();
        iterator.next();
        iterator.next();
        iterator.previous();
        iterator.next();
        iterator.next();
    }

    @Test (expectedExceptions = NoSuchElementException.class)
    public void listIteratorPreviousOnThreeElementsListMovingBackAndForth_shouldThrowExceptionAtTheBeginning() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final ListIterator<String> iterator = list.listIterator();

        iterator.next();
        iterator.next();
        iterator.next();
        iterator.previous();
        iterator.next();
        iterator.previous();
        iterator.previous();
        iterator.next();
        iterator.previous();
        iterator.previous();
        iterator.next();
        iterator.previous();
        iterator.previous();
    }





    //////////////////////////////////////////
    // listiterator(index)
    //////////////////////////////////////////
    @Test
    public void indexedListIteratorHasNextAndHasPreviousOnEmptyList_shouldReturnFalse() {
        final ObservableArrayList<String> list = new ObservableArrayList<>();
        final ListIterator<String> iterator = list.listIterator(0);

        assertThat(iterator.hasNext(), is(false));
        assertThat(iterator.hasPrevious(), is(false));
    }

    @Test (expectedExceptions = NoSuchElementException.class)
    public void indexedListIteratorNextOnEmptyList_shouldThrowException() {
        final ObservableArrayList<String> list = new ObservableArrayList<>();

        list.listIterator(0).next();
    }

    @Test (expectedExceptions = NoSuchElementException.class)
    public void indexedListIteratorPreviousOnEmptyList_shouldThrowException() {
        final ObservableArrayList<String> list = new ObservableArrayList<>();

        list.listIterator(0).previous();
    }

    @Test
    public void indexedListIteratorInBeginningHasNextAndHasPreviousOnThreeElementsList_shouldReturnTrueForEachElement() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final ListIterator<String> iterator = list.listIterator(0);

        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.hasPrevious(), is(false));
        iterator.next();
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.hasPrevious(), is(true));
        iterator.next();
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.hasPrevious(), is(true));
        iterator.next();
        assertThat(iterator.hasNext(), is(false));
        assertThat(iterator.hasPrevious(), is(true));
        iterator.previous();
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.hasPrevious(), is(true));
        iterator.previous();
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.hasPrevious(), is(true));
        iterator.previous();
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.hasPrevious(), is(false));
    }

    @Test
    public void indexedListIteratorInMiddleHasNextAndHasPreviousOnThreeElementsList_shouldReturnTrueForEachElement() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final ListIterator<String> iterator = list.listIterator(1);

        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.hasPrevious(), is(true));
        iterator.next();
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.hasPrevious(), is(true));
        iterator.next();
        assertThat(iterator.hasNext(), is(false));
        assertThat(iterator.hasPrevious(), is(true));
        iterator.previous();
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.hasPrevious(), is(true));
        iterator.previous();
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.hasPrevious(), is(true));
        iterator.previous();
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.hasPrevious(), is(false));
    }

    @Test
    public void indexedListIteratorAtEndHasNextAndHasPreviousOnThreeElementsList_shouldReturnTrueForEachElement() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final ListIterator<String> iterator = list.listIterator(3);

        assertThat(iterator.hasNext(), is(false));
        assertThat(iterator.hasPrevious(), is(true));
        iterator.previous();
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.hasPrevious(), is(true));
        iterator.previous();
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.hasPrevious(), is(true));
        iterator.previous();
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.hasPrevious(), is(false));
    }

    @Test
    public void indexedListIteratorAtBeginningNextOnThreeElementsList_shouldReturnElements() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final ListIterator<String> iterator = list.listIterator(0);

        assertThat(iterator.next(), is("1"));
        assertThat(iterator.next(), is("2"));
        assertThat(iterator.previous(), is("2"));
        assertThat(iterator.next(), is("2"));
        assertThat(iterator.next(), is("3"));
        assertThat(iterator.previous(), is("3"));
        assertThat(iterator.previous(), is("2"));
        assertThat(iterator.next(), is("2"));
        assertThat(iterator.previous(), is("2"));
        assertThat(iterator.previous(), is("1"));
    }

    @Test
    public void indexedListIteratorInMiddleNextOnThreeElementsList_shouldReturnElements() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final ListIterator<String> iterator = list.listIterator(1);

        assertThat(iterator.next(), is("2"));
        assertThat(iterator.previous(), is("2"));
        assertThat(iterator.next(), is("2"));
        assertThat(iterator.next(), is("3"));
        assertThat(iterator.previous(), is("3"));
        assertThat(iterator.previous(), is("2"));
        assertThat(iterator.next(), is("2"));
        assertThat(iterator.previous(), is("2"));
        assertThat(iterator.previous(), is("1"));
    }

    @Test
    public void indexedListIteratorAtEndNextOnThreeElementsList_shouldReturnElements() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final ListIterator<String> iterator = list.listIterator(3);

        assertThat(iterator.previous(), is("3"));
        assertThat(iterator.next(), is("3"));
        assertThat(iterator.previous(), is("3"));
        assertThat(iterator.previous(), is("2"));
        assertThat(iterator.next(), is("2"));
        assertThat(iterator.previous(), is("2"));
        assertThat(iterator.previous(), is("1"));
        assertThat(iterator.next(), is("1"));
        assertThat(iterator.previous(), is("1"));
    }

    @Test (expectedExceptions = NoSuchElementException.class)
    public void indexedListIteratorAtBeginningNextOnThreeElementsList_shouldThrowExceptionAtTheEnd() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final ListIterator<String> iterator = list.listIterator(0);

        iterator.next();
        iterator.previous();
        iterator.next();
        iterator.next();
        iterator.previous();
        iterator.next();
        iterator.next();
        iterator.previous();
        iterator.next();
        iterator.next();
    }

    @Test (expectedExceptions = NoSuchElementException.class)
    public void indexedListIteratorAtEndNextOnThreeElementsList_shouldThrowExceptionAtTheEnd() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final ListIterator<String> iterator = list.listIterator(3);

        iterator.next();
    }

    @Test (expectedExceptions = NoSuchElementException.class)
    public void indexedListIteratorAtBeginningPreviousOnThreeElements_shouldThrowExceptionAtTheBeginning() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final ListIterator<String> iterator = list.listIterator(0);

        iterator.previous();
    }

    @Test (expectedExceptions = NoSuchElementException.class)
    public void indexedListIteratorAtEndPreviousOnThreeElements_shouldThrowExceptionAtTheBeginning() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final ListIterator<String> iterator = list.listIterator(3);

        iterator.previous();
        iterator.next();
        iterator.previous();
        iterator.previous();
        iterator.next();
        iterator.previous();
        iterator.previous();
        iterator.next();
        iterator.previous();
        iterator.previous();
    }

}
