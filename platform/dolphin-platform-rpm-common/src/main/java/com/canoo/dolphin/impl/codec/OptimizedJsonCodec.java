/*
 * Copyright 2015-2017 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dolphin.impl.codec;

import com.canoo.dolphin.impl.codec.encoders.AbstractCommandTranscoder;
import com.canoo.dolphin.impl.codec.encoders.AttributeMetadataChangedCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.CallActionCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.ChangeAttributeMetadataCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.CommandTranscoder;
import com.canoo.dolphin.impl.codec.encoders.CreateContextCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.CreateControllerCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.CreatePresentationModelCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.DeletePresentationModelCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.DestroyContextCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.DestroyControllerCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.EmptyCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.InterruptLongPollCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.PresentationModelDeletedCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.StartLongPollCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.ValueChangedCommandEncoder;
import com.canoo.impl.platform.core.Assert;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.opendolphin.core.comm.Codec;
import org.opendolphin.core.comm.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.opendolphin.core.comm.CommandConstants.*;

public class OptimizedJsonCodec implements Codec {

    private static final Logger LOG = LoggerFactory.getLogger(OptimizedJsonCodec.class);

    private static final OptimizedJsonCodec INSTANCE = new OptimizedJsonCodec();

    private final Gson GSON;

    private final Map<String, CommandTranscoder<?>> transcoders = new HashMap<>();

    private OptimizedJsonCodec() {
        GSON = new GsonBuilder().serializeNulls().create();

        addTranscoder(new StartLongPollCommandEncoder(), START_LONG_POLL_COMMAND_ID);
        addTranscoder(new InterruptLongPollCommandEncoder(), INTERRUPT_LONG_POLL_COMMAND_ID);
        addTranscoder(new CreatePresentationModelCommandEncoder(), CREATE_PRESENTATION_MODEL_COMMAND_ID);
        addTranscoder(new DeletePresentationModelCommandEncoder(), DELETE_PRESENTATION_MODEL_COMMAND_ID);
        addTranscoder(new PresentationModelDeletedCommandEncoder(), PRESENTATION_MODEL_DELETED_COMMAND_ID);
        addTranscoder(new ValueChangedCommandEncoder(), VALUE_CHANGED_COMMAND_ID);
        addTranscoder(new ChangeAttributeMetadataCommandEncoder(), CHANGE_ATTRIBUTE_METADATA_COMMAND_ID);
        addTranscoder(new AttributeMetadataChangedCommandEncoder(), ATTRIBUTE_METADATA_CHANGED_COMMAND_ID);
        addTranscoder(new EmptyCommandEncoder(), EMPTY_COMMAND_ID);
        addTranscoder(new CreateContextCommandEncoder(), CREATE_CONTEXT_COMMAND_ID);
        addTranscoder(new DestroyContextCommandEncoder(), DESTROY_CONTEXT_COMMAND_ID);
        addTranscoder(new CreateControllerCommandEncoder(), CREATE_CONTROLLER_COMMAND_ID);
        addTranscoder(new DestroyControllerCommandEncoder(), DESTROY_CONTROLLER_COMMAND_ID);
        addTranscoder(new CallActionCommandEncoder(), CALL_ACTION_COMMAND_ID);
    }

    private <C extends Command> void addTranscoder(final AbstractCommandTranscoder<C> transcoder, final String commandId) {
        Assert.requireNonNull(transcoder, "transcoder");
        Assert.requireNonNull(commandId, "commandId");

        if(transcoders.containsKey(commandId)) {
            throw new IllegalStateException("Transcoder for " + commandId + " already defined!");
        }
        transcoders.put(commandId, transcoder);
    }

    @Override
    @SuppressWarnings("unchecked")
    public String encode(final List<? extends Command> commands) {
        Assert.requireNonNull(commands, "commands");
        LOG.trace("Encoding command list with {} commands", commands.size());
        final StringBuilder builder = new StringBuilder("[");
        for (final Command command : commands) {
            if (command == null) {
                throw new IllegalArgumentException("Command list contains a null command: " + command);
            } else {
                LOG.trace("Encoding command of type {}", command.getClass());
                final CommandTranscoder encoder = transcoders.get(command.getId());
                if (encoder == null) {
                    throw new RuntimeException("No encoder for command type " + command.getClass() + " found");
                }
                final JsonObject jsonObject = encoder.encode(command);
                GSON.toJson(jsonObject, builder);
                builder.append(",");
            }
        }
        if (!commands.isEmpty()) {
            final int length = builder.length();
            builder.delete(length - 1, length);
        }
        builder.append("]");
        if (LOG.isTraceEnabled()) {
            LOG.trace("Encoded message: {}", builder.toString());
        }
        return builder.toString();
    }

    @Override
    public List<Command> decode(final String transmitted) {
        Assert.requireNonNull(transmitted, "transmitted");
        LOG.trace("Decoding message: {}", transmitted);
        try {
            final List<Command> commands = new ArrayList<>();
            final JsonArray array = (JsonArray) new JsonParser().parse(transmitted);
            for (final JsonElement jsonElement : array) {
                final JsonObject command = (JsonObject) jsonElement;
                final JsonPrimitive idElement = command.getAsJsonPrimitive("id");
                if (idElement == null) {
                    throw new RuntimeException("Can not encode command without id!");
                }
                String id = idElement.getAsString();
                LOG.trace("Decoding command: {}", id);
                final CommandTranscoder<?> encoder = transcoders.get(id);
                if (encoder == null) {
                    throw new RuntimeException("Can not encode command of type " + id + ". No matching encoder found!");
                }
                commands.add(encoder.decode(command));
            }
            LOG.trace("Decoded command list with {} commands", commands.size());
            return commands;
        } catch (Exception ex) {
            throw new JsonParseException("Illegal JSON detected", ex);
        }
    }

    public static OptimizedJsonCodec getInstance() {
        return INSTANCE;
    }
}
