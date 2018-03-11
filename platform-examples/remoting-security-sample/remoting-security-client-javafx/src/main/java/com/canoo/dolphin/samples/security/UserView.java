package com.canoo.dolphin.samples.security;

import com.canoo.platform.remoting.client.ClientContext;
import com.canoo.platform.remoting.client.javafx.FXBinder;
import com.canoo.platform.remoting.client.javafx.view.AbstractViewController;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import static com.canoo.dolphin.samples.security.Constants.USER_CONTROLLER;

public class UserView extends AbstractViewController<UserBean> {

    private final GridPane pane;

    private final TextField userField;

    private final TextField mailField;

    public UserView(final ClientContext clientContext) {
        super(clientContext, USER_CONTROLLER);
        this.pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(24));
        userField = new TextField();
        mailField = new TextField();
        pane.add(new Label("Username:"), 0, 0);
        pane.add(userField, 1, 0);
        pane.add(new Label("Mail address:"), 0, 1);
        pane.add(mailField, 1, 1);
    }

    @Override
    protected void init() {
        FXBinder.bind(userField.textProperty()).bidirectionalTo(getModel().userNameProperty());
        FXBinder.bind(mailField.textProperty()).bidirectionalTo(getModel().mailAddressProperty());
    }

    @Override
    public Node getRootNode() {
        return pane;
    }
}
