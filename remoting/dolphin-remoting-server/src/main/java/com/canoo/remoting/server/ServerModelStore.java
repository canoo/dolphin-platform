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
package com.canoo.remoting.server;

import com.canoo.communication.common.ModelStore;
import com.canoo.communication.common.ModelStoreConfig;
import com.canoo.communication.common.PresentationModel;
import com.canoo.communication.common.commands.Command;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The ServerModelStore is a {@link ModelStore} with customized behavior appropriate to the
 * server side of a Dolphin connection.  There is one ServerModelSore for each user session.
 * The ServerModelStore self-assigns a unique ID which identifies each user session.
 */
public class ServerModelStore extends ModelStore {

    protected List<Command> currentResponse = null;

    /** Used to create unique presentation model ids within one server model store. */
    protected long pmInstanceCount = 0L;

    /** thread safe unique store count across all sessions in order to create unique store ids.*/
    private static final AtomicInteger storeCount = new AtomicInteger(0);

    /** unique identification of the current user session. */
    public final int id = storeCount.getAndIncrement();

    public ServerModelStore() {
    }

    public ServerModelStore(ModelStoreConfig config) {
        super(config);
    }

    /** A shared mutable state that is safe to use since we are thread-confined */
    protected List<Command> getCurrentResponse() {
        return currentResponse;
    }

    /** A shared mutable state that is safe to use since we are thread-confined */
    public void setCurrentResponse(List<Command> currentResponse) {
        this.currentResponse = currentResponse;
    }

    @Override
    public boolean add(PresentationModel model) {
        boolean added = super.add(model);
        if (! added) return false;
        ((ServerPresentationModel)model).setModelStore(this);
        return true;
    }

    public int getId() {
        return id;
    }
}
