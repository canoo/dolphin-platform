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
package com.canoo.dolphin.todo.server;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;
import com.canoo.dolphin.server.DolphinModel;
import com.canoo.dolphin.server.Param;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.Topic;
import com.canoo.dolphin.todo.pm.ToDoItem;
import com.canoo.dolphin.todo.pm.ToDoList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import static com.canoo.dolphin.todo.TodoAppConstants.ADD_ACTION;
import static com.canoo.dolphin.todo.TodoAppConstants.CONTROLLER_NAME;
import static com.canoo.dolphin.todo.TodoAppConstants.ITEM_PARAM;
import static com.canoo.dolphin.todo.TodoAppConstants.MARK_ACTION;

@DolphinController(CONTROLLER_NAME)
public class ToDoController {

    private final static Topic<String> ITEM_MARK_CHANGED = Topic.create("item_mark_changed");

    private final static Topic<String> ITEM_ADDED = Topic.create("item_added");

    @Inject
    private BeanManager beanManager;

    @Inject
    private DolphinEventBus eventBus;

    @DolphinModel
    private ToDoList toDoList;

    @PostConstruct
    public void onInit() {
        eventBus.subscribe(ITEM_MARK_CHANGED,
                m -> {
                    toDoList.getItems().stream().filter(i -> i.getText().equals(m.getData())).forEach(i -> i.setCompleted(!i.isCompleted()));
                }
        );

        eventBus.subscribe(ITEM_ADDED,
                m -> {
                    onAdded(m.getData());
                }
        );

        System.out.println("Init");
    }

    @PreDestroy
    public void onDestroy() {
        System.out.println("Destroyed");
    }

    @DolphinAction(ADD_ACTION)
    public void add() {
        final String newItemText = toDoList.getNewItemText().get();
        toDoList.getNewItemText().set("");
        eventBus.publish(ITEM_ADDED, newItemText);
    }

    @DolphinAction(MARK_ACTION)
    public void markChanged(@Param(ITEM_PARAM) String name) {
        eventBus.publish(ITEM_MARK_CHANGED, name);
    }

    private void onAdded(String text) {
        final ToDoItem toDoItem = beanManager.create(ToDoItem.class);
        toDoItem.setText(text);
        toDoList.getItems().add(toDoItem);
    }
}
