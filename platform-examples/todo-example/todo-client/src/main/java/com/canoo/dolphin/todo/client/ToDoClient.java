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
package com.canoo.dolphin.todo.client;

import com.canoo.platform.remoting.client.ClientContext;
import com.canoo.platform.remoting.client.javafx.DolphinPlatformApplication;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.MalformedURLException;
import java.net.URL;

public class ToDoClient extends DolphinPlatformApplication {

    @Override
    protected URL getServerEndpoint() throws MalformedURLException {
        return new URL("http://localhost:8080/todo-app/dolphin");
    }

    @Override
    protected void start(Stage primaryStage, ClientContext clientContext) throws Exception {
        ToDoView viewController = new ToDoView(clientContext);
        Scene scene = new Scene(viewController.getParent());
        scene.getStylesheets().add(ToDoClient.class.getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> System.exit(0));
        primaryStage.show();
    }

    public static void main(String[] args) {
        Platform.setImplicitExit(false);
        Application.launch(args);
    }
}
