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
package org.opendolphin.core.server;

import org.opendolphin.RemotingConstants;
import org.opendolphin.core.BaseAttribute;
import org.opendolphin.core.comm.QualifierChangedCommand;

import java.util.List;

public class ServerAttribute extends BaseAttribute {

    private boolean notifyClient = true;

    public ServerAttribute(String propertyName, Object initialValue) {
        super(propertyName, initialValue);
    }

    public ServerAttribute(String propertyName, Object baseValue, String qualifier) {
        super(propertyName, baseValue, qualifier);
    }

    @Override
    public ServerPresentationModel getPresentationModel() {
        return (ServerPresentationModel) super.getPresentationModel();
    }

    @Override
    public void setValue(final Object newValue) {
        if (notifyClient) {
            ServerModelStore.changeValueCommand(getPresentationModel().getModelStore().getCurrentResponse(), this, newValue);
        }

        super.setValue(newValue);
        // on the server side, we have no listener on the model store to care for the distribution of
        // baseValue changes to all attributes of the same qualifier so we must care for that ourselves

        if (getQualifier() == null) {
            return;

        }

        if (getPresentationModel() == null) {
            return;

        }
        // we may not know the pm, yet
        for (ServerAttribute sameQualified : (List<ServerAttribute>)getPresentationModel().getModelStore().findAllAttributesByQualifier(getQualifier())) {
            if (sameQualified.equals(this)) {
                continue;
            }

            if (newValue == null && sameQualified.getValue() != null || newValue != null && sameQualified.getValue() == null || !newValue.equals(sameQualified.getValue())) {
                sameQualified.setValue(newValue);
            }
        }
    }

    @Override
    public void setQualifier(String value) {
        super.setQualifier(value);
        if (notifyClient) {
            getPresentationModel().getModelStore().getCurrentResponse().add(new QualifierChangedCommand(getId(), value));
        }

    }

    public String getOrigin() {
        return RemotingConstants.SERVER_ORIGIN;
    }

    /**
     * Do the applyChange without creating commands that are sent to the client
     */
    public void silently(Runnable applyChange) {
        boolean temp = notifyClient;
        notifyClient = false;
        try {
            applyChange.run();
        } finally {
            notifyClient = temp;
        }
    }

    /**
     * Do the applyChange with enforced creation of commands that are sent to the client
     */
    protected void verbosely(Runnable applyChange) {
        boolean temp = notifyClient;
        notifyClient = true;
        try {
            applyChange.run();
        } finally {
            notifyClient = temp;
        }
    }

    /**
     * Overriding the standard behavior of PCLs such that firing is enforced to be done
     * verbosely. This is safe since on the server side PCLs are never used for the control
     * of the client notification as part of the OpenDolphin infrastructure
     * (as opposed to the java client).
     * That is: all remaining PCLs are application specific and _must_ be called verbosely.
     */
    @Override
    protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
        verbosely(new Runnable() {
            @Override
            public void run() {
                ServerAttribute.super.firePropertyChange(propertyName, oldValue, newValue);
            }

        });
    }

}
