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
package com.canoo.common;

import com.canoo.common.StringUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

public class StringUtilTest {

        @Test
        public void testIsBlank() {
        Assert.assertTrue(StringUtil.isBlank(null));
        Assert.assertTrue(StringUtil.isBlank(""));
        Assert.assertTrue(StringUtil.isBlank(" "));
        Assert.assertTrue(StringUtil.isBlank("\t"));
        Assert.assertTrue(StringUtil.isBlank(" \t\n"));
        Assert.assertFalse(StringUtil.isBlank("a"));
        Assert.assertFalse(StringUtil.isBlank("."));
    }

}
