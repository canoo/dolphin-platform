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
package com.canoo.platform.remoting;

/**
 * This exception is thrown if an error occurs in the protocol (request / response body)
 */
public class DolphinRemotingException extends Exception {

    private static final long serialVersionUID = 1934440187016337212L;

    public DolphinRemotingException(String message, Throwable cause) {
        super(message, cause);
    }

}
