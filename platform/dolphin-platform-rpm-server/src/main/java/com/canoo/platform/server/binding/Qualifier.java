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
package com.canoo.platform.server.binding;

import com.canoo.dp.impl.platform.core.Assert;

import java.util.UUID;

/**
 * A qualifier to define a server site binding of properties (see {@link com.canoo.dolphin.mapping.Property}).
 *
 * @param <T> generic type of the property that can be bound by using the qualifier
 * @see PropertyBinder
 */
public final class Qualifier<T> {

    private final String identifier;

    public Qualifier(final String identifier) {
        this.identifier = Assert.requireNonNull(identifier, "identifier");
    }

    public String getIdentifier() {
        return identifier;
    }

    public static <T> Qualifier<T> create() {
        return new Qualifier<>(UUID.randomUUID().toString());
    }

    public static <T> Qualifier<T> create(String identifier) {
        return new Qualifier<>(identifier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Qualifier<?> qualifier = (Qualifier<?>) o;

        return identifier != null ? identifier.equals(qualifier.identifier) : qualifier.identifier == null;

    }

    @Override
    public int hashCode() {
        return identifier != null ? identifier.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Qualifier: " + identifier;
    }
}
