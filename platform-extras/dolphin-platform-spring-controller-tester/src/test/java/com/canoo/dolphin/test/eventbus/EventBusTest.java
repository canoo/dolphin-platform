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
package com.canoo.dolphin.test.eventbus;

import com.canoo.dolphin.test.spring.ControllerUnderTest;
import com.canoo.dolphin.test.spring.SpringTestNGControllerTest;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class EventBusTest extends SpringTestNGControllerTest {

    private ControllerUnderTest<EventBusTestModel> publisher;

    private ControllerUnderTest<EventBusTestModel> subscriber;

    @BeforeMethod
    protected void init() {
        publisher = createController(EventBusTestConstants.EVENT_BUS_PUBLISHER_CONTROLLER_NAME);
        subscriber = createController(EventBusTestConstants.EVENT_BUS_SUBSCIBER_CONTROLLER_NAME);
    }


    @AfterMethod
    protected void destroy() {
        publisher.destroy();
        subscriber.destroy();
    }

    @Test
    public void testValuesNotEqualByDefault() {
        //given:
        publisher.getModel().valueProperty().set("A");
        subscriber.getModel().valueProperty().set("B");

        //then:
        Assert.assertFalse(publisher.getModel().valueProperty().get().equals(subscriber.getModel().valueProperty().get()));
    }

    @Test
    public void testValuesEqualsAfterEvent() {
        //given:
        publisher.getModel().valueProperty().set("A");

        //when:
        publisher.invoke(EventBusTestConstants.CALL_ACTION);

        //then:
        Assert.assertTrue(publisher.getModel().valueProperty().get().equals(subscriber.getModel().valueProperty().get()));
        Assert.assertEquals(publisher.getModel().valueProperty().get(), "A");
        Assert.assertEquals(subscriber.getModel().valueProperty().get(), "A");
    }

}