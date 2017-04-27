package org.opendolphin.core.client;

import org.opendolphin.core.Attribute;

public interface ModelSynchronizer {

    void onAdded(ClientPresentationModel model);

    void onDeleted(ClientPresentationModel model);

    void onPropertyChanged(Attribute attribute, Object oldValue, Object newValue);

    void onMetadataChanged(Attribute attribute);
}
