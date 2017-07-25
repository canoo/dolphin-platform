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
package com.canoo.dp.impl.client.javafx;

import com.canoo.dolphin.binding.Binding;
import com.canoo.platform.client.javafx.binding.JavaFXListBinder;
import com.canoo.dolphin.collections.ListChangeEvent;
import com.canoo.dolphin.collections.ObservableList;
import com.canoo.platform.core.functional.Subscription;
import com.canoo.dp.impl.platform.core.Assert;
import javafx.collections.ListChangeListener;

import java.util.IdentityHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DefaultJavaFXListBinder<S> implements JavaFXListBinder<S> {

    private javafx.collections.ObservableList<S> list;

    private static IdentityHashMap<javafx.collections.ObservableList, javafx.collections.ObservableList> boundLists = new IdentityHashMap<>();

    public DefaultJavaFXListBinder(javafx.collections.ObservableList<S> list) {
        Assert.requireNonNull(list, "list");
        this.list = list;
    }

    @Override
    public <T> Binding to(ObservableList<T> dolphinList, Function<? super T, ? extends S> converter) {
        Assert.requireNonNull(dolphinList, "dolphinList");
        Assert.requireNonNull(converter, "converter");
        if(boundLists.containsKey(list)) {
            throw new UnsupportedOperationException("A JavaFX list can only be bound to one Dolphin Platform list!");
        }

        boundLists.put(list, list);
        final InternalListChangeListener<T> listChangeListener = new InternalListChangeListener<>(converter);
        final Subscription subscription = dolphinList.onChanged(listChangeListener);

        list.setAll(dolphinList.stream().map(converter).collect(Collectors.toList()));

        ListChangeListener<S> readOnlyListener = c -> {
            if (!listChangeListener.onChange) {
                throw new UnsupportedOperationException("A JavaFX list that is bound to a dolphin list can only be modified by the binding!");
            }
        };
        list.addListener(readOnlyListener);


        return () -> {
            subscription.unsubscribe();
            list.removeListener(readOnlyListener);
            boundLists.remove(list);
        };
    }

    @Override
    public <T> Binding bidirectionalTo(ObservableList<T> dolphinList, Function<? super T, ? extends S> converter, Function<? super S, ? extends T> backConverter) {
        Assert.requireNonNull(dolphinList, "dolphinList");
        Assert.requireNonNull(converter, "converter");
        Assert.requireNonNull(backConverter, "backConverter");
        if(boundLists.containsKey(list)) {
            throw new IllegalStateException("A JavaFX list can only be bound to one Dolphin Platform list!");
        }

        final InternalBidirectionalListChangeListener<T> listChangeListener = new InternalBidirectionalListChangeListener<>(dolphinList, converter, backConverter);
        final Subscription subscription = dolphinList.onChanged(listChangeListener);

        list.setAll(dolphinList.stream().map(converter).collect(Collectors.toList()));

        list.addListener(listChangeListener);

        return () -> {
            subscription.unsubscribe();
            list.removeListener(listChangeListener);
        };
    }

    private class InternalListChangeListener<T> implements com.canoo.dolphin.collections.ListChangeListener<T> {

        private final Function<? super T, ? extends S> converter;

        protected boolean onChange;

        private InternalListChangeListener(Function<? super T, ? extends S> converter) {
            this.converter = converter;
            onChange = false;
        }

        @Override
        public void listChanged(ListChangeEvent<? extends T> e) {
            if (onChange) {
                return;
            }

            onChange = true;
            try {
                for (ListChangeEvent.Change<? extends T> c : e.getChanges()) {
                    final int index = c.getFrom();
                    if (c.isRemoved() || c.isReplaced()) {
                        list.subList(index, index + c.getRemovedElements().size()).clear();
                    }
                    if (c.isAdded() || c.isReplaced()) {
                        list.addAll(index,
                                e.getSource()
                                        .subList(index, c.getTo()).stream()
                                        .map(converter)
                                        .collect(Collectors.toList()));
                    }
                }
            } finally {
                onChange = false;
            }
        }
    }

    private class InternalBidirectionalListChangeListener<T> extends InternalListChangeListener<T> implements ListChangeListener<S> {

        private final ObservableList<T> dolphinList;
        private final Function<? super S, ? extends T> backConverter;

        private InternalBidirectionalListChangeListener(ObservableList<T> dolphinList,
                                                        Function<? super T, ? extends S> converter,
                                                        Function<? super S, ? extends T> backConverter) {
            super(converter);
            this.dolphinList = dolphinList;
            this.backConverter = backConverter;
        }

        @Override
        public void onChanged(Change<? extends S> change) {
            if (onChange) {
                return;
            }

            onChange = true;
            try {
                while (change.next()) {
                    // TODO: Replace with subList() operation once implemented
                    final int index = change.getFrom();
                    if (change.wasRemoved() || change.wasReplaced()) {
                        for (int i = 0; i < change.getRemovedSize(); i++) {
                            dolphinList.remove(index);
                        }
                    }
                    if (change.wasAdded() || change.wasReplaced()) {
                        dolphinList.addAll(index,
                                change.getAddedSubList().stream()
                                        .map(backConverter)
                                        .collect(Collectors.toList()));
                    }
                }
            } finally {
                onChange = false;
            }
        }
    }
}
