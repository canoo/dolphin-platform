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
package com.canoo.impl.server;

import com.canoo.dp.impl.server.client.HttpClientSessionImpl;
import com.canoo.platform.server.client.ClientSession;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ClientSessionImplTest {

    @Test
    public void testAddAttribute() {
        //given:
        ClientSession dolphinSession = new HttpClientSessionImpl();

        //when:
        dolphinSession.setAttribute("test-attribute", "Hello Dolphin Session");

        //then:
        Assert.assertEquals(1, dolphinSession.getAttributeNames().size());
        Assert.assertTrue(dolphinSession.getAttributeNames().contains("test-attribute"));
        Assert.assertEquals("Hello Dolphin Session", dolphinSession.getAttribute("test-attribute"));
    }

    @Test
    public void testNullAttribute() {
        //given:
        ClientSession dolphinSession = new HttpClientSessionImpl();

        //then:
        Assert.assertEquals(0, dolphinSession.getAttributeNames().size());
        Assert.assertNull(dolphinSession.getAttribute("test-attribute"));
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testImmutableAttributeSet() {
        //given:
        ClientSession dolphinSession = new HttpClientSessionImpl();

        //then:
        dolphinSession.getAttributeNames().add("att");
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testImmutableAttributeSet2() {
        //given:
        ClientSession dolphinSession = new HttpClientSessionImpl();

        //when:
        dolphinSession.setAttribute("test-attribute", "Hello Dolphin Session");

        //then:
        dolphinSession.getAttributeNames().remove("test-attribute");
    }

    @Test
    public void testRemoveAttribute() {
        //given:
        ClientSession dolphinSession = new HttpClientSessionImpl();

        //when:
        dolphinSession.setAttribute("test-attribute", "Hello Dolphin Session");
        dolphinSession.removeAttribute("test-attribute");

        //then:
        Assert.assertEquals(0, dolphinSession.getAttributeNames().size());
        Assert.assertNull(dolphinSession.getAttribute("test-attribute"));
    }

    @Test
    public void testMultipleAttributes() {
        //given:
        ClientSession dolphinSession = new HttpClientSessionImpl();

        //when:
        dolphinSession.setAttribute("test-attribute1", "Hello Dolphin Session");
        dolphinSession.setAttribute("test-attribute2", "Yeah!");
        dolphinSession.setAttribute("test-attribute3", "Dolphin Platform");

        //then:
        Assert.assertEquals(3, dolphinSession.getAttributeNames().size());
        Assert.assertTrue(dolphinSession.getAttributeNames().contains("test-attribute1"));
        Assert.assertTrue(dolphinSession.getAttributeNames().contains("test-attribute2"));
        Assert.assertTrue(dolphinSession.getAttributeNames().contains("test-attribute3"));
        Assert.assertEquals("Hello Dolphin Session", dolphinSession.getAttribute("test-attribute1"));
        Assert.assertEquals("Yeah!", dolphinSession.getAttribute("test-attribute2"));
        Assert.assertEquals("Dolphin Platform", dolphinSession.getAttribute("test-attribute3"));
    }

    @Test
    public void testInvalidate() {
        //given:
        ClientSession dolphinSession = new HttpClientSessionImpl();

        //when:
        dolphinSession.setAttribute("test-attribute1", "Hello Dolphin Session");
        dolphinSession.setAttribute("test-attribute2", "Yeah!");
        dolphinSession.setAttribute("test-attribute3", "Dolphin Platform");
        dolphinSession.invalidate();

        //then:
        Assert.assertEquals(0, dolphinSession.getAttributeNames().size());
        Assert.assertFalse(dolphinSession.getAttributeNames().contains("test-attribute1"));
        Assert.assertFalse(dolphinSession.getAttributeNames().contains("test-attribute2"));
        Assert.assertFalse(dolphinSession.getAttributeNames().contains("test-attribute3"));
        Assert.assertNull(dolphinSession.getAttribute("test-attribute1"));
        Assert.assertNull(dolphinSession.getAttribute("test-attribute2"));
        Assert.assertNull(dolphinSession.getAttribute("test-attribute3"));
    }
}
