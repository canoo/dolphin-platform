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

import com.canoo.dolphin.collections.ListChangeEvent;
import com.canoo.dolphin.collections.ListChangeListener;
import com.canoo.dolphin.event.Subscription;
import com.canoo.implementation.dolphin.collections.ObservableArrayList;
import org.hamcrest.MatcherAssert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class TestObservableArrayListWriteOperations {

    // TODO: removeAll, retainAll

    //////////////////////////////////////////
    // add(Object)
    //////////////////////////////////////////
    @Test
    public void removeListener_shouldNotFireListener() {
        final ObservableArrayList<String> list = new ObservableArrayList<>();
        final TestListChangeListener listener = new TestListChangeListener();
        final Subscription subscription = list.onChanged(listener);
        subscription.unsubscribe();

        list.add("42");

        assertThat(listener.calls, is(0));
    }



    //////////////////////////////////////////
    // add(Object)
    //////////////////////////////////////////
    @Test
    public void addToEmptyList_shouldAddElement() {
        final ObservableArrayList<String> list = new ObservableArrayList<>();
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        final boolean result = list.add("42");

        assertThat(result, is(true));
        assertThat(list, is(Collections.singletonList("42")));

        assertThat(listener.calls, is(1));
        assertThat(listener.changes, hasSize(1));
        MatcherAssert.assertThat(listener.changes.get(0), allOf(hasProperty("from", is(0)), hasProperty("to", is(1)), hasProperty("removedElements", empty())));
    }

    @Test
    public void addToNonEmptyList_shouldAddElement() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        final boolean result = list.add("42");

        assertThat(result, is(true));
        assertThat(list, is(Arrays.asList("1", "2", "3", "42")));

        assertThat(listener.calls, is(1));
        assertThat(listener.changes, hasSize(1));
        MatcherAssert.assertThat(listener.changes.get(0), allOf(hasProperty("from", is(3)), hasProperty("to", is(4)), hasProperty("removedElements", empty())));
    }



    //////////////////////////////////////////
    // add(int, Object)
    //////////////////////////////////////////
    @Test
    public void indexedAddToEmptyList_shouldAddElement() {
        final ObservableArrayList<String> list = new ObservableArrayList<>();
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        list.add(0, "42");

        assertThat(list, is(Collections.singletonList("42")));

        assertThat(listener.calls, is(1));
        assertThat(listener.changes, hasSize(1));
        MatcherAssert.assertThat(listener.changes.get(0), allOf(hasProperty("from", is(0)), hasProperty("to", is(1)), hasProperty("removedElements", empty())));
    }

    @Test
    public void indexedAddAtBeginningOfNonEmptyList_shouldAddElement() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        list.add(0, "42");

        assertThat(list, is(Arrays.asList("42", "1", "2", "3")));

        assertThat(listener.calls, is(1));
        assertThat(listener.changes, hasSize(1));
        MatcherAssert.assertThat(listener.changes.get(0), allOf(hasProperty("from", is(0)), hasProperty("to", is(1)), hasProperty("removedElements", empty())));
    }

    @Test
    public void indexedAddInMiddleOfNonEmptyList_shouldAddElement() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        list.add(1, "42");

        assertThat(list, is(Arrays.asList("1", "42", "2", "3")));

        assertThat(listener.calls, is(1));
        assertThat(listener.changes, hasSize(1));
        MatcherAssert.assertThat(listener.changes.get(0), allOf(hasProperty("from", is(1)), hasProperty("to", is(2)), hasProperty("removedElements", empty())));
    }

    @Test
    public void indexedAddAtEndOfNonEmptyList_shouldAddElement() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        list.add(3, "42");

        assertThat(list, is(Arrays.asList("1", "2", "3", "42")));

        assertThat(listener.calls, is(1));
        assertThat(listener.changes, hasSize(1));
        MatcherAssert.assertThat(listener.changes.get(0), allOf(hasProperty("from", is(3)), hasProperty("to", is(4)), hasProperty("removedElements", empty())));
    }

    @Test (expectedExceptions = IndexOutOfBoundsException.class)
    public void indexedAddWithNegativeIndex_shouldThrowException() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        list.add(-1, "42");
    }

    @Test (expectedExceptions = IndexOutOfBoundsException.class)
    public void indexedAddWithTooLargeIndex_shouldThrowException() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        list.add(4, "42");
    }


    //////////////////////////////////////////
    // removePresentationModel(Object)
    //////////////////////////////////////////
    @Test
    public void removeOnSingleElementList_shouldRemoveElement() {
        //given:
        final ObservableArrayList<String> list = new ObservableArrayList<>("42");
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        //when:
        final boolean result = list.remove("42");

        //then:
        assertThat(result, is(true));
        assertThat(list, empty());
        assertThat(listener.calls, is(1));
        assertThat(listener.changes, hasSize(1));
        MatcherAssert.assertThat(listener.changes.get(0), allOf(hasProperty("from", is(0)), hasProperty("to", is(0)), hasProperty("removedElements", is(Collections.singletonList("42")))));
    }

    @Test
    public void removeAtBeginningOfNonEmptyList_shouldRemoveElement() {
        //given:
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        //when:
        final boolean result = list.remove("1");

        //then:
        assertThat(result, is(true));
        assertThat(list, is(Arrays.asList("2", "3")));
        assertThat(listener.calls, is(1));
        assertThat(listener.changes, hasSize(1));
        MatcherAssert.assertThat(listener.changes.get(0), allOf(hasProperty("from", is(0)), hasProperty("to", is(0)), hasProperty("removedElements", is(Collections.singletonList("1")))));
    }

    @Test
    public void removeInMiddleOfNonEmptyList_shouldRemoveElement() {
        //given:
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        //when:
        final boolean result = list.remove("2");

        //then:
        assertThat(result, is(true));
        assertThat(list, is(Arrays.asList("1", "3")));
        assertThat(listener.calls, is(1));
        assertThat(listener.changes, hasSize(1));
        MatcherAssert.assertThat(listener.changes.get(0), allOf(hasProperty("from", is(1)), hasProperty("to", is(1)), hasProperty("removedElements", is(Collections.singletonList("2")))));
    }

    @Test
    public void removeAtEndOfNonEmptyList_shouldRemoveElement() {
        //given:
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        //when:
        final boolean result = list.remove("3");

        //then:
        assertThat(result, is(true));
        assertThat(list, is(Arrays.asList("1", "2")));
        assertThat(listener.calls, is(1));
        assertThat(listener.changes, hasSize(1));
        MatcherAssert.assertThat(listener.changes.get(0), allOf(hasProperty("from", is(2)), hasProperty("to", is(2)), hasProperty("removedElements", is(Collections.singletonList("3")))));
    }

    @Test
    public void removeNonExistingObject_shouldBeNoOp() {
        //given:
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        //when:
        final boolean result = list.remove("42");

        //then:
        assertThat(result, is(false));
        assertThat(list, is(Arrays.asList("1", "2", "3")));
        assertThat(listener.calls, is(0));
    }



    //////////////////////////////////////////
    // removePresentationModel(index)
    //////////////////////////////////////////
    @Test
    public void indexedRemoveOnSingleElementList_shouldRemoveElement() {
        //given:
        final ObservableArrayList<String> list = new ObservableArrayList<>("42");
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        //when:
        final String result = list.remove(0);

        //then:
        assertThat(result, is("42"));
        assertThat(list, empty());
        assertThat(listener.calls, is(1));
        assertThat(listener.changes, hasSize(1));
        MatcherAssert.assertThat(listener.changes.get(0), allOf(hasProperty("from", is(0)), hasProperty("to", is(0)), hasProperty("removedElements", is(Collections.singletonList("42")))));
    }

    @Test
    public void indexedRemoveAtBeginningOfNonEmptyList_shouldRemoveElement() {
        //given:
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        //when:
        final String result = list.remove(0);

        //then:
        assertThat(result, is("1"));
        assertThat(list, is(Arrays.asList("2", "3")));
        assertThat(listener.calls, is(1));
        assertThat(listener.changes, hasSize(1));
        MatcherAssert.assertThat(listener.changes.get(0), allOf(hasProperty("from", is(0)), hasProperty("to", is(0)), hasProperty("removedElements", is(Collections.singletonList("1")))));
    }

    @Test
    public void indexedRemoveInMiddleOfNonEmptyList_shouldRemoveElement() {
        //given:
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        //when:
        final String result = list.remove(1);

        //then:
        assertThat(result, is("2"));
        assertThat(list, is(Arrays.asList("1", "3")));
        assertThat(listener.calls, is(1));
        assertThat(listener.changes, hasSize(1));
        MatcherAssert.assertThat(listener.changes.get(0), allOf(hasProperty("from", is(1)), hasProperty("to", is(1)), hasProperty("removedElements", is(Collections.singletonList("2")))));
    }

    @Test
    public void indexedRemoveAtEndOfNonEmptyList_shouldRemoveElement() {
        //given:
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        //when:
        final String result = list.remove(2);

        //then:
        assertThat(result, is("3"));
        assertThat(list, is(Arrays.asList("1", "2")));
        assertThat(listener.calls, is(1));
        assertThat(listener.changes, hasSize(1));
        MatcherAssert.assertThat(listener.changes.get(0), allOf(hasProperty("from", is(2)), hasProperty("to", is(2)), hasProperty("removedElements", is(Collections.singletonList("3")))));
    }

    @Test (expectedExceptions = IndexOutOfBoundsException.class)
    public void indexedRemoveWithNegativeIndex_shouldThrowException() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        list.remove(-1);
    }

    @Test (expectedExceptions = IndexOutOfBoundsException.class)
    public void indexedRemoveWithTooLargeIndex_shouldThrowException() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        list.remove(4);
    }



    //////////////////////////////////////////
    // addAll(Collection)
    //////////////////////////////////////////
    @Test
    public void addAllToEmptyList_shouldAddElements() {
        //given:
        final ObservableArrayList<String> list = new ObservableArrayList<>();
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        //when:
        final boolean result = list.addAll(Arrays.asList("1", "2", "3"));

        //then:
        assertThat(result, is(true));
        assertThat(list, is(Arrays.asList("1", "2", "3")));
        assertThat(listener.calls, is(1));
        assertThat(listener.changes, hasSize(1));
        MatcherAssert.assertThat(listener.changes.get(0), allOf(hasProperty("from", is(0)), hasProperty("to", is(3)), hasProperty("removedElements", empty())));
    }

    @Test
    public void addAllVarArgsToEmptyList_shouldAddElements() {
        //given:
        final ObservableArrayList<String> list = new ObservableArrayList<>();
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        //when:
        final boolean result = list.addAll("1", "2", "3");

        //then:
        assertThat(result, is(true));
        assertThat(list, is(Arrays.asList("1", "2", "3")));
        assertThat(listener.calls, is(1));
        assertThat(listener.changes, hasSize(1));
        MatcherAssert.assertThat(listener.changes.get(0), allOf(hasProperty("from", is(0)), hasProperty("to", is(3)), hasProperty("removedElements", empty())));
    }

    @Test
    public void addAllToNonEmptyList_shouldAddElements() {
        //given:
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        //when:
        final boolean result = list.addAll(Arrays.asList("42", "43", "44"));

        //then:
        assertThat(result, is(true));
        assertThat(list, is(Arrays.asList("1", "2", "3", "42", "43", "44")));
        assertThat(listener.calls, is(1));
        assertThat(listener.changes, hasSize(1));
        MatcherAssert.assertThat(listener.changes.get(0), allOf(hasProperty("from", is(3)), hasProperty("to", is(6)), hasProperty("removedElements", empty())));
    }

    @Test
    public void addAllVarArgsToNonEmptyList_shouldAddElements() {
        //given:
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        //when:
        final boolean result = list.addAll("42", "43", "44");

        //then:
        assertThat(result, is(true));
        assertThat(list, is(Arrays.asList("1", "2", "3", "42", "43", "44")));
        assertThat(listener.calls, is(1));
        assertThat(listener.changes, hasSize(1));
        MatcherAssert.assertThat(listener.changes.get(0), allOf(hasProperty("from", is(3)), hasProperty("to", is(6)), hasProperty("removedElements", empty())));
    }

    @Test
    public void addAllEmptyList_shouldBeNoOp() {
        //given:
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        //when:
        final boolean result = list.addAll(Collections.<String>emptyList());

        //then:
        assertThat(result, is(false));
        assertThat(list, is(Arrays.asList("1", "2", "3")));
        assertThat(listener.calls, is(0));
    }

    @Test (expectedExceptions = NullPointerException.class)
    public void addAllWithNull_shouldThrowException() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");

        list.addAll((Collection<? extends String>) null);
    }

    //////////////////////////////////////////
    // addAll(Collection)
    //////////////////////////////////////////
    @Test
    public void indexedAddAllToEmptyList_shouldAddElements() {
        final ObservableArrayList<String> list = new ObservableArrayList<>();
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        final boolean result = list.addAll(0, Arrays.asList("1", "2", "3"));

        assertThat(result, is(true));
        assertThat(list, is(Arrays.asList("1", "2", "3")));

        assertThat(listener.calls, is(1));
        assertThat(listener.changes, hasSize(1));
        MatcherAssert.assertThat(listener.changes.get(0), allOf(hasProperty("from", is(0)), hasProperty("to", is(3)), hasProperty("removedElements", empty())));
    }

    @Test
    public void indexedAddAllAtBeginningOfNonEmptyList_shouldAddElement() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        final boolean result = list.addAll(0, Arrays.asList("42", "43", "44"));

        assertThat(result, is(true));
        assertThat(list, is(Arrays.asList("42", "43", "44", "1", "2", "3")));

        assertThat(listener.calls, is(1));
        assertThat(listener.changes, hasSize(1));
        MatcherAssert.assertThat(listener.changes.get(0), allOf(hasProperty("from", is(0)), hasProperty("to", is(3)), hasProperty("removedElements", empty())));
    }

    @Test
    public void indexedAddAllInMiddleOfNonEmptyList_shouldAddElement() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        final boolean result = list.addAll(1, Arrays.asList("42", "43", "44"));

        assertThat(result, is(true));
        assertThat(list, is(Arrays.asList("1", "42", "43", "44", "2", "3")));

        assertThat(listener.calls, is(1));
        assertThat(listener.changes, hasSize(1));
        MatcherAssert.assertThat(listener.changes.get(0), allOf(hasProperty("from", is(1)), hasProperty("to", is(4)), hasProperty("removedElements", empty())));
    }

    @Test
    public void indexedAddAllAtEndOfNonEmptyList_shouldAddElement() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        final boolean result = list.addAll(3, Arrays.asList("42", "43", "44"));

        assertThat(result, is(true));
        assertThat(list, is(Arrays.asList("1", "2", "3", "42", "43", "44")));

        assertThat(listener.calls, is(1));
        assertThat(listener.changes, hasSize(1));
        MatcherAssert.assertThat(listener.changes.get(0), allOf(hasProperty("from", is(3)), hasProperty("to", is(6)), hasProperty("removedElements", empty())));
    }

    @Test (expectedExceptions = IndexOutOfBoundsException.class)
    public void indexedAddAllWithNegativeIndex_shouldThrowException() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        list.addAll(-1, Arrays.asList("42", "43", "44"));
    }

    @Test (expectedExceptions = IndexOutOfBoundsException.class)
    public void indexedAddAllWithTooLargeIndex_shouldThrowException() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        list.addAll(4, Arrays.asList("42", "43", "44"));
    }

    @Test
    public void indexedAddAllEmptyList_shouldBeNoOp() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        final boolean result = list.addAll(0, Collections.<String>emptyList());

        assertThat(result, is(false));
        assertThat(list, is(Arrays.asList("1", "2", "3")));

        assertThat(listener.calls, is(0));
    }

    @Test (expectedExceptions = NullPointerException.class)
    public void indexedAddAllWithNull_shouldThrowException() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");

        list.addAll(0, null);
    }



    //////////////////////////////////////////
    // clear()
    //////////////////////////////////////////
    @Test
    public void clearOnEmtpyList_shouldBeNoOp() {
        final ObservableArrayList<String> list = new ObservableArrayList<>();
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        list.clear();

        assertThat(list, empty());

        assertThat(listener.calls, is(0));
    }

    @Test
    public void clearOnNonEmptyList_shouldClearList() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        list.clear();

        assertThat(list, empty());

        assertThat(listener.calls, is(1));
        assertThat(listener.changes, hasSize(1));
        MatcherAssert.assertThat(listener.changes.get(0), allOf(hasProperty("from", is(0)), hasProperty("to", is(0)), hasProperty("removedElements", is(Arrays.asList("1", "2", "3")))));
    }



    //////////////////////////////////////////
    // set()
    //////////////////////////////////////////
    @Test
    public void setOnSingleElementList_shouldSetElement() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("42");
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        final String result = list.set(0, "1");

        assertThat(result, is("42"));
        assertThat(list, is(Collections.singletonList("1")));

        assertThat(listener.calls, is(1));
        assertThat(listener.changes, hasSize(1));
        MatcherAssert.assertThat(listener.changes.get(0), allOf(hasProperty("from", is(0)), hasProperty("to", is(1)), hasProperty("removedElements", is(Collections.singletonList("42")))));
    }

    @Test
    public void setAtBeginningOfNonEmptyList_shouldSetElement() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        final String result = list.set(0, "42");

        assertThat(result, is("1"));
        assertThat(list, is(Arrays.asList("42", "2", "3")));

        assertThat(listener.calls, is(1));
        assertThat(listener.changes, hasSize(1));
        MatcherAssert.assertThat(listener.changes.get(0), allOf(hasProperty("from", is(0)), hasProperty("to", is(1)), hasProperty("removedElements", is(Collections.singletonList("1")))));
    }

    @Test
    public void setInMiddleOfNonEmptyList_shouldSetElement() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        final String result = list.set(1, "42");

        assertThat(result, is("2"));
        assertThat(list, is(Arrays.asList("1", "42", "3")));

        assertThat(listener.calls, is(1));
        assertThat(listener.changes, hasSize(1));
        MatcherAssert.assertThat(listener.changes.get(0), allOf(hasProperty("from", is(1)), hasProperty("to", is(2)), hasProperty("removedElements", is(Collections.singletonList("2")))));
    }

    @Test
    public void setAtEndOfNonEmptyList_shouldSetElement() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        final TestListChangeListener listener = new TestListChangeListener();
        list.onChanged(listener);

        final String result = list.set(2, "42");

        assertThat(result, is("3"));
        assertThat(list, is(Arrays.asList("1", "2", "42")));

        assertThat(listener.calls, is(1));
        assertThat(listener.changes, hasSize(1));
        MatcherAssert.assertThat(listener.changes.get(0), allOf(hasProperty("from", is(2)), hasProperty("to", is(3)), hasProperty("removedElements", is(Collections.singletonList("3")))));
    }

    @Test (expectedExceptions = IndexOutOfBoundsException.class)
    public void setWithNegativeIndex_shouldThrowException() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        list.set(-1, "42");
    }

    @Test (expectedExceptions = IndexOutOfBoundsException.class)
    public void setWithTooLargeIndex_shouldThrowException() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");
        list.set(4, "42");
    }





    public static class TestListChangeListener implements ListChangeListener<String> {

        private int calls;
        private List<? extends ListChangeEvent.Change<? extends String>> changes = new ArrayList<>();

        @Override
        public void listChanged(ListChangeEvent<? extends String> evt) {
            calls++;
            changes = evt.getChanges();
        }
    }
}
