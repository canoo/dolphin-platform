package com.canoo.platform.samples.reconnect;

import com.canoo.platform.remoting.client.ClientContext;
import com.canoo.platform.remoting.client.javafx.FXBinder;
import com.canoo.platform.remoting.client.javafx.view.AbstractViewController;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.util.Optional;

import static com.canoo.platform.samples.reconnect.RemotingConstants.STATUS_CONTROLLER_NAME;

public class StatusView extends AbstractViewController<StatusBean> {

    private Label timeLabel = new Label();

    public StatusView(final ClientContext clientContext) {
        super(clientContext, STATUS_CONTROLLER_NAME);
    }

    @Override
    protected void init() {
        FXBinder.bind(timeLabel.textProperty()).to(getModel().timeProperty(), t -> Optional.ofNullable(t).map(v -> v.toString()).orElse(""));
    }

    @Override
    public Node getRootNode() {
        return timeLabel;
    }
}
