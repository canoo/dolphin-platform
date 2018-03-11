/*
 * Copyright 2015-2018 Canoo Engineering AG.
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
package com.canoo.dp.impl.server.security.spring;

import com.canoo.dp.impl.platform.core.Assert;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.util.function.Supplier;

import static com.canoo.dp.impl.security.SecurityHttpHeader.AUTHORIZATION_HEADER;
import static com.canoo.dp.impl.security.SecurityHttpHeader.BEARER;

public class SecurityClientRequestFactory extends HttpComponentsClientHttpRequestFactory implements ClientHttpRequestFactory {

    private final Supplier<String> securityTokenSupplier;

    public SecurityClientRequestFactory(final Supplier<String> securityTokenSupplier) {
        super(HttpClients.custom()
                .disableCookieManagement()
                .build()
        );
        this.securityTokenSupplier = Assert.requireNonNull(securityTokenSupplier, "securityTokenSupplier");
    }

    @Override
    protected void postProcessHttpRequest(HttpUriRequest request) {
        final String token = securityTokenSupplier.get();
        request.setHeader(AUTHORIZATION_HEADER, BEARER + token);
    }
}
