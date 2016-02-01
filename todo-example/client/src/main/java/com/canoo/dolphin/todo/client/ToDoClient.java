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
import com.canoo.dolphin.client.javafx.DolphinPlatformApplication;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ToDoClient extends DolphinPlatformApplication {

    @Override
    protected String getServerEndpoint() {
        return "http://localhost:8080/dolphin";
    }

    @Override
    protected void start(Stage primaryStage, ClientContext clientContext) throws Exception {
        try {
            ToDoViewBinder viewController = new ToDoViewBinder(clientContext);
            primaryStage.setScene(new Scene(viewController.getRootNode()));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
