package com.canoo.dolphin.util;

import com.canoo.implementation.dolphin.util.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;

/**
 * Created by hendrikebbers on 03.01.17.
 */
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
