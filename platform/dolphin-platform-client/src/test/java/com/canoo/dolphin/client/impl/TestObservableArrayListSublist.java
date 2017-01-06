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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

public class TestObservableArrayListSublist {

    // TODO Test for modification (incl. listeners)

    @Test
    public void sublistOnEmptyList_shouldReturnEmptyList() {
        final ObservableArrayList<String> list = new ObservableArrayList<>();

        final List<String> sublist = list.subList(0, 0);

        assertThat(sublist, empty());
    }

    @Test
    public void sublistOnNonEmptyList_shouldReturnSubList() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");

        final List<String> sublist = list.subList(1, 2);

        assertThat(sublist, is(Arrays.asList("2")));
    }

    @Test (expectedExceptions = IndexOutOfBoundsException.class)
    public void sublistWithNegativeStart_shouldThrowException() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");

        list.subList(-1, 0);
    }

    @Test (expectedExceptions = IndexOutOfBoundsException.class)
    public void sublistWithTooLargeEnd_shouldThrowException() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");

        list.subList(0, 4);
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void sublistWithOutOfOrderIndexes_shouldThrowException() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");

        list.subList(2, 1);
    }
}
