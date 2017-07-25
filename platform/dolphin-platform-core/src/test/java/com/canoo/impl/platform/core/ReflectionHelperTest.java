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
package com.canoo.impl.platform.core;

import com.canoo.dp.impl.platform.core.ReflectionHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;


public class ReflectionHelperTest {


    private List<String> forTypeParameterCheck1;

    private List forTypeParameterCheck2;

    @Test
    public void testIsNumber() {
        Assert.assertTrue(ReflectionHelper.isNumber(Integer.class));
        Assert.assertTrue(ReflectionHelper.isNumber(Integer.TYPE));
        Assert.assertTrue(ReflectionHelper.isNumber(Long.class));
        Assert.assertTrue(ReflectionHelper.isNumber(Long.TYPE));
        Assert.assertTrue(ReflectionHelper.isNumber(Double.class));
        Assert.assertTrue(ReflectionHelper.isNumber(Double.TYPE));
        Assert.assertTrue(ReflectionHelper.isNumber(Float.class));
        Assert.assertTrue(ReflectionHelper.isNumber(Float.TYPE));
        Assert.assertTrue(ReflectionHelper.isNumber(Byte.class));
        Assert.assertTrue(ReflectionHelper.isNumber(Byte.TYPE));
        Assert.assertTrue(ReflectionHelper.isNumber(Short.class));
        Assert.assertTrue(ReflectionHelper.isNumber(Short.TYPE));

        Assert.assertFalse(ReflectionHelper.isNumber(Date.class));
        Assert.assertFalse(ReflectionHelper.isNumber(String.class));
        Assert.assertFalse(ReflectionHelper.isNumber(Object.class));
    }

    @Test
    public void testGetTypeParameter() {
        try {
            Assert.assertEquals(ReflectionHelper.getTypeParameter(ReflectionHelperTest.class.getDeclaredField("forTypeParameterCheck1")), String.class);
        } catch (Exception e) {
            Assert.fail("Generic Type not found", e);
        }

        try {
            Assert.assertEquals(ReflectionHelper.getTypeParameter(ReflectionHelperTest.class.getDeclaredField("forTypeParameterCheck2")), null);
        } catch (Exception e) {
            Assert.fail("Generic Type not found", e);
        }

        try {
            ReflectionHelper.getTypeParameter(null);
            Assert.fail("Null check not working");
        } catch (Exception e) {

        }
    }

}