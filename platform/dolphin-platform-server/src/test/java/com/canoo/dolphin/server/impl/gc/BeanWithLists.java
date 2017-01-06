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

import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.implementation.dolphin.server.gc.GarbageCollector;

@DolphinBean
public class BeanWithLists {

    private ObservableList<String> stringList;

    private ObservableList<Boolean> booleanList;

    private ObservableList<Double> doubleList;

    private ObservableList<BeanWithLists> beansList;

    private ObservableList<BeanWithProperties> beansList2;

    public BeanWithLists(GarbageCollector garbageCollector) {
        stringList = new ObservableListWithGcSupport(garbageCollector);
        booleanList = new ObservableListWithGcSupport(garbageCollector);
        doubleList = new ObservableListWithGcSupport(garbageCollector);
        beansList = new ObservableListWithGcSupport(garbageCollector);
        beansList2 = new ObservableListWithGcSupport(garbageCollector);
    }

    public ObservableList<String> getStringList() {
        return stringList;
    }

    public ObservableList<Boolean> getBooleanList() {
        return booleanList;
    }

    public ObservableList<Double> getDoubleList() {
        return doubleList;
    }

    public ObservableList<BeanWithLists> getBeansList() {
        return beansList;
    }

    public ObservableList<BeanWithProperties> getBeansList2() {
        return beansList2;
    }
}
