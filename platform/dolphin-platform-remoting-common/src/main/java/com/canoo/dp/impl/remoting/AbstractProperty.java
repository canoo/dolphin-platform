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
package com.canoo.dp.impl.remoting;

import com.canoo.platform.core.functional.Subscription;
import com.canoo.platform.remoting.ValueChangeEvent;
import com.canoo.platform.remoting.ValueChangeListener;
import com.canoo.platform.remoting.Property;
import org.apiguardian.api.API;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public abstract class AbstractProperty<T> implements Property<T> {

    private final List<ValueChangeListener<? super T>> listeners = new CopyOnWriteArrayList<>();

    @Override
    public Optional<T> value() {
        return Optional.ofNullable(get());
    }

    @Override
    public Subscription onChanged(final ValueChangeListener<? super T> listener) {
        listeners.add(listener);
        return new Subscription() {
            @Override
            public void unsubscribe() {
                listeners.remove(listener);
            }
        };
    }

    protected void firePropertyChanged(final T oldValue, final T newValue) {
        final ValueChangeEvent<T> event = new ValueChangeEvent<T>() {
            @Override
            public Property<T> getSource() {
                return AbstractProperty.this;
            }

            @Override
            public T getOldValue() {
                return oldValue;
            }

            @Override
            public T getNewValue() {
                return newValue;
            }
        };
        notifyInternalListeners(event);
        notifyExternalListeners(event);
    }

    protected void notifyExternalListeners(ValueChangeEvent<T> event) {
        for(ValueChangeListener<? super T> listener : listeners) {
            listener.valueChanged(event);
        }
    }

    protected void notifyInternalListeners(ValueChangeEvent<T> event) {
    }

    public String toString() {
        return "Dolphin " + getClass().getSimpleName() + "[value: " + get() + "]";
    }

}
