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
package com.canoo.dolphin.client;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.client.util.AbstractDolphinBasedTest;
import com.canoo.dolphin.client.util.ChildModel;
import com.canoo.dolphin.client.util.ListReferenceModel;
import com.canoo.dolphin.client.util.SimpleAnnotatedTestModel;
import com.canoo.dolphin.client.util.SimpleTestModel;
import com.canoo.dolphin.client.util.SingleReferenceModel;
import com.canoo.dolphin.impl.BeanDefinitionException;
import mockit.Mocked;
import com.canoo.remoting.client.ClientDolphin;
import com.canoo.remoting.client.ClientPresentationModel;
import com.canoo.remoting.client.communication.ClientConnector;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class TestModelDeletion extends AbstractDolphinBasedTest {

    @Test
    public void testWithAnnotatedSimpleModel(@Mocked ClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        SimpleAnnotatedTestModel model = manager.create(SimpleAnnotatedTestModel.class);

        manager.remove(model);

        List<ClientPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType("simple_test_model");
        assertThat(dolphinModels, empty());

        Collection<ClientPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));

        assertThat(manager.isManaged(model), is(false));
    }

    @Test
    public void testWithSimpleModel(@Mocked ClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        SimpleTestModel model = manager.create(SimpleTestModel.class);

        manager.remove(model);

        List<ClientPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(SimpleTestModel.class.getName());
        assertThat(dolphinModels, empty());

        Collection<ClientPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));

        assertThat(manager.isManaged(model), is(false));
    }

    @Test(expectedExceptions = BeanDefinitionException.class)
    public void testWithWrongModelType(@Mocked ClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        manager.remove("I'm a String");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testWithNull(@Mocked ClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        manager.remove(null);
    }

    @Test
    public void testWithSingleReferenceModel(@Mocked ClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        SingleReferenceModel model = manager.create(SingleReferenceModel.class);

        manager.remove(model);

        List<ClientPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(SingleReferenceModel.class.getName());
        assertThat(dolphinModels, empty());

        Collection<ClientPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));

        assertThat(manager.isManaged(model), is(false));
    }

    @Test
    public void testWithListReferenceModel(@Mocked ClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        ListReferenceModel model = manager.create(ListReferenceModel.class);

        manager.remove(model);

        List<ClientPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName());
        assertThat(dolphinModels, empty());

        Collection<ClientPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));    //Dolphin Class Repository wurde bereits angelegt

        assertThat(manager.isManaged(model), is(false));
    }

    @Test
    public void testWithInheritedModel(@Mocked ClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        ChildModel model = manager.create(ChildModel.class);

        manager.remove(model);

        List<ClientPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(ChildModel.class.getName());
        assertThat(dolphinModels, empty());

        Collection<ClientPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));

        assertThat(manager.isManaged(model), is(false));
    }


}

