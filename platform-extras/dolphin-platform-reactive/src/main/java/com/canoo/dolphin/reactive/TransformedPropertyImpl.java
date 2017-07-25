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
package com.canoo.dolphin.reactive;

import com.canoo.platform.core.functional.Subscription;
import com.canoo.dolphin.impl.AbstractProperty;
import com.canoo.dp.impl.platform.core.Assert;
import rx.functions.Action1;

/**
 * Default implementation for {@link TransformedProperty}
 * @param <T> type of the property
 */
public class TransformedPropertyImpl<T> extends AbstractProperty<T> implements TransformedProperty<T>, Action1<T> {

    private T value;

    private final Subscription subscription;

    public TransformedPropertyImpl(final Subscription subscription) {
        Assert.requireNonNull(subscription, "subscription");
        this.subscription = subscription;
    }

    @Override
    public void set(final T value) {
        throw new RuntimeException("The transformed property is bound to another property!");
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void unsubscribe() {
        subscription.unsubscribe();
    }

    @Override
    public void call(final T t) {
        T oldValue = this.value;
        this.value = t;
        firePropertyChanged(oldValue, value);
    }
}
