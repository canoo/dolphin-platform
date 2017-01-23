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
import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
import com.canoo.dolphin.server.util.SimpleAnnotatedTestModel;
import com.canoo.dolphin.server.util.SimpleTestModel;
import com.canoo.remoting.server.ServerDolphin;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TestFindAll extends AbstractDolphinBasedTest {

    @Test
    public void testWithSimpleModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        SimpleTestModel model1 = manager.create(SimpleTestModel.class);
        SimpleTestModel model2 = manager.create(SimpleTestModel.class);
        SimpleTestModel model3 = manager.create(SimpleTestModel.class);

        manager.create(SimpleAnnotatedTestModel.class);

        List<SimpleTestModel> models = manager.findAll(SimpleTestModel.class);
        assertThat(models, is(Arrays.asList(model1, model2, model3)));
    }
}
