package org.opendolphin.core.util;

import org.opendolphin.core.comm.Command;

public abstract class AbstractTestCommand extends Command {
    public AbstractTestCommand(String id) {
        super(id);
    }
}
