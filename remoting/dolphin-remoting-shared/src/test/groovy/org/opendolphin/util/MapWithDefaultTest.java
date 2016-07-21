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
package org.opendolphin.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class MapWithDefaultTest {
    @Test
    public void valueShouldBeGenerated() {
        // given:
        Map<Integer, Integer> map = MapWithDefault.newInstance(new HashMap<>(), k -> k * 2);

        // when:
        map.put(1,3);

        // then:
        assertThat(map.get(1), equalTo(3));
        assertThat(map.get(2), equalTo(4));
        assertThat(map.size(), equalTo(2));
    }
}
