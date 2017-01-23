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
package com.canoo.remoting.server;

import java.util.Arrays;
import java.util.List;

/**
 * A DTO is a <a href="http://en.wikipedia.org/wiki/Data_transfer_object">data transfer object</a>, used to
 * transfer the contents of a presentation model in a single Dolphin data command.
 * <p>
 * A DTO consists of a list of Slot objects, where each slot corresponds to an attribute in the presentation model.
 * A slot consists of a propertyName (String), a value, a baseValue, a qualifier, and a tag.
 *
 * @see Slot
 */
public final class DTO {

    private List<Slot> slots;

    public DTO(List<Slot> newSlots) {
        slots = newSlots;
    }

    public DTO(Slot... newSlots) {
        this(Arrays.asList(newSlots));
    }

    public List<Slot> getSlots() {
        return slots;
    }
}
