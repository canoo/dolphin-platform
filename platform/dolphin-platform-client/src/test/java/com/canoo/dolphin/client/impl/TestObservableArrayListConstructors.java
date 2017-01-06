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
import java.util.Collection;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;

public class TestObservableArrayListConstructors {

    @Test
    public void emptyConstructor_shouldResultInEmptyList() {
        final ObservableArrayList<String> list = new ObservableArrayList<>();

        assertThat(list, empty());
    }

    @Test
    public void constructorWithCapacity_shouldResultInEmptyList() {
        final ObservableArrayList<String> list = new ObservableArrayList<>(10);

        assertThat(list, empty());
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void constructorWithNegativeCapacity_shouldThrowException() {
        new ObservableArrayList<>(-1);
    }

    @Test
    public void constructorWithNormalCollection_shouldResultInFilledList() {
        final ObservableArrayList<String> list = new ObservableArrayList<>(Arrays.asList("1", "2", "3"));

        assertThat(list, hasSize(3));
        assertThat(list, contains("1", "2", "3"));
    }

    @Test
    public void constructorWithEmptyCollection_shouldResultInEmptyList() {
        final ObservableArrayList<String> list = new ObservableArrayList<>(Collections.<String>emptySet());

        assertThat(list, empty());
    }

    @Test (expectedExceptions = NullPointerException.class)
    public void constructorWithNullCollection_shouldThrowException() {
        new ObservableArrayList<>((Collection<String>)null);
    }

    @Test
    public void constructorWithElements_shouldResultInFilledList() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");

        assertThat(list, hasSize(3));
        assertThat(list, contains("1", "2", "3"));
    }

    @Test
    public void constructorWithSingleElements_shouldResultInFilledList() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1");

        assertThat(list, hasSize(1));
        assertThat(list, contains("1"));
    }

    @Test
    public void constructorWithNullElement_shouldResultInFilledList() {
        final ObservableArrayList<String> list = new ObservableArrayList<>((String)null);

        assertThat(list, hasSize(1));
        assertThat(list, contains((String)null));
    }
}
