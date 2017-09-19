package com.canoo.dp.impl.remoting.codec.encoders;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.remoting.legacy.commands.DeletePresentationModelCommand;
import com.google.gson.JsonObject;

import static com.canoo.dp.impl.remoting.legacy.commands.CommandConstants.DELETE_PRESENTATION_MODEL_COMMAND_ID;
import static com.canoo.dp.impl.remoting.legacy.commands.CommandConstants.ID;
import static com.canoo.dp.impl.remoting.legacy.commands.CommandConstants.PM_ID;

public class DeletePresentationModelCommandEncoder extends AbstractCommandTranscoder<DeletePresentationModelCommand> {

    @Override
    public JsonObject encode(DeletePresentationModelCommand command) {
        Assert.requireNonNull(command, "command");
        final JsonObject jsonCommand = new JsonObject();
        jsonCommand.addProperty(ID, DELETE_PRESENTATION_MODEL_COMMAND_ID);
        jsonCommand.addProperty(PM_ID, command.getPmId());
        return jsonCommand;
    }

    @Override
    public DeletePresentationModelCommand decode(JsonObject jsonObject) {
        DeletePresentationModelCommand command = new DeletePresentationModelCommand();
        command.setPmId(getStringElement(jsonObject, PM_ID));
        return command;
    }
}
