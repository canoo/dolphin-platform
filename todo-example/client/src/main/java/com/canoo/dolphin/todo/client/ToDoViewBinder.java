/*
 * Copyright 2015 Canoo Engineering AG.
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
import com.canoo.dolphin.client.javafx.AbstractViewBinder;
import com.canoo.dolphin.client.javafx.FXBinder;
import com.canoo.dolphin.client.javafx.FXWrapper;
import com.canoo.dolphin.todo.pm.ToDoItem;
import com.canoo.dolphin.todo.pm.ToDoList;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Created by hendrikebbers on 16.09.15.
 */
public class ToDoViewBinder extends AbstractViewBinder<ToDoList> {

    private final TextField createField;
    private final Button createButton;
    private final ListView<ToDoItem> itemList;
    private final StackPane root;

    public ToDoViewBinder(ClientContext clientContext) {
        super(clientContext, "ToDoController");

        createField = new TextField();
        createButton = new Button("Create");
        createButton.setDisable(true);
        HBox createComponent = new HBox(createField, createButton);
        itemList = new ListView<>();
        VBox vBox = new VBox(createComponent, itemList);
        root = new StackPane(vBox);
        itemList.setCellFactory(c -> new ToDoItemCell(i -> invoke("markChanged", new Param("itemName", i.getText()))));
    }


    public StackPane getRoot() {
        return root;
    }

    @Override
    protected void init() {
        FXBinder.bind(createField.textProperty()).bidirectionalTo(getModel().getNewItemText());
        ObservableList<ToDoItem> items = FXWrapper.wrapList(getModel().getItems());
        itemList.setItems(items);
        createButton.setDisable(false);
        createButton.setOnAction(event -> invoke("add"));
    }

}
