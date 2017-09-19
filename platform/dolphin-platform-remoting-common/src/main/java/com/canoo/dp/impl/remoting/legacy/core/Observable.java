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
package com.canoo.dp.impl.remoting.legacy.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Observable {

    private final PropertyChangeSupport pcs;

    public Observable() {
        pcs = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null || containsListener(listener, getPropertyChangeListeners())) return;
        pcs.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listener == null || containsListener(listener, getPropertyChangeListeners(propertyName))) return;
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return pcs.getPropertyChangeListeners();
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return pcs.getPropertyChangeListeners(propertyName);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (oldValue == newValue) return;
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    private boolean containsListener(PropertyChangeListener listener, PropertyChangeListener[] listeners) {
        for (PropertyChangeListener subject : listeners) {
            if (subject == listener) return true;
        }
        return false;
    }
}