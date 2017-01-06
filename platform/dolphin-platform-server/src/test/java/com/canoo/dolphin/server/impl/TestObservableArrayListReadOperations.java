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
package com.canoo.dolphin.server.impl;

import com.canoo.implementation.dolphin.collections.ObservableArrayList;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;

public class TestObservableArrayListReadOperations {

    @Test
    public void sizeOfEmptyList_shouldBeZero() {
        final ObservableArrayList<String> list = new ObservableArrayList<>();

        assertThat(list.size(), is(0));
    }

    @Test
    public void sizeOfNonEmptyList_shouldBeThree() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");

        assertThat(list.size(), is(3));
    }
    @Test
    public void isEmptyOfEmptyList_shouldBeTrue() {
        final ObservableArrayList<String> list = new ObservableArrayList<>();

        assertThat(list.isEmpty(), is(true));
    }

    @Test
    public void isEmptyOfNonEmptyList_shouldBeFalse() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");

        assertThat(list.isEmpty(), is(false));
    }

    @Test
    public void containsForAnExistingElement_shouldBeFound() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");

        assertThat(list.contains("2"), is(true));
    }

    @Test
    public void containsForNonExistingElement_shouldNotBeFound() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");

        assertThat(list.contains("4"), is(false));
    }

    @Test
    public void get_shouldReturnElement() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");

        assertThat(list.get(1), is("2"));
    }

    @Test (expectedExceptions = IndexOutOfBoundsException.class)
    public void getWithNegativeIndex_shouldThrowException() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");

        list.get(-1);
    }

    @Test (expectedExceptions = IndexOutOfBoundsException.class)
    public void getWithIndexTooLarge_shouldThrowException() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");

        list.get(3);
    }

    @Test
    public void indexOfExistingElement_shouldReturnFirstIndex() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3", "2", "1");

        assertThat(list.indexOf("2"), is(1));
    }

    @Test
    public void indexOfNonExistingElement_shouldReturnMinusOne() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3", "2", "1");

        assertThat(list.indexOf("4"), is(-1));
    }

    @Test
    public void lastIndexOfExistingElement_shouldReturnLastIndex() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3", "2", "1");

        assertThat(list.lastIndexOf("2"), is(3));
    }

    @Test
    public void lastIndexOfNonExistingElement_shouldReturnMinusOne() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3", "2", "1");

        assertThat(list.lastIndexOf("4"), is(-1));
    }

    @Test
    public void toArrayOnEmptyList_shouldReturnEmptyArray() {
        final ObservableArrayList<String> list = new ObservableArrayList<>();

        final Object[] result = list.toArray();

        assertThat(result, emptyArray());
    }

    @Test
    public void toArrayOnNonEmptyList_shouldReturnNonEmptyArray() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");

        final Object[] result = list.toArray();

        assertThat(result, is(new Object[]{"1", "2", "3"}));
    }

    @Test
    public void toArrayWithGivenArrayOnEmptyList_shouldReturnEmptyArray() {
        final ObservableArrayList<String> list = new ObservableArrayList<>();
        final Object[] given = new Object[0];

        final Object[] result = list.toArray(given);

        assertThat(result, sameInstance(given));
        assertThat(result, emptyArray());
    }

    @Test
    public void toArrayWithGivenTooLongArrayOnEmptyList_shouldSetArrayElementToNull() {
        final ObservableArrayList<String> list = new ObservableArrayList<>();
        final Object[] given = new Object[] {"42"};

        final Object[] result = list.toArray(given);

        assertThat(result, sameInstance(given));
        assertThat(result, is(new Object[]{null}));
    }

    @Test
    public void toArrayWithGivenArrayOnNonEmptyList_shouldReturnNonEmptyArray() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final Object[] given = new Object[3];

        final Object[] result = list.toArray(given);

        assertThat(result, sameInstance(given));
        assertThat(result, is(new Object[] {"1", "2", "3"}));
    }

    @Test
    public void toArrayWithGivenArrayTooSmallOnNonEmptyList_shouldReturnNonEmptyArray() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final Object[] given = new Object[2];

        final Object[] result = list.toArray(given);

        assertThat(result, not(sameInstance(given)));
        assertThat(result, is(new Object[] {"1", "2", "3"}));
    }

    @Test
    public void toArrayWithGivenArrayTooLargeOnNonEmptyList_shouldReturnNonEmptyArray() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final Object[] given = new Object[] {"42", "43", "44", "45"};

        final Object[] result = list.toArray(given);

        assertThat(result, sameInstance(given));
        assertThat(result, is(new Object[] {"1", "2", "3", null}));
    }

    @Test
    public void hashCodeForEqualLists_shouldBeEqual() {
        assertThat(new ObservableArrayList<>().hashCode(), is(Collections.emptyList().hashCode()));

        assertThat(new ObservableArrayList<>("42").hashCode(), is(Collections.singletonList("42").hashCode()));

        assertThat(new ObservableArrayList<>("1", "2", "3").hashCode(), is(Arrays.asList("1", "2", "3").hashCode()));
    }

    @Test
    public void equalsForEqualLists_shouldWorkAsExpected() {
        final ObservableArrayList<String> observableList0 = new ObservableArrayList<>();
        final ObservableArrayList<String> observableList1 = new ObservableArrayList<>("42");
        final ObservableArrayList<String> observableList2 = new ObservableArrayList<>("1", "2", "3");

        final List<String> list0 = Collections.emptyList();
        final List<String> list1 = Collections.singletonList("42");
        final List<String> list2 = Arrays.asList("1", "2", "3");
        final List<String> list3 = Arrays.asList("1", "2", "4");

        assertThat(observableList0.equals(observableList0), is(true));
        assertThat(observableList0.equals(observableList1), is(false));
        assertThat(observableList0.equals(observableList2), is(false));
        assertThat(observableList0.equals(list0), is(true));
        assertThat(observableList0.equals(list1), is(false));
        assertThat(observableList0.equals(list2), is(false));
        assertThat(observableList0.equals(list3), is(false));

        assertThat(observableList1.equals(observableList0), is(false));
        assertThat(observableList1.equals(observableList1), is(true));
        assertThat(observableList1.equals(observableList2), is(false));
        assertThat(observableList1.equals(list0), is(false));
        assertThat(observableList1.equals(list1), is(true));
        assertThat(observableList1.equals(list2), is(false));
        assertThat(observableList0.equals(list3), is(false));

        assertThat(observableList2.equals(observableList0), is(false));
        assertThat(observableList2.equals(observableList1), is(false));
        assertThat(observableList2.equals(observableList2), is(true));
        assertThat(observableList2.equals(list0), is(false));
        assertThat(observableList2.equals(list1), is(false));
        assertThat(observableList2.equals(list2), is(true));
        assertThat(observableList0.equals(list3), is(false));
    }


}
