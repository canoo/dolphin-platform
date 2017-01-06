package com.canoo.dolphin.impl.collections;

import com.canoo.implementation.dolphin.collections.ObservableArrayList;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class TestObservableArrayList {

    @Test
    public void testCreation() {
        ObservableArrayList<String> list = new ObservableArrayList<>();
        Assert.assertTrue(list.isEmpty());
        Assert.assertEquals(list.size(), 0);

        list = new ObservableArrayList<>(12);
        Assert.assertTrue(list.isEmpty());
        Assert.assertEquals(list.size(), 0);

        list = new ObservableArrayList<>("1", "2", "3");
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 3);

        list = new ObservableArrayList<>(Arrays.asList("1", "2", "3"));
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 3);
    }

    @Test
    public void testSize() {
        ObservableArrayList<String> list = new ObservableArrayList<>();
        Assert.assertTrue(list.isEmpty());
        Assert.assertEquals(list.size(), 0);

        list.add("HUHU");
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 1);

        list.add("TEST");
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 2);

        list.clear();
        Assert.assertTrue(list.isEmpty());
        Assert.assertEquals(list.size(), 0);
    }

    @Test
    public void testAddAndRemove() {
        ObservableArrayList<String> list = new ObservableArrayList<>();
        Assert.assertTrue(list.isEmpty());
        Assert.assertEquals(list.size(), 0);

        list.add("HUHU");
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 1);
        assertSameContent(list, Arrays.asList("HUHU"));
        list.clear();

        list.addAll("1", "2", "3");
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 3);
        assertSameContent(list, Arrays.asList("1", "2", "3"));
        list.clear();

        list.addAll(Arrays.asList("1", "2", "3"));
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 3);
        assertSameContent(list, Arrays.asList("1", "2", "3"));
        list.clear();

        list.addAll(Arrays.asList("1", "2", "3"));
        list.remove(0);
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 2);
        assertSameContent(list, Arrays.asList("2", "3"));
        list.clear();

        list.addAll(Arrays.asList("1", "2", "3"));
        list.remove(1);
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 2);
        assertSameContent(list, Arrays.asList("1", "3"));
        list.clear();

        list.addAll(Arrays.asList("1", "2", "3"));
        list.remove("2");
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(list.size(), 2);
        assertSameContent(list, Arrays.asList("1", "3"));
        list.clear();

        //Implementation still missing
//        list.addAll(Arrays.asList("1", "2", "3"));
//        list.removeAll("1", "2");
//        Assert.assertFalse(list.isEmpty());
//        Assert.assertEquals(list.size(), 1);
//        assertSameContent(list, Arrays.asList("3"));
//        list.clear();
//
//        list.addAll(Arrays.asList("1", "2", "3"));
//        list.removeAll(Arrays.asList("1", "3"));
//        Assert.assertFalse(list.isEmpty());
//        Assert.assertEquals(list.size(), 1);
//        assertSameContent(list, Arrays.asList("2"));
//        list.clear();
    }

    private <T> void assertSameContent(List<T> a, List<T> b) {
        Assert.assertTrue(a.size() == b.size());
        for(T t : a) {
            Assert.assertTrue(b.contains(t));
            Assert.assertTrue(a.indexOf(t) == b.indexOf(t));
        }
    }

}
