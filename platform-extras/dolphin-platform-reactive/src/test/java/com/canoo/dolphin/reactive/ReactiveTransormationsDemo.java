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
package com.canoo.dolphin.reactive;

import com.canoo.dolphin.event.ValueChangeEvent;
import com.canoo.dolphin.event.ValueChangeListener;
import com.canoo.implementation.dolphin.MockedProperty;
import com.canoo.dolphin.mapping.Property;
import rx.functions.Func1;

import java.util.concurrent.TimeUnit;

/**
 * Since we do not have tests for the module this class can be started to see the functionallity.
 */
public class ReactiveTransormationsDemo {

    public static void main(String... args) throws Exception{

        System.out.println("throttleLast");

        final Property<String> property = new MockedProperty<>();
        property.set("");
        final Property<String> debouncedProperty = ReactiveTransormations.throttleLast(property, 200, TimeUnit.MILLISECONDS);
        debouncedProperty.onChanged(new ValueChangeListener<String>() {
            @Override
            public void valueChanged(ValueChangeEvent<? extends String> evt) {
                System.out.println(debouncedProperty.get().length());
            }
        });

        for(int i = 0; i < 50; i++) {
            property.set(property.get() + "A");
            Thread.sleep(20);
        }
        Thread.sleep(2000);

        System.out.println("filter");

        final Property<String> property2 = new MockedProperty<>();
        property2.set("");
        final Property<String> debouncedProperty2 = ReactiveTransormations.filter(property2, new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                return s.length() % 2 == 0;
            }
        });
        debouncedProperty2.onChanged(new ValueChangeListener<String>() {
            @Override
            public void valueChanged(ValueChangeEvent<? extends String> evt) {
                System.out.println(debouncedProperty2.get().length());
            }
        });

        for(int i = 0; i < 50; i++) {
            property2.set(property2.get() + "A");
            Thread.sleep(20);
        }
        Thread.sleep(500);
    }

}
