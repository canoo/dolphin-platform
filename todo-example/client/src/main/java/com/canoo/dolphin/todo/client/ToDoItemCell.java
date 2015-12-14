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

import com.canoo.dolphin.client.javafx.FXBinder;
import com.canoo.dolphin.todo.pm.ToDoItem;
import javafx.scene.control.ListCell;
import javafx.scene.text.Text;

import java.util.function.Consumer;

public class ToDoItemCell extends ListCell<ToDoItem> {

    public ToDoItemCell(Consumer<ToDoItem> actionConsumer) {
        Text itemNameText = new Text();

        itemNameText.visibleProperty().bind(emptyProperty().not());
        setGraphic(itemNameText);

        itemProperty().addListener((obs, oldVal, newVal) -> {
            itemNameText.textProperty().unbind();
            itemNameText.strikethroughProperty().unbind();
            if(newVal != null) {
                FXBinder.bind(itemNameText.textProperty()).to(newVal.getTextProperty());
                FXBinder.bind(itemNameText.strikethroughProperty()).bidirectionalTo(newVal.getCompletedProperty());
            }
        });

        setOnMouseClicked(e -> {
            if(getItem() != null) {
                actionConsumer.accept(getItem());
            }
        });

        setStyle("-fx-background-color: white");
        itemNameText.setStyle("-fx-font-size: 24px");
    }
}
