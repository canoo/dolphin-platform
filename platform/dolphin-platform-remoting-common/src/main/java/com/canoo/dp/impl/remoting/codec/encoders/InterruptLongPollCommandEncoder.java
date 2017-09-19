package com.canoo.dp.impl.remoting.codec.encoders;

import com.canoo.dp.impl.remoting.legacy.commands.InterruptLongPollCommand;
import com.canoo.dp.impl.platform.core.Assert;
import com.google.gson.JsonObject;

import static com.canoo.dp.impl.remoting.legacy.commands.CommandConstants.ID;
import static com.canoo.dp.impl.remoting.legacy.commands.CommandConstants.INTERRUPT_LONG_POLL_COMMAND_ID;

@Deprecated
public class InterruptLongPollCommandEncoder extends AbstractCommandTranscoder<InterruptLongPollCommand> {

    @Override
    public JsonObject encode(InterruptLongPollCommand command) {
        Assert.requireNonNull(command, "command");
        final JsonObject jsonCommand = new JsonObject();
        jsonCommand.addProperty(ID, INTERRUPT_LONG_POLL_COMMAND_ID);
        return jsonCommand;
    }

    @Override
    public InterruptLongPollCommand decode(JsonObject jsonObject) {
        return new InterruptLongPollCommand();
    }
}
