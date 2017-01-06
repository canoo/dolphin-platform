package com.canoo.implementation.dolphin;

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.event.ValueChangeEvent;
import com.canoo.dolphin.event.ValueChangeListener;
import com.canoo.dolphin.mapping.Property;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractProperty<T> implements Property<T> {

    private final List<ValueChangeListener<? super T>> listeners = new CopyOnWriteArrayList<>();

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
