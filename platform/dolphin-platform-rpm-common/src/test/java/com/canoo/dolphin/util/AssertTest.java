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
package com.canoo.dolphin.util;

import com.canoo.dp.impl.platform.core.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;

public class AssertTest {

    @Test
    public void testRequireNonNull() {
        Assert.requireNonNull("Hello", "message");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testRequireNonNullException() {
        Assert.requireNonNull(null, "message");
    }

    @Test
    public void testRequireNonNullEntries() {
        Assert.requireNonNullEntries(Arrays.asList("a"), "message");
        Assert.requireNonNullEntries(Arrays.asList("a", "b"), "message");
    }

    public void testRequireNonNullEntriesException() {
        try {
            Assert.requireNonNullEntries(Arrays.asList(), "message");
            org.testng.Assert.fail();
        } catch (Exception e) {
            org.testng.Assert.assertTrue(e instanceof NullPointerException);
        }
        try {
            Assert.requireNonNullEntries(null, "message");
            org.testng.Assert.fail();
        } catch (Exception e) {
            org.testng.Assert.assertTrue(e instanceof NullPointerException);
        }
    }

    @Test
    public void testRequireNonBlank() {
        Assert.requireNonBlank("Hello", "message");
    }

    @Test
    public void testRequireNonBlankException() {
        try {
            Assert.requireNonBlank(null, "message");
            org.testng.Assert.fail();
        } catch (Exception e) {
            org.testng.Assert.assertTrue(e instanceof NullPointerException);
        }
        try {
            Assert.requireNonBlank("", "message");
            org.testng.Assert.fail();
        } catch (Exception e) {
            org.testng.Assert.assertTrue(e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testIsBlank() {
        org.testng.Assert.assertTrue(Assert.isBlank(""));
        org.testng.Assert.assertTrue(Assert.isBlank(null));
        org.testng.Assert.assertFalse(Assert.isBlank("a"));
    }

    @Test
    public void testRequireState() {
        Assert.requireState(true, "message");
        try {
            Assert.requireState(false, "message");
            org.testng.Assert.fail();
        } catch (Exception e) {
            org.testng.Assert.assertTrue(e instanceof IllegalStateException);
        }
    }
}
