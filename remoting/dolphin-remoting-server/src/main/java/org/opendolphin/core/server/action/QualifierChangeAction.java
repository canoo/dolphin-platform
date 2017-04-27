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
package org.opendolphin.core.server.action;

import org.opendolphin.core.comm.QualifierChangedCommand;
import org.opendolphin.core.server.ServerAttribute;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;

@Deprecated
public class QualifierChangeAction extends DolphinServerAction {

    public void registerIn(ActionRegistry registry) {
        registry.register(QualifierChangedCommand.class, new CommandHandler<QualifierChangedCommand>() {
            @Override
            public void handleCommand(final QualifierChangedCommand command) {
                final ServerAttribute attribute = getServerModelStore().findAttributeById(command.getAttributeId());
                if (attribute == null) {
                    throw new IllegalStateException("Can not find attribute with id '" + command.getAttributeId());
                }

                attribute.silently(new Runnable() {
                    @Override
                    public void run() {
                        attribute.setQualifier(command.getQualifier());
                    }

                });
            }
        });
    }

}
