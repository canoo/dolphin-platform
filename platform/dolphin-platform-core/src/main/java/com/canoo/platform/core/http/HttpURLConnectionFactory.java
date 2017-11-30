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
package com.canoo.platform.core.http;

import org.apiguardian.api.API;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Factory interface to create a url connection
 */
@API(since = "0.x", status = EXPERIMENTAL)
@FunctionalInterface
public interface HttpURLConnectionFactory {

    /**
     * Creates a url connection for the given url
     * @param url the url
     * @return the connection
     * @throws IOException if the connection can not be created
     */
    HttpURLConnection create(URL url) throws IOException;

}
