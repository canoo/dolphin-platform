/*
 * Copyright 2015-2016 Canoo Engineering AG.
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
package com.canoo.implementation.dolphin.server.context;

/**
 * This exception defines an error in the handling of dolphin commands
 */
public class DolphinCommandException extends RuntimeException {

    private static final long serialVersionUID = -1936119062569456362L;

    public DolphinCommandException() {
    }

    public DolphinCommandException(String message) {
        super(message);
    }

    public DolphinCommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public DolphinCommandException(Throwable cause) {
        super(cause);
    }
}
