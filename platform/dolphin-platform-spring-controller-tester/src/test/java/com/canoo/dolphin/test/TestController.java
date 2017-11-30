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
package com.canoo.dolphin.test;

import com.canoo.platform.remoting.BeanManager;
import com.canoo.platform.remoting.server.DolphinAction;
import com.canoo.platform.remoting.server.DolphinController;
import com.canoo.platform.remoting.server.DolphinModel;
import com.canoo.platform.remoting.server.event.RemotingEventBus;
import com.canoo.platform.remoting.server.event.MessageEvent;
import com.canoo.platform.remoting.server.event.MessageListener;
import com.canoo.platform.remoting.server.event.Topic;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@DolphinController("TestController")
public class TestController {

    @Autowired
    private BeanManager beanManager;

    @Autowired
    private RemotingEventBus eventBus;

    @DolphinModel
    private TestModel model;

    private static Topic<String> TEST_TOPIC = Topic.create();

    @PostConstruct
    public void init() {
        eventBus.subscribe(TEST_TOPIC, new MessageListener<String>() {
            @Override
            public void accept(MessageEvent<String> message) {
                model.setValue(message.getData());
            }
        });
    }

    @DolphinAction("sendEvent")
    public void sendEvent() {
        eventBus.publish(TEST_TOPIC, "changed by eventBus!");
    }

    @DolphinAction("action")
    public void doSomeAction() {
        model.setValue("Hello Dolphin Test");
    }

    @DolphinAction("addToList")
    public void addToList() {
        model.getItems().add("Hallo");
    }

    @DolphinAction("addBeanToList")
    public void addBeanToList() {
        TestSubModel bean = beanManager.create(TestSubModel.class);
        bean.setValue("I'm a subbean");
        model.getInternModels().add(bean);
    }
}
