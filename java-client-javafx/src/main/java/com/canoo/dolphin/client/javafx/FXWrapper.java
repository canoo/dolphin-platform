/**
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
package com.canoo.dolphin.client.javafx;

import com.canoo.dolphin.collections.ListChangeEvent;
import com.canoo.dolphin.mapping.Property;
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
        final ObjectProperty<T> property = new SimpleObjectProperty<>();
        FXBinder.bind(property).bidirectionalTo(dolphinProperty);
        return property;
    }

    /**
     * Create a JavaFX {@link javafx.beans.property.ObjectProperty} as a wrapper for a dolphin platform property.
     * A converter is used to convert between the 2 properties.
     * @param dolphinProperty the Dolphin Platform property
     * @param converter the converter
     * @param <T> type of the JavaFX property
     * @param <U> type of the Dolphin Platform property
     * @return the JavaFX property
     */
    public static <T, U> ObjectProperty<T> wrapObjectProperty(final Property<U> dolphinProperty, BidirectionalConverter<U, T> converter) {
        final ObjectProperty<T> property = new SimpleObjectProperty<>();
        FXBinder.bind(property).bidirectionalTo(dolphinProperty, converter);
        return property;
    }

    /**
     * Create a JavaFX (Number) {@link javafx.beans.property.ObjectProperty} as a wrapper for a dolphin platform (Double) property.
     * @param dolphinProperty the Dolphin Platform property
     * @return the JavaFX property
     */
    public static javafx.beans.property.Property<Number> wrapDoubleInNumberProperty(final Property<Double> dolphinProperty) {
        final javafx.beans.property.Property<Number> property = new SimpleDoubleProperty();
        FXBinder.bind(property).bidirectionalTo(dolphinProperty, new BidirectionalConverter<Double, Number>() {
            @Override
            public Double convertBack(Number value) {
                if(value == null) {
                    return null;
                }
                return value.doubleValue();
            }

            @Override
            public Number convert(Double value) {
                return value;
            }
        });
        if(dolphinProperty.get() == null) {
            dolphinProperty.set(property.getValue().doubleValue());
        }
        return property;
    }

    /**
     * Create a JavaFX (Number) {@link javafx.beans.property.ObjectProperty} as a wrapper for a dolphin platform (Integer) property.
     * @param dolphinProperty the Dolphin Platform property
     * @return the JavaFX property
     */
    public static javafx.beans.property.Property<Number> wrapIntegerInNumberProperty(final Property<Integer> dolphinProperty) {
        final javafx.beans.property.Property<Number> property = new SimpleIntegerProperty();
        FXBinder.bind(property).bidirectionalTo(dolphinProperty, new BidirectionalConverter<Integer, Number>() {
            @Override
            public Integer convertBack(Number value) {
                if(value == null) {
                    return null;
                }
                return value.intValue();
            }

            @Override
            public Number convert(Integer value) {
                return value;
            }
        });
        if(dolphinProperty.get() == null) {
            dolphinProperty.set(property.getValue().intValue());
        }
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
