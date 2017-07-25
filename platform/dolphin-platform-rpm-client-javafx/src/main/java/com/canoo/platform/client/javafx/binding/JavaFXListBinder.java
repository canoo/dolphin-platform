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

import com.canoo.dolphin.binding.Binding;
import com.canoo.dolphin.collections.ObservableList;

import java.util.function.Function;

import static com.canoo.dp.impl.platform.core.Assert.*;

public interface JavaFXListBinder<S> {

    default Binding to(ObservableList<? extends S> dolphinList) {
        requireNonNull(dolphinList, "dolphinList");
        return to(dolphinList, Function.<S>identity());
    }

    <T> Binding to(ObservableList<T> dolphinList, Function<? super T, ? extends S> converter);

    default Binding bidirectionalTo(ObservableList<S> dolphinList) {
        requireNonNull(dolphinList, "dolphinList");
        return bidirectionalTo(dolphinList, Function.identity(), Function.identity());
    }

    <T> Binding bidirectionalTo(ObservableList<T> dolphinList, Function<? super T, ? extends S> converter, Function<? super S, ? extends T> backConverter);

}
