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
package com.canoo.platform.client.javafx.binding;

import com.canoo.platform.client.javafx.FXBinder;
import com.canoo.dolphin.collections.ListChangeEvent;
import com.canoo.dolphin.mapping.Property;
import com.canoo.dp.impl.platform.core.Assert;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * A util class that can be used to create JavaFX properties and lists as wrapper around Dolphin Platform properties and lists.
 */
public class FXWrapper {


    /**
     * private constructor
     */
    private FXWrapper() {
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.DoubleProperty} as a wrapper for a dolphin platform property
     *
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static DoubleProperty wrapDoubleProperty(final Property<Double> dolphinProperty) {
        Assert.requireNonNull(dolphinProperty, "dolphinProperty");
        final DoubleProperty property = new SimpleDoubleProperty();
        FXBinder.bind(property).bidirectionalToNumeric(dolphinProperty);
        return property;
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.FloatProperty} as a wrapper for a dolphin platform property
     *
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static FloatProperty wrapFloatProperty(final Property<Float> dolphinProperty) {
        Assert.requireNonNull(dolphinProperty, "dolphinProperty");
        final FloatProperty property = new SimpleFloatProperty();
        FXBinder.bind(property).bidirectionalToNumeric(dolphinProperty);
        return property;
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.IntegerProperty} as a wrapper for a dolphin platform property
     *
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static IntegerProperty wrapIntProperty(final Property<Integer> dolphinProperty) {
        Assert.requireNonNull(dolphinProperty, "dolphinProperty");
        final IntegerProperty property = new SimpleIntegerProperty();
        FXBinder.bind(property).bidirectionalToNumeric(dolphinProperty);
        return property;
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.LongProperty} as a wrapper for a dolphin platform property
     *
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static LongProperty wrapLongProperty(final Property<Long> dolphinProperty) {
        Assert.requireNonNull(dolphinProperty, "dolphinProperty");
        final LongProperty property = new SimpleLongProperty();
        FXBinder.bind(property).bidirectionalToNumeric(dolphinProperty);
        return property;
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.BooleanProperty} as a wrapper for a dolphin platform property
     *
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static BooleanProperty wrapBooleanProperty(final Property<Boolean> dolphinProperty) {
        Assert.requireNonNull(dolphinProperty, "dolphinProperty");
        final BooleanProperty property = new SimpleBooleanProperty();
        FXBinder.bind(property).bidirectionalTo(dolphinProperty);
        return property;
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.StringProperty} as a wrapper for a dolphin platform property
     *
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static StringProperty wrapStringProperty(final Property<String> dolphinProperty) {
        Assert.requireNonNull(dolphinProperty, "dolphinProperty");
        StringProperty property = new SimpleStringProperty();
        FXBinder.bind(property).bidirectionalTo(dolphinProperty);
        return property;
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.ObjectProperty} as a wrapper for a dolphin platform property
     *
     * @param dolphinProperty the dolphin platform property
     * @return the JavaFX property
     */
    public static <T> ObjectProperty<T> wrapObjectProperty(final Property<T> dolphinProperty) {
        Assert.requireNonNull(dolphinProperty, "dolphinProperty");
        final ObjectProperty<T> property = new SimpleObjectProperty<>();
        FXBinder.bind(property).bidirectionalTo(dolphinProperty);
        return property;
    }

    /**
     * Create a JavaFX {@link javafx.collections.ObservableList} wrapper for a dolphin platform list
     *
     * @param dolphinList the dolphin platform list
     * @param <T>         type of the list content
     * @return the JavaFX list
     */
    public static <T> ObservableList<T> wrapList(com.canoo.dolphin.collections.ObservableList<T> dolphinList) {
        Assert.requireNonNull(dolphinList, "dolphinList");
        final ObservableList<T> list = FXCollections.observableArrayList(dolphinList);

        list.addListener((ListChangeListener<T>) c -> {
            if (listenToFx) {
                listenToDolphin = false;
                while (c.next()) {
                    if (c.wasAdded() || c.wasRemoved() || c.wasReplaced()) {
                        for (T removed : c.getRemoved()) {
                            dolphinList.remove(removed);
                        }
                        for (T added : c.getAddedSubList()) {
                            dolphinList.add(list.indexOf(added), added);
                        }
                    }
                }
                listenToDolphin = true;
            }
        });

        dolphinList.onChanged(e -> {
            if (listenToDolphin) {
                listenToFx = false;
                for (ListChangeEvent.Change<? extends T> c : e.getChanges()) {
                    if (c.isAdded()) {
                        for (int i = c.getFrom(); i < c.getTo(); i++) {
                            list.add(i, dolphinList.get(i));
                        }
                    } else if (c.isRemoved()) {
                        final int index = c.getFrom();
                        list.remove(index, index + c.getRemovedElements().size());
                    }
                }
                listenToFx = true;
            }
        });

        return list;
    }

    //TODO: HACK
    private static boolean listenToFx = true;

    //TODO: HACK
    private static boolean listenToDolphin = true;
}
