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
import com.canoo.dolphin.client.util.SimpleAnnotatedTestModel;
import com.canoo.dolphin.client.util.SimpleTestModel;
import mockit.Mocked;
import com.canoo.remoting.client.ClientDolphin;
import com.canoo.remoting.client.ClientPresentationModel;
import com.canoo.remoting.client.communication.ClientConnector;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class TestDeleteAll extends AbstractDolphinBasedTest {

    @Test
    public void testWithSimpleModel(@Mocked ClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);


        SimpleTestModel model1 = manager.create(SimpleTestModel.class);
        SimpleTestModel model2 = manager.create(SimpleTestModel.class);
        SimpleTestModel model3 = manager.create(SimpleTestModel.class);

        SimpleAnnotatedTestModel wrongModel = manager.create(SimpleAnnotatedTestModel.class);

        manager.removeAll(SimpleTestModel.class);
        assertThat(manager.isManaged(model1), is(false));
        assertThat(manager.isManaged(model2), is(false));
        assertThat(manager.isManaged(model3), is(false));
        assertThat(manager.isManaged(wrongModel), is(true));

        List<ClientPresentationModel> testModels = dolphin.findAllPresentationModelsByType("com.canoo.dolphin.client.util.SimpleTestModel");
        assertThat(testModels, hasSize(0));

    }
}

