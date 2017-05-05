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
package org.opendolphin.core.client

import org.junit.Assert
import org.junit.Test

public class ClientPresentationModelTest {

    @Test
    public void testStandardCtor() {
        ClientPresentationModel model = new ClientPresentationModel("x", Collections.emptyList());
        Assert.assertEquals("x", model.getId());
    }

    @Test
    public void testNullIdCtor() {
        ClientPresentationModel model1 = new ClientPresentationModel(Collections.emptyList());
        ClientPresentationModel model2 = new ClientPresentationModel(Collections.emptyList());
        Assert.assertNotEquals(model1.getId(), model2.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadIdCtor() {
        new ClientPresentationModel("1000-AUTO-CLT", Collections.emptyList());
    }

}
