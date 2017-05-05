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
package com.canoo.dolphin.client.javafx;

import com.canoo.dolphin.client.ClientInitializationException;
import com.canoo.dolphin.client.DolphinRuntimeException;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class SimpleDolphinPlatformApplication extends DolphinPlatformApplication {

    private void showError(Stage parent, String header, String content, Exception e) {
        parent.hide();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        ButtonType reconnect = new ButtonType("reconnect");
        alert.getButtonTypes().addAll(reconnect);

        alert.getDialogPane().setExpandableContent(expContent);
        ButtonType result = alert.showAndWait().orElse(null);

        if (result != null && reconnect.equals(result)) {
            reconnect(parent);
        } else {
            System.exit(0);
        }
    }

    @Override
    protected void onInitializationError(Stage primaryStage, ClientInitializationException initializationException, Iterable<DolphinRuntimeException> possibleCauses) {
        showError(primaryStage, "Error on initialization", "A error happened while initializing the Client and Connection", initializationException);
    }

    @Override
    protected void onRuntimeError(Stage primaryStage, DolphinRuntimeException runtimeException) {
        showError(primaryStage, "Error at runtime", "A error happened at runtime", runtimeException);
    }

}
