package com.canoo.dolphin.impl.codec.encoders;

import com.canoo.dolphin.impl.commands.CreateContextCommand;
import com.canoo.dp.impl.platform.core.Assert;
import com.google.gson.JsonObject;

import static com.canoo.dolphin.impl.codec.CommandConstants.*;

public class CreateContextCommandEncoder extends AbstractCommandEncoder<CreateContextCommand> {
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
