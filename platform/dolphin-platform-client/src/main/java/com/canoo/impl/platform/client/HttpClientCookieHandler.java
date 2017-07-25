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
package com.canoo.impl.platform.client;

import com.canoo.dp.impl.platform.core.PlatformConstants;
import com.canoo.platform.core.DolphinRuntimeException;
import com.canoo.dp.impl.platform.core.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientCookieHandler {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientCookieHandler.class);

    private final CookieStore cookieStore;

    public HttpClientCookieHandler(CookieStore cookieStore) {
        this.cookieStore = Assert.requireNonNull(cookieStore, "cookieStore");
    }

    public void updateCookiesFromResponse(final HttpURLConnection conn) throws URISyntaxException {
        LOG.debug("adding cookies from response to cookie store");
        Map<String, List<String>> headerFields = conn.getHeaderFields();
        List<String> cookiesHeader = headerFields.get(PlatformConstants.SET_COOKIE_HEADER);
        if (cookiesHeader != null) {
            LOG.debug("found '{}' header field", PlatformConstants.SET_COOKIE_HEADER);
            for (String cookie : cookiesHeader) {
                if (cookie == null || cookie.isEmpty()) {
                    continue;
                }
                LOG.debug("will parse '{}' header content '{}'", cookie);
                List<HttpCookie> cookies = new ArrayList<>();
                try {
                    cookies.addAll(HttpCookie.parse(cookie));
                } catch (Exception e) {
                    throw new DolphinRuntimeException("Can not convert '" + PlatformConstants.SET_COOKIE_HEADER + "' response header field to http cookies. Bad content: " + cookie, e);
                }
                LOG.debug("Found {} http cookies in header", cookies.size());
                for (HttpCookie httpCookie : cookies) {
                    cookieStore.add(conn.getURL().toURI(), httpCookie);
                }

            }
        }
    }

    public void setRequestCookies(final HttpURLConnection conn) throws URISyntaxException {
        LOG.debug("adding cookies from cookie store to request");
        if (cookieStore.getCookies().size() > 0) {
            String cookieValue = "";
            for (HttpCookie cookie : cookieStore.get(conn.getURL().toURI())) {
                cookieValue = cookieValue + cookie + ";";
            }
            if (!cookieValue.isEmpty()) {
                cookieValue = cookieValue.substring(0, cookieValue.length());
                LOG.debug("Adding '{}' header to request. Content: {}", PlatformConstants.SET_COOKIE_HEADER, cookieValue);
                conn.setRequestProperty(PlatformConstants.COOKIE_HEADER, cookieValue);
            }
        }
    }
}
