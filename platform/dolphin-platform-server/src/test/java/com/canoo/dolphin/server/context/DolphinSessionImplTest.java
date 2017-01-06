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
package com.canoo.dolphin.server.context;

import com.canoo.dolphin.server.session.DolphinSession;
import com.canoo.implementation.dolphin.server.context.DolphinSessionImpl;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.Executor;

/**
 * Created by hendrikebbers on 18.03.16.
 */
public class DolphinSessionImplTest {

    @Test
    public void testid() {
        //given:
        DolphinSession dolphinSession = new DolphinSessionImpl("test-id", new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        });

        //then:
        Assert.assertEquals("test-id", dolphinSession.getId());
    }

    @Test
    public void testAddAttribute() {
        //given:
        DolphinSession dolphinSession = new DolphinSessionImpl("test-id", new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        });

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
        DolphinSession dolphinSession = new DolphinSessionImpl("test-id", new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        });

        //then:
        Assert.assertEquals(0, dolphinSession.getAttributeNames().size());
        Assert.assertNull(dolphinSession.getAttribute("test-attribute"));
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testImmutableAttributeSet() {
        //given:
        DolphinSession dolphinSession = new DolphinSessionImpl("test-id", new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        });

        //then:
        dolphinSession.getAttributeNames().add("att");
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testImmutableAttributeSet2() {
        //given:
        DolphinSession dolphinSession = new DolphinSessionImpl("test-id", new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        });

        //when:
        dolphinSession.setAttribute("test-attribute", "Hello Dolphin Session");

        //then:
        dolphinSession.getAttributeNames().remove("test-attribute");
    }

    @Test
    public void testRemoveAttribute() {
        //given:
        DolphinSession dolphinSession = new DolphinSessionImpl("test-id", new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        });

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
        DolphinSession dolphinSession = new DolphinSessionImpl("test-id", new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        });

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
        DolphinSession dolphinSession = new DolphinSessionImpl("test-id", new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        });

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
