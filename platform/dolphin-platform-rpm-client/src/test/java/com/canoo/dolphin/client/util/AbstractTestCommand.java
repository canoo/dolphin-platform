package com.canoo.dolphin.client.util;

import org.opendolphin.core.comm.Command;

/**
 * Created by hendrikebbers on 01.07.17.
 */
public abstract class AbstractTestCommand extends Command{
    public AbstractTestCommand(String id) {
        super(id);
    }
}
