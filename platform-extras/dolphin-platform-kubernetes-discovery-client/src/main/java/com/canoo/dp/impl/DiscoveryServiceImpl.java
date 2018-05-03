package com.canoo.dp.impl;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.client.PlatformClient;
import com.canoo.platform.client.discovery.DiscoveryService;
import com.canoo.platform.core.DolphinRuntimeException;
import com.canoo.platform.core.http.HttpClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class DiscoveryServiceImpl implements DiscoveryService {

    private final HttpClient httpClient;

    private final String discoveryServiceEndpoint;

    private Map<String, URL> cache;

    public DiscoveryServiceImpl(final String discoveryServiceEndpoint) {
        this.discoveryServiceEndpoint = Assert.requireNonBlank(discoveryServiceEndpoint, "discoveryServiceEndpoint");
        httpClient = PlatformClient.getService(HttpClient.class);
        Assert.requireNonNull(httpClient, "httpClient");
        cache = new ConcurrentHashMap<>();
    }

    @Override
    public CompletableFuture<URL> getAddress(final String name) {
        final URL cachedURL = cache.get(name);
        if(cachedURL != null) {
            final CompletableFuture<URL> future = new CompletableFuture<>();
            future.complete(cachedURL);
            return future;
        } else {
            return httpClient.get(discoveryServiceEndpoint + "/services/" + name).withoutContent().readString().execute().handle((r, e) -> {
                if (e != null) {
                    throw new DolphinRuntimeException("Can not get address for name " + name, e);
                }
                try {
                    final URL url = new URL(r.getContent());
                    cache.put(name, url);
                    return url;
                } catch (final MalformedURLException e1) {
                    throw new DolphinRuntimeException("Can not get address for name " + name, e);
                }
            });
        }
    }

    @Override
    public CompletableFuture<URL> getAddress(final String namespace, final String name) {
        final URL cachedURL = cache.get(namespace + ":" + name);
        if(cachedURL != null) {
            final CompletableFuture<URL> future = new CompletableFuture<>();
            future.complete(cachedURL);
            return future;
        } else {
            return httpClient.get(discoveryServiceEndpoint + "/services/" + namespace + "/" + name).withoutContent().readString().execute().handle((r, e) -> {
                if (e != null) {
                    throw new DolphinRuntimeException("Can not get address for namespace " + namespace + " and name " + name, e);
                }
                try {
                    final URL url = new URL(r.getContent());
                    cache.put(namespace + ":" + name, url);
                    return url;
                } catch (final MalformedURLException e1) {
                    throw new DolphinRuntimeException("Can not get address for namespace " + namespace + " and name " + name, e);
                }
            });
        }
    }

    @Override
    public void clearCache() {
        cache.clear();
    }
}
