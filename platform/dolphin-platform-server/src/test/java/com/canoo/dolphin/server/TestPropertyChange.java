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
package com.canoo.dolphin.server;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.event.ValueChangeEvent;
import com.canoo.dolphin.event.ValueChangeListener;
import com.canoo.dolphin.mapping.Property;
import com.canoo.dolphin.server.util.*;
import com.canoo.remoting.server.ServerDolphin;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class TestPropertyChange extends AbstractDolphinBasedTest {

    @Test
    public void testWithAnnotatedSimpleModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final SimpleAnnotatedTestModel model = manager.create(SimpleAnnotatedTestModel.class);

        final ListerResults<String> results = new ListerResults<>();
        ValueChangeListener<String> myListener = new ValueChangeListener<String>() {
            @SuppressWarnings("unchecked")
            @Override
            public void valueChanged(ValueChangeEvent<? extends String> evt) {
                assertThat((Property<String>)evt.getSource(), is(model.getMyProperty()));
                results.newValue = evt.getNewValue();
                results.oldValue = evt.getOldValue();
                results.listenerCalls++;
            }
        };

        final Subscription subscription = model.getMyProperty().onChanged(myListener);
        assertThat(results.listenerCalls, is(0));
        assertThat(results.newValue, nullValue());
        assertThat(results.oldValue, nullValue());

        model.getMyProperty().set("Hallo Property");
        assertThat(results.listenerCalls, is(1));
        assertThat(results.newValue, is("Hallo Property"));
        assertThat(results.oldValue, nullValue());

        results.listenerCalls = 0;
        model.getMyProperty().set("Hallo Property2");
        assertThat(results.listenerCalls, is(1));
        assertThat(results.newValue, is("Hallo Property2"));
        assertThat(results.oldValue, is("Hallo Property"));

        results.listenerCalls = 0;
        subscription.unsubscribe();
        model.getMyProperty().set("Hallo Property3");
        assertThat(results.listenerCalls, is(0));
        assertThat(results.newValue, is("Hallo Property2"));
        assertThat(results.oldValue, is("Hallo Property"));
    }

    @Test
    public void testWithSimpleModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final SimpleTestModel model = manager.create(SimpleTestModel.class);

        final ListerResults<String> results = new ListerResults<>();
        ValueChangeListener<String> myListener = new ValueChangeListener<String>() {
            @SuppressWarnings("unchecked")
            @Override
            public void valueChanged(ValueChangeEvent<? extends String> evt) {
                assertThat((Property<String>) evt.getSource(), is(model.getTextProperty()));
                results.newValue = evt.getNewValue();
                results.oldValue = evt.getOldValue();
                results.listenerCalls++;
            }
        };

        final Subscription subscription = model.getTextProperty().onChanged(myListener);
        assertThat(results.listenerCalls, is(0));
        assertThat(results.newValue, nullValue());
        assertThat(results.oldValue, nullValue());

        model.getTextProperty().set("Hallo Property");
        assertThat(results.listenerCalls, is(1));
        assertThat(results.newValue, is("Hallo Property"));
        assertThat(results.oldValue, nullValue());

        results.listenerCalls = 0;
        model.getTextProperty().set("Hallo Property2");
        assertThat(results.listenerCalls, is(1));
        assertThat(results.newValue, is("Hallo Property2"));
        assertThat(results.oldValue, is("Hallo Property"));

        results.listenerCalls = 0;
        subscription.unsubscribe();
        model.getTextProperty().set("Hallo Property3");
        assertThat(results.listenerCalls, is(0));
        assertThat(results.newValue, is("Hallo Property2"));
        assertThat(results.oldValue, is("Hallo Property"));
    }


    @Test
    public void testWithSingleReferenceModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final SimpleTestModel ref1 = manager.create(SimpleTestModel.class);
        final SimpleTestModel ref2 = manager.create(SimpleTestModel.class);
        final SimpleTestModel ref3 = manager.create(SimpleTestModel.class);

        final SingleReferenceModel model = manager.create(SingleReferenceModel.class);

        final ListerResults<SimpleTestModel> results = new ListerResults<>();
        final ValueChangeListener<SimpleTestModel> myListener = new ValueChangeListener<SimpleTestModel>() {
            @SuppressWarnings("unchecked")
            @Override
            public void valueChanged(ValueChangeEvent<? extends SimpleTestModel> evt) {
                assertThat((Property<SimpleTestModel>) evt.getSource(), is(model.getReferenceProperty()));
                results.newValue = evt.getNewValue();
                results.oldValue = evt.getOldValue();
                results.listenerCalls++;
            }
        };

        final Subscription subscription = model.getReferenceProperty().onChanged(myListener);
        assertThat(results.listenerCalls, is(0));
        assertThat(results.newValue, nullValue());
        assertThat(results.oldValue, nullValue());

        model.getReferenceProperty().set(ref1);
        assertThat(results.listenerCalls, is(1));
        assertThat(results.newValue, is(ref1));
        assertThat(results.oldValue, nullValue());

        results.listenerCalls = 0;
        model.getReferenceProperty().set(ref2);
        assertThat(results.listenerCalls, is(1));
        assertThat(results.newValue, is(ref2));
        assertThat(results.oldValue, is(ref1));

        results.listenerCalls = 0;
        subscription.unsubscribe();
        model.getReferenceProperty().set(ref3);
        assertThat(results.listenerCalls, is(0));
        assertThat(results.newValue, is(ref2));
        assertThat(results.oldValue, is(ref1));
    }

    @Test
    public void testWithInheritedModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final ChildModel model = manager.create(ChildModel.class);

        final ListerResults<String> childResults = new ListerResults<>();
        ValueChangeListener<String> childListener = new ValueChangeListener<String>() {
            @SuppressWarnings("unchecked")
            @Override
            public void valueChanged(ValueChangeEvent<? extends String> evt) {
                assertThat((Property<String>) evt.getSource(), is(model.getChildProperty()));
                childResults.newValue = evt.getNewValue();
                childResults.oldValue = evt.getOldValue();
                childResults.listenerCalls++;
            }
        };
        final ListerResults<String> parentResults = new ListerResults<>();
        ValueChangeListener<String> parentListener = new ValueChangeListener<String>() {
            @SuppressWarnings("unchecked")
            @Override
            public void valueChanged(ValueChangeEvent<? extends String> evt) {
                assertThat((Property<String>) evt.getSource(), is(model.getParentProperty()));
                parentResults.newValue = evt.getNewValue();
                parentResults.oldValue = evt.getOldValue();
                parentResults.listenerCalls++;
            }
        };

        model.getChildProperty().onChanged(childListener);
        model.getParentProperty().onChanged(parentListener);
        assertThat(childResults.listenerCalls, is(0));
        assertThat(childResults.newValue, nullValue());
        assertThat(childResults.oldValue, nullValue());
        assertThat(parentResults.listenerCalls, is(0));
        assertThat(parentResults.newValue, nullValue());
        assertThat(parentResults.oldValue, nullValue());

        model.getChildProperty().set("Hallo Property");
        assertThat(childResults.listenerCalls, is(1));
        assertThat(childResults.newValue, is("Hallo Property"));
        assertThat(childResults.oldValue, nullValue());
        assertThat(parentResults.listenerCalls, is(0));
        assertThat(parentResults.newValue, nullValue());
        assertThat(parentResults.oldValue, nullValue());

        childResults.listenerCalls = 0;
        childResults.newValue = null;
        childResults.oldValue = null;
        model.getParentProperty().set("Hallo Property2");
        assertThat(childResults.listenerCalls, is(0));
        assertThat(childResults.newValue, nullValue());
        assertThat(childResults.oldValue, nullValue());
        assertThat(parentResults.listenerCalls, is(1));
        assertThat(parentResults.newValue, is("Hallo Property2"));
        assertThat(parentResults.oldValue, nullValue());
    }

    private static class ListerResults<T> {
        public T newValue;
        public T oldValue;
        public int listenerCalls;
    }
}
