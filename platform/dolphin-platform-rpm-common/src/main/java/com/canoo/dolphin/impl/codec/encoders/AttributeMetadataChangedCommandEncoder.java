package com.canoo.dolphin.impl.codec.encoders;

import com.canoo.impl.platform.core.Assert;
import com.google.gson.JsonObject;
import org.opendolphin.core.comm.AttributeMetadataChangedCommand;

import static org.opendolphin.core.comm.CommandConstants.*;

public class AttributeMetadataChangedCommandEncoder extends AbstractCommandTranscoder<AttributeMetadataChangedCommand> {

    @Override
    public JsonObject encode(AttributeMetadataChangedCommand command) {
        Assert.requireNonNull(command, "command");
        final JsonObject jsonCommand = new JsonObject();
        jsonCommand.addProperty(ID, ATTRIBUTE_METADATA_CHANGED_COMMAND_ID);
        jsonCommand.addProperty(ATTRIBUTE_ID, command.getAttributeId());
        jsonCommand.addProperty(NAME, command.getMetadataName());
        jsonCommand.add(VALUE, ValueEncoder.encodeValue(command.getValue()));
        return jsonCommand;
    }

    @Override
    public AttributeMetadataChangedCommand decode(JsonObject jsonObject) {
        AttributeMetadataChangedCommand command = new AttributeMetadataChangedCommand();
        command.setAttributeId(getStringElement(jsonObject, ATTRIBUTE_ID));
        command.setMetadataName(getStringElement(jsonObject, NAME));
        command.setValue(ValueEncoder.decodeValue(jsonObject.get(VALUE)));
        return command;
    }
}
