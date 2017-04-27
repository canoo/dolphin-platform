package org.opendolphin.core.client;

import org.opendolphin.core.Attribute;
import org.opendolphin.core.client.comm.AbstractClientConnector;
import org.opendolphin.core.comm.*;
import org.opendolphin.util.Provider;

public class DefaultModelSynchronizer implements ModelSynchronizer {

    private final Provider<AbstractClientConnector> connectionProvider;

    public DefaultModelSynchronizer(Provider<AbstractClientConnector> connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public void onAdded(final ClientPresentationModel model) {
        final Command command = CreatePresentationModelCommand.makeFrom(model);
        send(command);
    }

    @Override
    public void onDeleted(final ClientPresentationModel model) {
        final Command command = new DeletePresentationModelCommand(model.getId());
        send(command);
    }

    @Override
    public void onPropertyChanged(final Attribute attribute, Object oldValue, Object newValue) {
        final Command command = new ValueChangedCommand(attribute.getId(), oldValue, newValue);
        send(command);
    }

    @Override
    public void onMetadataChanged(final Attribute attribute) {
        final Command command = new QualifierChangedCommand(attribute.getId(), attribute.getQualifier());
        send(command);
    }

    private void send(Command command) {
        AbstractClientConnector clientConnector = connectionProvider.get();
        if(clientConnector == null) {
            throw new IllegalStateException("No connection defined!");
        }
        clientConnector.send(command);
    }
}
