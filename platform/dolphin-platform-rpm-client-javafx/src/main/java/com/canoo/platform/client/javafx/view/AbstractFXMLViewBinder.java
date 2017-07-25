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
package com.canoo.platform.client.javafx.view;

import com.canoo.platform.client.ClientContext;
import com.canoo.dp.impl.platform.core.Assert;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.net.URL;

/**
 * Defines an abstract class that can be used to create a FXML based view that is bound to a Dolphin Platform controller
 * on the server and shares a model with the controller.
 *
 * By creating new instances of classes that extend this class a JavaFX view will be automatically created by using the
 * given FXML file. In addition the {@link javafx.fxml.FXML} annotation can be used in that classes.
 *
 * For information about the Dolphin Platform based behavior of this class see {@link AbstractViewBinder}.
 *
 * @param <M> type of the model.
 */
public abstract class AbstractFXMLViewBinder<M> extends AbstractViewBinder<M> {

    private final Node rootNode;

    /**
     * Constructor
     * @param clientContext the DOlphin Platform client context
     * @param controllerName the controller name of the Dolphin Platform controller definition on the server that should
     *                       be used with this view.
     * @param fxmlLocation the location (url) of the FXML file that defines the layout of the view.
     */
    public AbstractFXMLViewBinder(ClientContext clientContext, String controllerName, URL fxmlLocation) {
        super(clientContext, controllerName);
        Assert.requireNonNull(fxmlLocation, "fxmlLocation");
        try {
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            loader.setController(this);
            rootNode = loader.load();
        } catch (Exception e) {
            throw new FxmlLoadException("Can not create view based on FXML location " + fxmlLocation, e);
        }
    }

    @Override
    public Node getRootNode() {
        return rootNode;
    }
}
