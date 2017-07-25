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
package com.canoo.dolphin.impl;

import com.canoo.dp.impl.platform.core.IdentitySet;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

public class IdentitySetTest {

    @Test
    public void testSize() {

        IdentitySet<Date> identitySet = new IdentitySet<>();

        Date date1 = new Date();
        Date date2 = new Date(date1.getTime());

        Assert.assertTrue(identitySet.isEmpty());
        Assert.assertEquals(identitySet.size(), 0);


        identitySet.add(date1);
        Assert.assertFalse(identitySet.isEmpty());
        Assert.assertEquals(identitySet.size(), 1);

        identitySet.add(date2);
        Assert.assertFalse(identitySet.isEmpty());
        Assert.assertEquals(identitySet.size(), 2);

        identitySet.remove(date2);
        Assert.assertFalse(identitySet.isEmpty());
        Assert.assertEquals(identitySet.size(), 1);

        identitySet.clear();
        Assert.assertTrue(identitySet.isEmpty());
        Assert.assertEquals(identitySet.size(), 0);
    }

    @Test
    public void testIterator() {

        IdentitySet<Date> identitySet = new IdentitySet<>();

        Date date1 = new Date();
        Date date2 = new Date(date1.getTime());
        identitySet.add(date1);
        identitySet.add(date2);

        Iterator<Date> iterator = identitySet.iterator();

        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(iterator.next(), date1);
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(iterator.next(), date2);
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testToArray() {

        IdentitySet<Date> identitySet = new IdentitySet<>();

        Date date1 = new Date();
        Date date2 = new Date(date1.getTime());
        identitySet.add(date1);
        identitySet.add(date2);

        Object[] itemArray = identitySet.toArray();
        Assert.assertEquals(itemArray.length, 2);
        Assert.assertEquals(itemArray[0], date1);
        Assert.assertEquals(itemArray[1], date2);

        Date[] itemArray2 = identitySet.toArray(new Date[0]);
        Assert.assertEquals(itemArray2.length, 2);
        Assert.assertEquals(itemArray2[0], date1);
        Assert.assertEquals(itemArray2[1], date2);
    }

    @Test
    public void testAddAll() {

        IdentitySet<Date> identitySet = new IdentitySet<>();

        Date date1 = new Date();
        Date date2 = new Date(date1.getTime());

        identitySet.addAll(Arrays.asList(date1, date2));

        Assert.assertEquals(identitySet.size(), 2);
        Assert.assertTrue(identitySet.contains(date1));
        Assert.assertTrue(identitySet.contains(date2));
    }

    @Test
    public void testRemoveAll() {

        IdentitySet<Date> identitySet = new IdentitySet<>();

        Date date1 = new Date();
        Date date2 = new Date(date1.getTime());

        identitySet.addAll(Arrays.asList(date1, date2));

        identitySet.removeAll(Arrays.asList(date1, date2));

        Assert.assertEquals(identitySet.size(), 0);
        Assert.assertFalse(identitySet.contains(date1));
        Assert.assertFalse(identitySet.contains(date2));


        identitySet.addAll(Arrays.asList(date1, date2));
        identitySet.removeAll(Arrays.asList(date1));

        Assert.assertEquals(identitySet.size(), 1);
        Assert.assertFalse(identitySet.contains(date1));
        Assert.assertTrue(identitySet.contains(date2));
    }



    @Test
    public void testContains() {

        IdentitySet<Date> identitySet = new IdentitySet<>();

        Date date1 = new Date();
        Date date2 = new Date(date1.getTime());

        Assert.assertFalse(identitySet.contains(date1));
        Assert.assertFalse(identitySet.contains(date2));
        Assert.assertTrue(identitySet.containsAll(new ArrayList<>()));
        Assert.assertFalse(identitySet.containsAll(Arrays.asList(date1)));
        Assert.assertFalse(identitySet.containsAll(Arrays.asList(date2)));
        Assert.assertFalse(identitySet.containsAll(Arrays.asList(date1, date2)));
        Assert.assertFalse(identitySet.containsAll(Arrays.asList(date2, date1)));

        identitySet.add(date1);
        Assert.assertTrue(identitySet.contains(date1));
        Assert.assertFalse(identitySet.contains(date2));
        Assert.assertTrue(identitySet.containsAll(new ArrayList<>()));
        Assert.assertTrue(identitySet.containsAll(Arrays.asList(date1)));
        Assert.assertFalse(identitySet.containsAll(Arrays.asList(date2)));
        Assert.assertFalse(identitySet.containsAll(Arrays.asList(date1, date2)));
        Assert.assertFalse(identitySet.containsAll(Arrays.asList(date2, date1)));

        identitySet.add(date2);
        Assert.assertTrue(identitySet.contains(date1));
        Assert.assertTrue(identitySet.contains(date2));
        Assert.assertTrue(identitySet.containsAll(new ArrayList<>()));
        Assert.assertTrue(identitySet.containsAll(Arrays.asList(date1)));
        Assert.assertTrue(identitySet.containsAll(Arrays.asList(date2)));
        Assert.assertTrue(identitySet.containsAll(Arrays.asList(date1, date2)));
        Assert.assertTrue(identitySet.containsAll(Arrays.asList(date2, date1)));

        identitySet.remove(date2);
        Assert.assertTrue(identitySet.contains(date1));
        Assert.assertFalse(identitySet.contains(date2));
        Assert.assertTrue(identitySet.containsAll(new ArrayList<>()));
        Assert.assertTrue(identitySet.containsAll(Arrays.asList(date1)));
        Assert.assertFalse(identitySet.containsAll(Arrays.asList(date2)));
        Assert.assertFalse(identitySet.containsAll(Arrays.asList(date1, date2)));
        Assert.assertFalse(identitySet.containsAll(Arrays.asList(date2, date1)));

        identitySet.clear();
        Assert.assertFalse(identitySet.contains(date1));
        Assert.assertFalse(identitySet.contains(date2));
        Assert.assertTrue(identitySet.containsAll(new ArrayList<>()));
        Assert.assertFalse(identitySet.containsAll(Arrays.asList(date1)));
        Assert.assertFalse(identitySet.containsAll(Arrays.asList(date2)));
        Assert.assertFalse(identitySet.containsAll(Arrays.asList(date1, date2)));
        Assert.assertFalse(identitySet.containsAll(Arrays.asList(date2, date1)));
    }

}
