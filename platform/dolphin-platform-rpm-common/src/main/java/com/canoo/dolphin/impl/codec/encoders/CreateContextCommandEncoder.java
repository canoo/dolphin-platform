package com.canoo.dolphin.impl.codec.encoders;

import com.canoo.dolphin.impl.commands.CreateContextCommand;
import com.canoo.impl.platform.core.Assert;
import com.google.gson.JsonObject;

import static org.opendolphin.core.comm.CommandConstants.*;

public class CreateContextCommandEncoder extends AbstractCommandTranscoder<CreateContextCommand> {
    @Override
    public JsonObject encode(CreateContextCommand command) {
        Assert.requireNonNull(command, "command");
        final JsonObject jsonCommand = new JsonObject();
        jsonCommand.addProperty(ID, CREATE_CONTEXT_COMMAND_ID);
        return jsonCommand;
    }

    @Override
    public CreateContextCommand decode(JsonObject jsonObject) {
        return new CreateContextCommand();
    }
}
