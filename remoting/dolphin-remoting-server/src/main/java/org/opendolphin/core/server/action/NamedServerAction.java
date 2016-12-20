package org.opendolphin.core.server.action;

import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.NamedCommandHandler;

/**
 * Java-friendly action handling
 */
public class NamedServerAction extends DolphinServerAction {

    private final String name;

    private final NamedCommandHandler namedCommandHandler;

    public NamedServerAction(String name, NamedCommandHandler namedCommandHandler) {
        this.name = name;
        this.namedCommandHandler = namedCommandHandler;
    }

    @Override
    public void registerIn(ActionRegistry registry) {
        registry.register(name, namedCommandHandler);
    }

    public final String getName() {
        return name;
    }

}
