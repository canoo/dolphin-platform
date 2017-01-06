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
package com.canoo.implementation.dolphin.client;

import com.canoo.implementation.dolphin.PlatformConstants;
import com.canoo.implementation.dolphin.util.Assert;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by hendrikebbers on 03.06.16.
 */
public class IdBasedResponseHandler implements ResponseHandler<String> {

    private final DolphinPlatformHttpClientConnector clientConnector;

    private String lastSessionId = null;

    IdBasedResponseHandler(DolphinPlatformHttpClientConnector clientConnector) {
        this.clientConnector = Assert.requireNonNull(clientConnector, "clientConnector");
    }

    @Override
    public String handleResponse(HttpResponse response) throws IOException {
        final StatusLine statusLine = response.getStatusLine();
        final HttpEntity entity = response.getEntity();

        if (statusLine.getStatusCode() == 408) {
            EntityUtils.consume(entity);
            throw new DolphinSessionException("Server can not handle Dolphin Client ID");
        }

        if (statusLine.getStatusCode() >= 300) {
            EntityUtils.consume(entity);
            throw new HttpResponseException(statusLine.getStatusCode(),
                    statusLine.getReasonPhrase());
        }

        try {
            final Header dolphinHeader = response.getFirstHeader(PlatformConstants.CLIENT_ID_HTTP_HEADER_NAME);
            clientConnector.setClientId(dolphinHeader.getValue());
        } catch (Exception e) {
            throw new DolphinSessionException("Error in handling Dolphin Client ID", e);
        }

        Header cookieHeader = response.getFirstHeader("Set-Cookie");
        if (cookieHeader != null) {
            if (lastSessionId != null) {
                throw new DolphinSessionException("Http session must not change but did. Old: " + lastSessionId + ", new: " + cookieHeader.getValue());
            }
            lastSessionId = cookieHeader.getValue();
        }
        return entity == null ? null : EntityUtils.toString(entity);
    }
}