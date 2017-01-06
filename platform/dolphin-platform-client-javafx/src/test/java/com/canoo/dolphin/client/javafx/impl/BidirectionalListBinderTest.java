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
package com.canoo.dolphin.client.javafx.impl;

import com.canoo.dolphin.client.javafx.binding.FXBinder;
import com.canoo.dolphin.collections.ObservableList;
import com.canoo.implementation.dolphin.collections.ObservableArrayList;
import javafx.collections.FXCollections;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

public class BidirectionalListBinderTest {

    //////////////////
    // Initialisation
    //////////////////
    @Test
    public void shouldClearElements() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        javaFXList.addAll("1", "2", "3");
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();

        // when:
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);

        // then:
        assertThat(javaFXList, empty());
    }

    @Test
    public void shouldAddElements() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        dolphinList.addAll(1, 2, 3);

        // when:
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);

        // then:
        assertThat(javaFXList, contains("1", "2", "3"));
    }

    @Test
    public void shouldReplaceElements() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        javaFXList.addAll("41", "42");
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        dolphinList.addAll(1, 2, 3);

        // when:
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);

        // then:
        assertThat(javaFXList, contains("1", "2", "3"));
    }



    ////////////////////////////////
    // Parameter check
    ////////////////////////////////
    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowNPEIfJavaFXListIsNull() {
        // when:
        FXBinder.bind((javafx.collections.ObservableList)null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowNPEIfDolphinListIsNull() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();

        // when:
        FXBinder.bind(javaFXList).bidirectionalTo(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowNPEIfDolphinListIsNull_WithConverters() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();

        // when:
        FXBinder.bind(javaFXList).bidirectionalTo(null, Function.identity(), Function.identity());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowNPEIfConverterIsNull() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<String> dolphinList = new ObservableArrayList<>();

        // when:
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, null, Function.identity());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowNPEIfBackconverterIsNull() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<String> dolphinList = new ObservableArrayList<>();

        // when:
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Function.identity(), null);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void shouldThrowIAEIfJavaFXListIsBound() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<String> dolphinList1 = new ObservableArrayList<>();
        final ObservableList<String> dolphinList2 = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).to(dolphinList1, Function.identity());

        // when:
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList2, Function.identity(), Function.identity());
    }



    ////////////////////////////////
    // Add elements to Dolphin List
    ////////////////////////////////

    @Test
    public void shouldAddSingleElementToEmptyDolphinList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);

        // when:
        javaFXList.add("1");

        // then:
        assertThat(dolphinList, contains(1));
    }

    @Test
    public void shouldAddMultipleElementsToEmptyDolphinList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);

        // when:
        javaFXList.addAll("1", "2", "3");

        // then:
        assertThat(dolphinList, contains(1, 2, 3));
    }

    @Test
    public void shouldAddSingleElementAtBeginningOfDolphinList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        dolphinList.addAll(41, 42, 43);

        // when:
        javaFXList.add(0, "1");

        // then:
        assertThat(dolphinList, contains(1, 41, 42, 43));
    }

    @Test
    public void shouldAddMultipleElementsAtBeginningOfDolphinList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        dolphinList.addAll(41, 42, 43);

        // when:
        javaFXList.addAll(0, Arrays.asList("1", "2", "3"));

        // then:
        assertThat(dolphinList, contains(1, 2, 3, 41, 42, 43));
    }

    @Test
    public void shouldAddSingleElementInMiddleOfDolphinList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        dolphinList.addAll(41, 42, 43);

        // when:
        javaFXList.add(1, "1");

        // then:
        assertThat(dolphinList, contains(41, 1, 42, 43));
    }

    @Test
    public void shouldAddMultipleElementsInMiddleOfDolphinList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        dolphinList.addAll(41, 42, 43);

        // when:
        javaFXList.addAll(1, Arrays.asList("1", "2", "3"));

        // then:
        assertThat(dolphinList, contains(41, 1, 2, 3, 42, 43));
    }

    @Test
    public void shouldAddSingleElementAtEndOfDolphinList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        dolphinList.addAll(41, 42, 43);

        // when:
        javaFXList.add("1");

        // then:
        assertThat(dolphinList, contains(41, 42, 43, 1));
    }

    @Test
    public void shouldAddMultipleElementsAtEndOfDolphinList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        dolphinList.addAll(41, 42, 43);

        // when:
        javaFXList.addAll(Arrays.asList("1", "2", "3"));

        // then:
        assertThat(dolphinList, contains(41, 42, 43, 1, 2, 3));
    }



    /////////////////////////////////////
    // Remove elements from Dolphin List
    /////////////////////////////////////

    @Test
    public void shouldRemoveSingleElementFromSingleElementDolphinList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        dolphinList.add(1);

        // when:
        javaFXList.remove("1");

        // then:
        assertThat(dolphinList, empty());
    }

    @Test
    public void shouldRemoveAllElementsFromDolphinList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        dolphinList.addAll(1, 2, 3);

        // when:
        javaFXList.clear();

        // then:
        assertThat(dolphinList, empty());
    }

    @Test
    public void shouldRemoveSingleElementAtBeginningOfDolphinList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        dolphinList.addAll(1, 2, 3);

        // when:
        javaFXList.remove("1");

        // then:
        assertThat(dolphinList, contains(2, 3));
    }

    @Test
    public void shouldRemoveMultipleElementsAtBeginningOfDolphinList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        dolphinList.addAll(1, 2, 3, 4, 5);

        // when:
        javaFXList.subList(0, 3).clear();

        // then:
        assertThat(dolphinList, contains(4, 5));
    }

    @Test
    public void shouldRemoveSingleElementInMiddleOfDolphinList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        dolphinList.addAll(1, 2, 3);

        // when:
        javaFXList.remove("2");

        // then:
        assertThat(dolphinList, contains(1, 3));
    }

    @Test
    public void shouldRemoveMultipleElementsInMiddleOfDolphinList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        dolphinList.addAll(1, 2, 3, 4, 5);

        // when:
        javaFXList.subList(1, 4).clear();

        // then:
        assertThat(dolphinList, contains(1, 5));
    }

    @Test
    public void shouldRemoveSingleElementAtEndOfDolphinList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        dolphinList.addAll(1, 2, 3);

        // when:
        javaFXList.remove("3");

        // then:
        assertThat(dolphinList, contains(1, 2));
    }

    @Test
    public void shouldRemoveMultipleElementsAtEndOfDolphinList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        dolphinList.addAll(1, 2, 3, 4, 5);

        // when:
        javaFXList.subList(2, 5).clear();

        // then:
        assertThat(dolphinList, contains(1, 2));
    }



    ////////////////////////////////////
    // Replace elements in Dolphin List
    ////////////////////////////////////

    @Test
    public void shouldReplaceSingleElementInSingleElementDolphinList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        dolphinList.add(1);

        // when:
        javaFXList.set(0, "42");

        // then:
        assertThat(dolphinList, contains(42));
    }

    @Test
    public void shouldReplaceAllElementsInDolphinList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        dolphinList.addAll(1, 2, 3);

        // when:
        javaFXList.setAll("41", "42", "43");

        // then:
        assertThat(dolphinList, contains(41, 42, 43));
    }

    @Test
    public void shouldReplaceSingleElementAtBeginningOfDolphinList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        dolphinList.addAll(1, 2, 3);

        // when:
        javaFXList.set(0, "42");

        // then:
        assertThat(dolphinList, contains(42, 2, 3));
    }

    @Test
    public void shouldReplaceSingleElementInMiddleOfDolphinList() {
        // given:
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        dolphinList.addAll(1, 2, 3);

        // when:
        javaFXList.set(1, "42");

        // then:
        assertThat(dolphinList, contains(1, 42, 3));
    }

    @Test
    public void shouldReplaceSingleElementAtEndOfDolphinList() {
        // given:
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        dolphinList.addAll(1, 2, 3);

        // when:
        javaFXList.set(2, "42");

        // then:
        assertThat(dolphinList, contains(1, 2, 42));
    }



    ///////////////////////////////
    // Add elements to JavaFX List
    ///////////////////////////////

    @Test
    public void shouldAddSingleElementToEmptyJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);

        // when:
        dolphinList.add(1);

        // then:
        assertThat(javaFXList, contains("1"));
    }

    @Test
    public void shouldAddMultipleElementsToEmptyJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);

        // when:
        dolphinList.addAll(1, 2, 3);

        // then:
        assertThat(javaFXList, contains("1", "2", "3"));
    }

    @Test
    public void shouldAddSingleElementAtBeginningOfJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        javaFXList.addAll("41", "42", "43");

        // when:
        dolphinList.add(0, 1);

        // then:
        assertThat(javaFXList, contains("1", "41", "42", "43"));
    }

    @Test
    public void shouldAddMultipleElementsAtBeginningOfJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        javaFXList.addAll("41", "42", "43");

        // when:
        dolphinList.addAll(0, Arrays.asList(1, 2, 3));

        // then:
        assertThat(javaFXList, contains("1", "2", "3", "41", "42", "43"));
    }

    @Test
    public void shouldAddSingleElementInMiddleOfJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        javaFXList.addAll("41", "42", "43");

        // when:
        dolphinList.add(1, 1);

        // then:
        assertThat(javaFXList, contains("41", "1", "42", "43"));
    }

    @Test
    public void shouldAddMultipleElementsInMiddleOfJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        javaFXList.addAll("41", "42", "43");

        // when:
        dolphinList.addAll(1, Arrays.asList(1, 2, 3));

        // then:
        assertThat(javaFXList, contains("41", "1", "2", "3", "42", "43"));
    }

    @Test
    public void shouldAddSingleElementAtEndOfJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        javaFXList.addAll("41", "42", "43");

        // when:
        dolphinList.add(1);

        // then:
        assertThat(javaFXList, contains("41", "42", "43", "1"));
    }

    @Test
    public void shouldAddMultipleElementsAtEndOfJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        javaFXList.addAll("41", "42", "43");

        // when:
        dolphinList.addAll(Arrays.asList(1, 2, 3));

        // then:
        assertThat(javaFXList, contains("41", "42", "43", "1", "2", "3"));
    }



    /////////////////////////////////////
    // Remove elements from Dolphin List
    /////////////////////////////////////

    @Test
    public void shouldRemoveSingleElementFromSingleElementJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        javaFXList.add("1");

        // when:
        dolphinList.remove(Integer.valueOf(1));

        // then:
        assertThat(javaFXList, empty());
    }

    @Test
    public void shouldRemoveAllElementsFromJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        javaFXList.addAll("1", "2", "3");

        // when:
        dolphinList.clear();

        // then:
        assertThat(javaFXList, empty());
    }

    @Test
    public void shouldRemoveSingleElementAtBeginningOfJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        javaFXList.addAll("1", "2", "3");

        // when:
        dolphinList.remove(Integer.valueOf(1));

        // then:
        assertThat(javaFXList, contains("2", "3"));
    }

    // TODO: Enable once DolphinList.subList() was implemented
    @Test (enabled = false)
    public void shouldRemoveMultipleElementsAtBeginningOfJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        javaFXList.addAll("1", "2", "3", "4", "5");

        // when:
        dolphinList.subList(0, 3).clear();

        // then:
        assertThat(javaFXList, contains(4, 5));
    }

    @Test
    public void shouldRemoveSingleElementInMiddleOfJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        javaFXList.addAll("1", "2", "3");

        // when:
        dolphinList.remove(Integer.valueOf(2));

        // then:
        assertThat(javaFXList, contains("1", "3"));
    }

    // TODO: Enable once DolphinList.subList() was implemented
    @Test (enabled = false)
    public void shouldRemoveMultipleElementsInMiddleOfJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        javaFXList.addAll("1", "2", "3", "4", "5");

        // when:
        dolphinList.subList(1, 4).clear();

        // then:
        assertThat(javaFXList, contains("1", 5));
    }

    @Test
    public void shouldRemoveSingleElementAtEndOfJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        javaFXList.addAll("1", "2", "3");

        // when:
        dolphinList.remove(Integer.valueOf(3));

        // then:
        assertThat(javaFXList, contains("1", "2"));
    }

    // TODO: Enable once DolphinList.subList() was implemented
    @Test (enabled = false)
    public void shouldRemoveMultipleElementsAtEndOfJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        javaFXList.addAll("1", "2", "3", "4", "5");

        // when:
        dolphinList.subList(2, 5).clear();

        // then:
        assertThat(javaFXList, contains("1", "2"));
    }



    ////////////////////////////////////
    // Replace elements in Dolphin List
    ////////////////////////////////////

    @Test
    public void shouldReplaceSingleElementInSingleElementJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        javaFXList.add("1");

        // when:
        dolphinList.set(0, 42);

        // then:
        assertThat(javaFXList, contains("42"));
    }

    @Test
    public void shouldReplaceAllElementsInJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        javaFXList.addAll("1", "2", "3");

        // when:
        dolphinList.setAll(41, 42, 43);

        // then:
        assertThat(javaFXList, contains("41", "42", "43"));
    }

    @Test
    public void shouldReplaceSingleElementAtBeginningOfJavaFXList() {
        // given:
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        javaFXList.addAll("1", "2", "3");

        // when:
        dolphinList.set(0, 42);

        // then:
        assertThat(javaFXList, contains("42", "2", "3"));
    }

    @Test
    public void shouldReplaceSingleElementInMiddleOfJavaFXList() {
        // given:
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        javaFXList.addAll("1", "2", "3");

        // when:
        dolphinList.set(1, 42);

        // then:
        assertThat(javaFXList, contains("1", "42", "3"));
    }

    @Test
    public void shouldReplaceSingleElementAtEndOfJavaFXList() {
        // given:
        final ObservableList<Integer> dolphinList = new ObservableArrayList<>();
        final javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();
        FXBinder.bind(javaFXList).bidirectionalTo(dolphinList, Object::toString, Integer::parseInt);
        javaFXList.addAll("1", "2", "3");

        // when:
        dolphinList.set(2, 42);

        // then:
        assertThat(javaFXList, contains("1", "2", "42"));
    }
}
