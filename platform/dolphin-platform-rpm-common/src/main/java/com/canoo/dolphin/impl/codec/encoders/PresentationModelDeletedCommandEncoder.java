package com.canoo.dolphin.impl.codec.encoders;

import com.canoo.impl.platform.core.Assert;
import com.google.gson.JsonObject;
import org.opendolphin.core.comm.PresentationModelDeletedCommand;

import static org.opendolphin.core.comm.CommandConstants.*;

@Deprecated
public class PresentationModelDeletedCommandEncoder extends AbstractCommandTranscoder<PresentationModelDeletedCommand> {

    @Override
    public JsonObject encode(PresentationModelDeletedCommand command) {
        Assert.requireNonNull(command, "command");
        final JsonObject jsonCommand = new JsonObject();
        jsonCommand.addProperty(ID, PRESENTATION_MODEL_DELETED_COMMAND_ID);
        jsonCommand.addProperty(PM_ID, command.getPmId());
        return jsonCommand;
    }

    @Override
    public PresentationModelDeletedCommand decode(JsonObject jsonObject) {
        PresentationModelDeletedCommand command = new PresentationModelDeletedCommand();
        command.setPmId(getStringElement(jsonObject, PM_ID));
        return command;
    }
}
