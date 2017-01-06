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
package com.canoo.dolphin.impl.collections;

import com.canoo.dolphin.collections.ListChangeEvent;
import com.canoo.dolphin.collections.ObservableList;
import com.canoo.implementation.dolphin.collections.ListChangeEventImpl;
import mockit.Mocked;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TestListChangeEvent {

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void constructingChangeWithNegativeFrom_shouldThrowException() {
        new ListChangeEventImpl.ChangeImpl<>(-1, 1, Collections.<String>emptyList());
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void constructingChangeWithToSmallerThanFrom_shouldThrowException() {
        new ListChangeEventImpl.ChangeImpl<>(1, 0, Collections.<String>emptyList());
    }

    @Test (expectedExceptions = NullPointerException.class)
    public void constructingChangeWithNullRemovedList_shouldThrowException() {
        new ListChangeEventImpl.ChangeImpl<>(0, 1, null);
    }

    @Test (expectedExceptions = NullPointerException.class)
    public void constructingEventWithNullSource_shouldThrowException() {
        final ListChangeEvent.Change<String> change= new ListChangeEventImpl.ChangeImpl<>(0, 1, Collections.<String>emptyList());
        new ListChangeEventImpl<>(null, Collections.singletonList(change));
    }

    @Test (expectedExceptions = NullPointerException.class)
    public void constructingEventWithNullChangeList_shouldThrowException(@Mocked ObservableList<String> source) {
        new ListChangeEventImpl<>(source, null);
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void constructingEventWithEmptyChangeList_shouldThrowException(@Mocked ObservableList<String> source) {
        new ListChangeEventImpl<>(source, Collections.<ListChangeEvent.Change<String>>emptyList());
    }

    @Test
    public void newElementsButNoRemovedElements_shouldReturnIsAddedOnly(@Mocked ObservableList<String> source) {
        final ListChangeEvent<String> event = new ListChangeEventImpl<>(source, 0, 1, Collections.<String>emptyList());

        final ListChangeEvent.Change<String> change = event.getChanges().get(0);

        assertThat(change.isAdded(), is(true));
        assertThat(change.isRemoved(), is(false));
        assertThat(change.isReplaced(), is(false));
    }

    @Test
    public void noNewElementsButRemovedElements_shouldReturnIsRemovedOnly(@Mocked ObservableList<String> source) {
        final ListChangeEvent<String> event = new ListChangeEventImpl<>(source, 1, 1, Collections.singletonList("Goodbye"));

        final ListChangeEvent.Change<String> change = event.getChanges().get(0);

        assertThat(change.isAdded(), is(false));
        assertThat(change.isRemoved(), is(true));
        assertThat(change.isReplaced(), is(false));
    }

    @Test
    public void newElementsAndRemovedElements_shouldReturnIsReplacedOnly(@Mocked ObservableList<String> source) {
        final ListChangeEvent<String> event = new ListChangeEventImpl<>(source, 0, 1, Collections.singletonList("Goodbye"));

        final ListChangeEvent.Change<String> change = event.getChanges().get(0);

        assertThat(change.isAdded(), is(false));
        assertThat(change.isRemoved(), is(false));
        assertThat(change.isReplaced(), is(true));
    }
}
