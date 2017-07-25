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
package com.canoo.impl.platform.client;

import com.canoo.platform.core.functional.Subscription;
import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.core.functional.Callback;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ForwardableCallback<T> implements Callback<T> {

    private List<Callback<T>> registeredCallbacks = new CopyOnWriteArrayList<>();

    public Subscription register(final Callback<T> callback) {
        Assert.requireNonNull(callback, "callback");
        registeredCallbacks.add(callback);
        return new Subscription() {
            @Override
            public void unsubscribe() {
                registeredCallbacks.remove(callback);
            }
        };
    }

    @Override
    public void call(T t) {
        for(Callback<T> callback : registeredCallbacks) {
            callback.call(t);
        }
    }
}
