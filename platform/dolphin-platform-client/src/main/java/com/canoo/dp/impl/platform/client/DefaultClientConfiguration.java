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
package com.canoo.dp.impl.platform.client;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.platform.core.SimpleDolphinPlatformThreadFactory;
import com.canoo.dp.impl.platform.core.http.DefaultHttpURLConnectionFactory;
import com.canoo.platform.client.ClientConfiguration;
import com.canoo.platform.core.http.HttpURLConnectionFactory;
import org.apiguardian.api.API;

import java.net.CookieManager;
import java.net.CookieStore;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.20", status = INTERNAL)
public class DefaultClientConfiguration implements ClientConfiguration {

    private final Properties internalProperties = new Properties();

    public DefaultClientConfiguration() {
        setBackgroundExecutor(Executors.newCachedThreadPool(new SimpleDolphinPlatformThreadFactory()));
        setCookieStore(new CookieManager().getCookieStore());
        setHttpURLConnectionFactory(new DefaultHttpURLConnectionFactory());
    }

    public DefaultClientConfiguration(Properties prop) {
        this();
        Assert.requireNonNull(prop, "prop");
        internalProperties.putAll(prop);
    }

    @Override
    public <T> T getObjectProperty(final String key) {
        return (T) internalProperties.get(key);
    }

    @Override
    public <T> T getObjectProperty(final String key, final T defaultValue) {
        if (containsProperty(key)) {
            return getObjectProperty(key);
        } else {
            return defaultValue;
        }
    }

    @Override
    public <T> void setObjectProperty(final String key, final T value) {
        internalProperties.put(key, value);
    }

    @Override
    public boolean containsProperty(final String key) {
        return internalProperties.containsKey(key);
    }

    @Override
    public Set<String> getPropertyKeys() {
        return internalProperties.stringPropertyNames();
    }

    @Override
    public Executor getUiExecutor() {
        return getObjectProperty(UI_EXECUTOR);
    }

    @Override
    public ExecutorService getBackgroundExecutor() {
        return getObjectProperty(BACKGROUND_EXECUTOR);
    }

    @Override
    public CookieStore getCookieStore() {
        return getObjectProperty(COOKIE_STORE);
    }

    @Override
    public HttpURLConnectionFactory getHttpURLConnectionFactory() {
        return getObjectProperty(CONNECTION_FACTORY);
    }

    @Override
    public void setUiExecutor(final Executor executor) {
        internalProperties.put(UI_EXECUTOR, executor);
    }

    @Override
    public void setBackgroundExecutor(final ExecutorService service) {
        internalProperties.put(BACKGROUND_EXECUTOR, service);
    }

    @Override
    public void setHttpURLConnectionFactory(HttpURLConnectionFactory httpURLConnectionFactory) {
        setObjectProperty(CONNECTION_FACTORY, httpURLConnectionFactory);
    }

    @Override
    public void setCookieStore(final CookieStore cookieStore) {
        internalProperties.put(COOKIE_STORE, cookieStore);
    }

    @Override
    public void setStringProperty(final String key, final String value) {
        setObjectProperty(key, value);
    }

    @Override
    public void setBooleanProperty(final String key, final boolean value) {
        setObjectProperty(key, value);
    }

    @Override
    public void setIntProperty(final String key, final int value) {
        setObjectProperty(key, value);
    }

    @Override
    public void setLongProperty(final String key, final long value) {
        setObjectProperty(key, value);
    }

    @Override
    public void setListProperty(final String key, final List<String> value) {
        setObjectProperty(key, value);
    }

    @Override
    public String getProperty(final String key, final String defaultValue) {
        return getObjectProperty(key, defaultValue);
    }

    @Override
    public String getProperty(final String key) {
        return getObjectProperty(key);
    }

    @Override
    public boolean getBooleanProperty(final String key, final boolean defaultValue) {
        return getObjectProperty(key, defaultValue);
    }

    @Override
    public int getIntProperty(final String key, final int defaultValue) {
        return getObjectProperty(key, defaultValue);
    }

    @Override
    public long getLongProperty(final String key, final long defaultValue) {
        return getObjectProperty(key, defaultValue);
    }

    @Override
    public List<String> getListProperty(final String key, final List<String> defaultValues) {
        return getObjectProperty(key, defaultValues);
    }
}
