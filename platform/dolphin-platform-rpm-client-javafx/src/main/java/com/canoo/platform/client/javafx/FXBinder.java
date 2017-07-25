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
package com.canoo.platform.client.javafx;

import com.canoo.dp.impl.client.javafx.DefaultDolphinBinder;
import com.canoo.dp.impl.client.javafx.DefaultJavaFXBidirectionalBinder;
import com.canoo.dp.impl.client.javafx.DefaultJavaFXBinder;
import com.canoo.dp.impl.client.javafx.DefaultJavaFXListBinder;
import com.canoo.dp.impl.client.javafx.DoubleDolphinBinder;
import com.canoo.dp.impl.client.javafx.DoubleJavaFXBidirectionalBinder;
import com.canoo.dp.impl.client.javafx.FloatDolphinBinder;
import com.canoo.dp.impl.client.javafx.FloatJavaFXBidirectionalBinder;
import com.canoo.dp.impl.client.javafx.IntegerDolphinBinder;
import com.canoo.dp.impl.client.javafx.IntegerJavaFXBidirectionalBinder;
import com.canoo.dp.impl.client.javafx.LongDolphinBinder;
import com.canoo.dp.impl.client.javafx.LongJavaFXBidirectionalBinder;
import com.canoo.dolphin.mapping.Property;
import com.canoo.platform.client.javafx.binding.DolphinBinder;
import com.canoo.platform.client.javafx.binding.JavaFXBidirectionalBinder;
import com.canoo.platform.client.javafx.binding.JavaFXBinder;
import com.canoo.platform.client.javafx.binding.JavaFXListBinder;
import com.canoo.platform.client.javafx.binding.NumericDolphinBinder;
import com.canoo.platform.client.javafx.binding.NumericJavaFXBidirectionaBinder;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.value.WritableDoubleValue;
import javafx.beans.value.WritableFloatValue;
import javafx.beans.value.WritableIntegerValue;
import javafx.beans.value.WritableLongValue;
import javafx.beans.value.WritableValue;
import javafx.collections.ObservableList;

import static com.canoo.dp.impl.platform.core.Assert.requireNonNull;

/**
 * Utility class to create uniderctional and bidirectional bindings between JavaFX and Dolphin Platform properties.
 */
public final class FXBinder {

    /**
     * private constructor.
     */
    private FXBinder() {
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param list the JavaFX list
     * @param <T> the data type of the list
     * @return a binder that can be used by the fluent API to create binding.
     */
    public static <T> JavaFXListBinder<T> bind(ObservableList<T> list) {
        requireNonNull(list, "list");
        return new DefaultJavaFXListBinder(list);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param writableDoubleValue the javafx property
     * @return a binder that can be used by the fluent API to create binding.
     */
    public static JavaFXBinder<Double> bind(WritableDoubleValue writableDoubleValue) {
        requireNonNull(writableDoubleValue, "writableDoubleValue");
        return new DefaultJavaFXBinder(writableDoubleValue);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param writableDoubleValue the javafx property
     * @return a binder that can be used by the fluent API to create binding.
     */
    public static JavaFXBinder<Float> bind(WritableFloatValue writableDoubleValue) {
        requireNonNull(writableDoubleValue, "writableDoubleValue");
        return new DefaultJavaFXBinder(writableDoubleValue);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param writableDoubleValue the javafx property
     * @return a binder that can be used by the fluent API to create binding.
     */
    public static JavaFXBinder<Integer> bind(WritableIntegerValue writableDoubleValue) {
        requireNonNull(writableDoubleValue, "writableDoubleValue");
        return new DefaultJavaFXBinder(writableDoubleValue);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param writableDoubleValue the javafx property
     * @return a binder that can be used by the fluent API to create binding.
     */
    public static JavaFXBinder<Long> bind(WritableLongValue writableDoubleValue) {
        requireNonNull(writableDoubleValue, "writableDoubleValue");
        return new DefaultJavaFXBinder(writableDoubleValue);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param property the javafx property
     * @returna binder that can be used by the fluent API to create binding.
     */
    public static NumericJavaFXBidirectionaBinder<Double> bind(DoubleProperty property) {
        requireNonNull(property, "property");
        return new DoubleJavaFXBidirectionalBinder(property);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param property the javafx property
     * @return binder that can be used by the fluent API to create binding.
     */
    public static NumericJavaFXBidirectionaBinder<Float> bind(FloatProperty property) {
        requireNonNull(property, "property");
        return new FloatJavaFXBidirectionalBinder(property);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param property the javafx property
     * @return binder that can be used by the fluent API to create binding.
     */
    public static NumericJavaFXBidirectionaBinder<Integer> bind(IntegerProperty property) {
        requireNonNull(property, "property");
        return new IntegerJavaFXBidirectionalBinder(property);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param property the javafx property
     * @return binder that can be used by the fluent API to create binding.
     */
    public static NumericJavaFXBidirectionaBinder<Long> bind(LongProperty property) {
        requireNonNull(property, "property");
        return new LongJavaFXBidirectionalBinder(property);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param writableDoubleValue the javafx property
     * @return binder that can be used by the fluent API to create binding.
     */
    public static <T> JavaFXBinder<T> bind(WritableValue<T> writableDoubleValue) {
        requireNonNull(writableDoubleValue, "writableDoubleValue");
        return new DefaultJavaFXBinder(writableDoubleValue);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param property the javafx property
     * @return binder that can be used by the fluent API to create binding.
     */
    public static <T> JavaFXBidirectionalBinder<T> bind(javafx.beans.property.Property<T> property) {
        requireNonNull(property, "property");
        return new DefaultJavaFXBidirectionalBinder<>(property);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param property the Dolphin Platform property
     * @return binder that can be used by the fluent API to create binding.
     */
    public static <T> DolphinBinder<T> bind(Property<T> property) {
        requireNonNull(property, "property");
        return new DefaultDolphinBinder<>(property);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param property the Dolphin Platform property
     * @return binder that can be used by the fluent API to create binding.
     */
    public static NumericDolphinBinder<Double> bindDouble(Property<Double> property) {
        requireNonNull(property, "property");
        return new DoubleDolphinBinder(property);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param property the Dolphin Platform property
     * @return binder that can be used by the fluent API to create binding.
     */
    public static NumericDolphinBinder<Float> bindFloat(Property<Float> property) {
        requireNonNull(property, "property");
        return new FloatDolphinBinder(property);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param property the Dolphin Platform property
     * @return binder that can be used by the fluent API to create binding.
     */
    public static NumericDolphinBinder<Integer> bindInteger(Property<Integer> property) {
        requireNonNull(property, "property");
        return new IntegerDolphinBinder(property);
    }

    /**
     * Start point of the fluent API to create a binding.
     * @param property the Dolphin Platform property
     * @return binder that can be used by the fluent API to create binding.
     */
    public static NumericDolphinBinder<Long> bindLong(Property<Long> property) {
        requireNonNull(property, "property");
        return new LongDolphinBinder(property);
    }
}
