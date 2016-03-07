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
package com.canoo.dolphin.todo.client;

import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.Param;
import com.canoo.dolphin.client.javafx.AbstractFXMLViewBinder;
import com.canoo.dolphin.client.javafx.FXBinder;
import com.canoo.dolphin.todo.TodoConstants;
import com.canoo.dolphin.todo.pm.ToDoItem;
import com.canoo.dolphin.todo.pm.ToDoList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;

public class ToDoViewBinder extends AbstractFXMLViewBinder<ToDoList> {

    @FXML
    private TextField createField;

    @FXML
    private Button createButton;

    @FXML
    private ListView<ToDoItem> itemList;

    public ToDoViewBinder(ClientContext clientContext) throws IOException {
        super(clientContext, TodoConstants.TODO_CONTROLLER_NAME, ToDoViewBinder.class.getResource("view.fxml"));
    }

    @Override
    protected void init() {
        itemList.setCellFactory(c -> new ToDoItemCell(i -> invoke(TodoConstants.MARK_ACTION, new Param(TodoConstants.ITEM_NAME_PARAM, i.getText()))));

        FXBinder.bind(createField.textProperty()).bidirectionalTo(getModel().getNewItemText());
        FXBinder.bind(itemList.getItems()).to(getModel().getItems());
        createButton.setOnAction(event -> invoke(TodoConstants.ADD_ACTION));
    }

}
