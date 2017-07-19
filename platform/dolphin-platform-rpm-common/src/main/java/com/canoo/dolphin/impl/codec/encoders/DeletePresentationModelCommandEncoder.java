package com.canoo.dolphin.impl.codec.encoders;

import com.canoo.impl.platform.core.Assert;
import com.google.gson.JsonObject;
import org.opendolphin.core.comm.DeletePresentationModelCommand;

import static org.opendolphin.core.comm.CommandConstants.*;

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
