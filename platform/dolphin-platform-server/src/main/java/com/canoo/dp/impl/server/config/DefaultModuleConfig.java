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
package com.canoo.dp.impl.server.config;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.server.spi.PlatformConfiguration;

import java.util.Arrays;
import java.util.List;

public class DefaultModuleConfig {

    public static final String USE_CROSS_SITE_ORIGIN_FILTER = "useCrossSiteOriginFilter";

    public static final String ACCESS_CONTROL_ALLOW_HEADERS = "accessControlAllowHeaders";

    public static final String ACCESS_CONTROL_ALLOW_METHODS = "accessControlAllowMethods";

    public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "accessControlAllowCredentials";

    public static final String ACCESS_CONTROL_MAXAGE = "accessControlMaxAge";

    public static final String SESSION_TIMEOUT = "sessionTimeout";

    public static final String ROOT_PACKAGE_FOR_CLASSPATH_SCAN = "rootPackageForClasspathScan";

    public static final String MBEAN_REGISTRATION = "mBeanRegistration";

    public static final String PLATFORM_ACTIVE = "active";

    public static final String MAX_CLIENTS_PER_SESSION = "maxClientsPerSession";

    public static final String ID_FILTER_URL_MAPPINGS = "idFilterUrlMappings";

    public static final String CORS_ENDPOINTS_URL_MAPPINGS = "corsUrlMappings";

    public final static int SESSION_TIMEOUT_DEFAULT_VALUE = 900;

    public final static boolean USE_CROSS_SITE_ORIGIN_FILTER_DEFAULT_VALUE = true;

    public final static boolean M_BEAN_REGISTRATION_DEFAULT_VALUE = false;

    public final static String ROOT_PACKAGE_FOR_CLASSPATH_SCAN_DEFAULT_VALUE = null;

    public final static boolean ACTIVE_DEFAULT_VALUE = true;

    public final static List<String> ACCESS_CONTROL_ALLOW_HEADERS_DEFAULT_VALUE = Arrays.asList("Content-Type", "x-requested-with", "origin", "authorization", "accept", "client-security-token");

    public final static List<String> ACCESS_CONTROL_ALLOW_METHODS_DEFAULT_VALUE = Arrays.asList("*");

    public final static boolean ACCESS_CONTROL_ALLOW_CREDENTIALS_DEFAULT_VALUE = true;

    public final static long ACCESS_CONTROL_MAX_AGE_DEFAULT_VALUE = 86400;

    public final static int MAX_CLIENTS_PER_SESSION_DEFAULT_VALUE = 10;

    public final static List<String> ID_FILTER_URL_MAPPINGS_DEFAULT_VALUE = Arrays.asList("/dolphin");

    public final static List<String> CORS_ENDPOINTS_URL_MAPPINGS_DEFAULT_VALUE = Arrays.asList("/*");

    public static int getSessionTimeout(final PlatformConfiguration configuration) {
        return Assert.requireNonNull(configuration, "configuration").getIntProperty(SESSION_TIMEOUT, SESSION_TIMEOUT_DEFAULT_VALUE);
    }

    public static boolean isUseCrossSiteOriginFilter(final PlatformConfiguration configuration) {
        return Assert.requireNonNull(configuration, "configuration").getBooleanProperty(USE_CROSS_SITE_ORIGIN_FILTER, USE_CROSS_SITE_ORIGIN_FILTER_DEFAULT_VALUE);
    }

    public static boolean isMBeanRegistration(final PlatformConfiguration configuration) {
        return Assert.requireNonNull(configuration, "configuration").getBooleanProperty(MBEAN_REGISTRATION, M_BEAN_REGISTRATION_DEFAULT_VALUE);
    }

    public static String getRootPackageForClasspathScan(final PlatformConfiguration configuration) {
        return Assert.requireNonNull(configuration, "configuration").getProperty(ROOT_PACKAGE_FOR_CLASSPATH_SCAN, ROOT_PACKAGE_FOR_CLASSPATH_SCAN_DEFAULT_VALUE);
    }

    public static List<String> getAccessControlAllowHeaders(final PlatformConfiguration configuration) {
        return Assert.requireNonNull(configuration, "configuration").getListProperty(ACCESS_CONTROL_ALLOW_HEADERS, ACCESS_CONTROL_ALLOW_HEADERS_DEFAULT_VALUE);
    }

    public static List<String> getAccessControlAllowMethods(final PlatformConfiguration configuration) {
        return Assert.requireNonNull(configuration, "configuration").getListProperty(ACCESS_CONTROL_ALLOW_METHODS, ACCESS_CONTROL_ALLOW_METHODS_DEFAULT_VALUE);
    }

    public static boolean isAccessControlAllowCredentials(final PlatformConfiguration configuration) {
        return Assert.requireNonNull(configuration, "configuration").getBooleanProperty(ACCESS_CONTROL_ALLOW_CREDENTIALS, ACCESS_CONTROL_ALLOW_CREDENTIALS_DEFAULT_VALUE);
    }

    public static long getAccessControlMaxAge(final PlatformConfiguration configuration) {
        return Assert.requireNonNull(configuration, "configuration").getLongProperty(ACCESS_CONTROL_MAXAGE, ACCESS_CONTROL_MAX_AGE_DEFAULT_VALUE);
    }

    public static boolean isActive(final PlatformConfiguration configuration) {
        return Assert.requireNonNull(configuration, "configuration").getBooleanProperty(PLATFORM_ACTIVE, ACTIVE_DEFAULT_VALUE);
    }

    public static int getMaxClientsPerSession(final PlatformConfiguration configuration) {
        return Assert.requireNonNull(configuration, "configuration").getIntProperty(MAX_CLIENTS_PER_SESSION, MAX_CLIENTS_PER_SESSION_DEFAULT_VALUE);
    }

    public static List<String> getIdFilterUrlMappings(final PlatformConfiguration configuration) {
        return Assert.requireNonNull(configuration, "configuration").getListProperty(ID_FILTER_URL_MAPPINGS, ID_FILTER_URL_MAPPINGS_DEFAULT_VALUE);
    }

    public static List<String> getCorsEndpoints(final PlatformConfiguration configuration) {
        return Assert.requireNonNull(configuration, "configuration").getListProperty(CORS_ENDPOINTS_URL_MAPPINGS, CORS_ENDPOINTS_URL_MAPPINGS_DEFAULT_VALUE);
    }
}
