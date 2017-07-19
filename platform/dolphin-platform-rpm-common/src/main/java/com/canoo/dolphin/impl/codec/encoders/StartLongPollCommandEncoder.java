package com.canoo.dolphin.impl.codec.encoders;

import com.canoo.dolphin.impl.commands.StartLongPollCommand;
import com.canoo.impl.platform.core.Assert;
import com.google.gson.JsonObject;

import static org.opendolphin.core.comm.CommandConstants.ID;
import static org.opendolphin.core.comm.CommandConstants.START_LONG_POLL_COMMAND_ID;

public class StartLongPollCommandEncoder extends AbstractCommandTranscoder<StartLongPollCommand> {

    @Override
    public JsonObject encode(StartLongPollCommand command) {
        Assert.requireNonNull(command, "command");
        final JsonObject jsonCommand = new JsonObject();
        jsonCommand.addProperty(ID, START_LONG_POLL_COMMAND_ID);
        return jsonCommand;
    }

    @Override
    public StartLongPollCommand decode(JsonObject jsonObject) {
        return new StartLongPollCommand();
    }
}
