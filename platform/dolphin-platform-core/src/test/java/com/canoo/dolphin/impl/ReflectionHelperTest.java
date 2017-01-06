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
package com.canoo.dolphin.impl;

import com.canoo.implementation.dolphin.collections.ObservableArrayList;
import com.canoo.implementation.dolphin.MockedProperty;
import com.canoo.implementation.dolphin.ReflectionHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.annotation.RetentionPolicy;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;


public class ReflectionHelperTest {

    @Test
    public void testIsAllowedForUnmanaged() {
        //Basics
        assertTrue(ReflectionHelper.isAllowedForUnmanaged(Double.class));
        assertTrue(ReflectionHelper.isAllowedForUnmanaged(Double.TYPE));
        assertTrue(ReflectionHelper.isAllowedForUnmanaged(Long.class));
        assertTrue(ReflectionHelper.isAllowedForUnmanaged(Long.TYPE));
        assertTrue(ReflectionHelper.isAllowedForUnmanaged(Float.class));
        assertTrue(ReflectionHelper.isAllowedForUnmanaged(Float.TYPE));
        assertTrue(ReflectionHelper.isAllowedForUnmanaged(Integer.class));
        assertTrue(ReflectionHelper.isAllowedForUnmanaged(Integer.TYPE));
        assertTrue(ReflectionHelper.isAllowedForUnmanaged(Boolean.class));
        assertTrue(ReflectionHelper.isAllowedForUnmanaged(Boolean.TYPE));
        assertTrue(ReflectionHelper.isAllowedForUnmanaged(String.class));

        //Enum
        assertTrue(ReflectionHelper.isAllowedForUnmanaged(RetentionPolicy.class));

        //Property
        assertTrue(ReflectionHelper.isAllowedForUnmanaged(MockedProperty.class));

        //Other
        assertFalse(ReflectionHelper.isAllowedForUnmanaged(Date.class));
        assertFalse(ReflectionHelper.isAllowedForUnmanaged(LocalDateTime.class));
        assertFalse(ReflectionHelper.isAllowedForUnmanaged(Locale.class));

        try {
            ReflectionHelper.isAllowedForUnmanaged(null);
            Assert.fail("Null check not working");
        } catch (Exception e) {

        }
    }

    @Test
    public void testIsEnumType() throws Exception {
        assertTrue(ReflectionHelper.isEnumType(DataType.class));

        try {
            ReflectionHelper.isEnumType(null);
            Assert.fail("Null check not working");
        } catch (Exception e) {

        }
    }

    @Test
    public void testIsProperty() throws Exception {
        assertTrue(ReflectionHelper.isProperty(MockedProperty.class));

        try {
            ReflectionHelper.isProperty((Class<?>) null);
            Assert.fail("Null check not working");
        } catch (Exception e) {

        }
    }

    @Test
    public void testBasicType() throws Exception {
        assertTrue(ReflectionHelper.isBasicType(String.class));
        assertTrue(ReflectionHelper.isBasicType(Number.class));
        assertTrue(ReflectionHelper.isBasicType(Long.class));
        assertTrue(ReflectionHelper.isBasicType(Integer.class));
        assertTrue(ReflectionHelper.isBasicType(Double.class));
        assertTrue(ReflectionHelper.isBasicType(Boolean.class));
        assertTrue(ReflectionHelper.isBasicType(Byte.class));
        assertTrue(ReflectionHelper.isBasicType(Short.class));
        assertTrue(ReflectionHelper.isBasicType(BigDecimal.class));
        assertTrue(ReflectionHelper.isBasicType(BigInteger.class));
        assertTrue(ReflectionHelper.isBasicType(Long.TYPE));
        assertTrue(ReflectionHelper.isBasicType(Integer.TYPE));
        assertTrue(ReflectionHelper.isBasicType(Double.TYPE));
        assertTrue(ReflectionHelper.isBasicType(Boolean.TYPE));
        assertTrue(ReflectionHelper.isBasicType(Byte.TYPE));
        assertTrue(ReflectionHelper.isBasicType(Short.TYPE));

        assertFalse(ReflectionHelper.isBasicType(ReflectionHelperTest.class));
        assertFalse(ReflectionHelper.isBasicType(DataType.class));
        assertFalse(ReflectionHelper.isBasicType(UUID.class));

        try {
            ReflectionHelper.isBasicType(null);
            Assert.fail("Null check not working");
        } catch (Exception e) {

        }
    }

    private List<String> forTypeParameterCheck1;

    private List forTypeParameterCheck2;

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

    @Test
    public void testIsObservableList() {
        assertTrue(ReflectionHelper.isObservableList(ObservableArrayList.class));
        assertFalse(ReflectionHelper.isObservableList(LinkedList.class));

        try {
            ReflectionHelper.isObservableList(null);
            Assert.fail("Null check not working");
        } catch (Exception e) {

        }
    }
}