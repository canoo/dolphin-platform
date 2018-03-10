package com.canoo.platform.samples.reconnect;

import com.canoo.platform.core.DolphinRuntimeException;
import com.canoo.platform.remoting.DolphinRemotingException;
import com.canoo.platform.remoting.client.ClientContext;
import com.canoo.platform.remoting.client.javafx.DolphinPlatformApplication;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

public class ReconnectClient extends DolphinPlatformApplication {

    private final static Logger LOG = LoggerFactory.getLogger(ReconnectClient.class);

    private boolean errorDialogVisible = false;

    @Override
    protected URL getServerEndpoint() throws MalformedURLException {
        return new URL("http://localhost:8080/reconnect-app/dolphin");
    }

    @Override
    protected void start(final Stage primaryStage, final ClientContext clientContext) throws Exception {
        final Button button = new Button("test");

        final StatusView statusView = new StatusView(clientContext);

        final HBox parent = new HBox(statusView.getRootNode(), button);

        primaryStage.setScene(new Scene(parent));
        primaryStage.show();
    }

    @Override
    protected void onRemotingError(final Stage primaryStage, final DolphinRemotingException exception) {
        showErrorDialog(primaryStage, "The connection is broken", exception);
    }

    @Override
    protected void onRuntimeError(final Stage primaryStage, final DolphinRuntimeException exception) {
        showErrorDialog(primaryStage, "An unexpected error happened", exception);
    }

    private void showErrorDialog(final Stage primaryStage, final String message, final Exception e) {
        LOG.error("Error", e);
        if(!errorDialogVisible) {
            Optional.ofNullable(primaryStage).ifPresent(s -> s.close());
            final ButtonType exitButton = new ButtonType("close application");
            final ButtonType reconnectButton = new ButtonType("reconnect");
            final Alert connectionErrorAlert = new Alert(Alert.AlertType.ERROR, message, exitButton, reconnectButton);
            errorDialogVisible = true;
            final ButtonType result = connectionErrorAlert.showAndWait().orElse(exitButton);
            errorDialogVisible = false;
            if (result.equals(exitButton)) {
                Platform.exit();
                System.exit(0);
            } else {
                reconnect(new Stage());
            }
        }
    }
}
