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
package com.canoo.dolphin.server.impl.gc;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;
import com.canoo.implementation.dolphin.server.gc.GarbageCollector;

@DolphinBean
public class BeanWithProperties {

    private Property<String> stringProperty;

    private Property<Boolean> booleanProperty;

    private Property<Double> doubleProperty;

    private Property<BeanWithProperties> beanProperty;

    private Property<BeanWithLists> listBeanProperty;

    public BeanWithProperties(GarbageCollector garbageCollector) {
        this.stringProperty = new PropertyWithGcSupport<>(garbageCollector);
        this.booleanProperty = new PropertyWithGcSupport<>(garbageCollector);
        this.doubleProperty = new PropertyWithGcSupport<>(garbageCollector);
        this.beanProperty = new PropertyWithGcSupport<>(garbageCollector);
        this.listBeanProperty = new PropertyWithGcSupport<>(garbageCollector);
    }

    public Property<String> stringProperty() {
        return stringProperty;
    }

    public Property<Boolean> booleanProperty() {
        return booleanProperty;
    }

    public Property<Double> doubleProperty() {
        return doubleProperty;
    }

    public Property<BeanWithProperties> beanProperty() {
        return beanProperty;
    }

    public Property<BeanWithLists> listBeanProperty() {
        return listBeanProperty;
    }
}
