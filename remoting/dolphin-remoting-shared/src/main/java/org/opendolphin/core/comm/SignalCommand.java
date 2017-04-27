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
package org.opendolphin.core.comm;

/**
 * A command where the id can be set from the outside for general purposes.
 * Signal commands are transmitted outside the usual sequence but possibly in the same
 * session. Therefore any handler for this command must neither change nor access any unprotected shared
 * mutable state like the dolphin instance or the model store.
 */
@Deprecated
public class SignalCommand extends Command {
    public SignalCommand() {
    }

    public SignalCommand(String id) {
        super(id);
    }
}
