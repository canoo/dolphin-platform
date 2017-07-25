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

import com.canoo.dolphin.impl.codec.encoders.CallActionCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.CommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.CreateContextCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.CreateControllerCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.CreatePresentationModelEncoder;
import com.canoo.dolphin.impl.codec.encoders.DestroyContextCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.DestroyControllerCommandEncoder;
import com.canoo.dolphin.impl.codec.encoders.ValueChangedCommandEncoder;
import com.canoo.dolphin.impl.commands.CallActionCommand;
import com.canoo.dolphin.impl.commands.CreateContextCommand;
import com.canoo.dolphin.impl.commands.CreateControllerCommand;
import com.canoo.dolphin.impl.commands.DestroyContextCommand;
import com.canoo.dolphin.impl.commands.DestroyControllerCommand;
import com.canoo.dp.impl.platform.core.Assert;
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
import org.opendolphin.core.comm.CreatePresentationModelCommand;
import org.opendolphin.core.comm.JsonCodec;
import org.opendolphin.core.comm.ValueChangedCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.canoo.dolphin.impl.codec.CommandConstants.*;

public class OptimizedJsonCodec implements Codec {

    private static final Logger LOG = LoggerFactory.getLogger(OptimizedJsonCodec.class);

    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    private static final Map<Class<? extends Command>, CommandEncoder<?>> ENCODERS = new HashMap<>();
    private static final Map<String, CommandEncoder<?>> DECODERS = new HashMap<>();

    static {
        final CreatePresentationModelEncoder createPresentationModelEncoder = new CreatePresentationModelEncoder();
        ENCODERS.put(CreatePresentationModelCommand.class, createPresentationModelEncoder);
        DECODERS.put(CREATE_PRESENTATION_MODEL_COMMAND_ID, createPresentationModelEncoder);

        final ValueChangedCommandEncoder valueChangedCommandEncoder = new ValueChangedCommandEncoder();
        ENCODERS.put(ValueChangedCommand.class, valueChangedCommandEncoder);
        DECODERS.put(VALUE_CHANGED_COMMAND_ID, valueChangedCommandEncoder);

        final CreateControllerCommandEncoder createControllerCommandEncoder = new CreateControllerCommandEncoder();
        ENCODERS.put(CreateControllerCommand.class, createControllerCommandEncoder);
        DECODERS.put(CREATE_CONTROLLER_COMMAND_ID, createControllerCommandEncoder);

        final DestroyControllerCommandEncoder destroyControllerCommandEncoder = new DestroyControllerCommandEncoder();
        ENCODERS.put(DestroyControllerCommand.class, destroyControllerCommandEncoder);
        DECODERS.put(DESTROY_CONTROLLER_COMMAND_ID, destroyControllerCommandEncoder);

        final CallActionCommandEncoder callActionCommandEncoder = new CallActionCommandEncoder();
        ENCODERS.put(CallActionCommand.class, callActionCommandEncoder);
        DECODERS.put(CALL_ACTION_COMMAND_ID, callActionCommandEncoder);

        final CreateContextCommandEncoder createContextCommandEncoder = new CreateContextCommandEncoder();
        ENCODERS.put(CreateContextCommand.class, createContextCommandEncoder);
        DECODERS.put(CREATE_CONTEXT_COMMAND_ID, createContextCommandEncoder);

        final DestroyContextCommandEncoder destroyContextCommandEncoder = new DestroyContextCommandEncoder();
        ENCODERS.put(DestroyContextCommand.class, destroyContextCommandEncoder);
        DECODERS.put(DESTROY_CONTEXT_COMMAND_ID, destroyContextCommandEncoder);
    }

    private final Codec fallBack = new JsonCodec();

    @Override
    @SuppressWarnings("unchecked")
    public String encode(List<? extends Command> commands) {
        Assert.requireNonNull(commands, "commands");
        LOG.trace("Encoding command list with {} commands", commands.size());
        final StringBuilder builder = new StringBuilder("[");
        for (final Command command : commands) {
            if (command == null) {
                throw new IllegalArgumentException("Command list contains a null command: " + command);
            } else {
                LOG.trace("Encoding command of type {}", command.getClass());
                final CommandEncoder encoder = ENCODERS.get(command.getClass());
                if (encoder != null) {
                    final JsonObject jsonObject = encoder.encode(command);
                    GSON.toJson(jsonObject, builder);
                } else {
                    final String result = fallBack.encode(Collections.singletonList(command));
                    builder.append(result.substring(1, result.length() - 1));
                }
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
    public List<Command> decode(String transmitted) {
        Assert.requireNonNull(transmitted, "transmitted");
        LOG.trace("Decoding message: {}", transmitted);
        try {
            final List<Command> commands = new ArrayList<>();
            final JsonArray array = (JsonArray) new JsonParser().parse(transmitted);

            for (final JsonElement jsonElement : array) {
                final JsonObject command = (JsonObject) jsonElement;
                JsonPrimitive idPrimitive = command.getAsJsonPrimitive("id");

                String id = null;
                if (idPrimitive != null) {
                    id = idPrimitive.getAsString();
                }
                LOG.trace("Decoding command: {}", id);
                CommandEncoder<?> encoder = null;
                if (id != null) {
                    encoder = DECODERS.get(id);
                }
                if (encoder != null) {
                    commands.add(encoder.decode(command));
                } else {
                    commands.addAll(fallBack.decode("[" + command.toString() + "]"));
                }
            }
            LOG.trace("Decoded command list with {} commands", commands.size());
            return commands;
        } catch (ClassCastException | NullPointerException ex) {
            throw new JsonParseException("Illegal JSON detected", ex);
        }
    }

}
