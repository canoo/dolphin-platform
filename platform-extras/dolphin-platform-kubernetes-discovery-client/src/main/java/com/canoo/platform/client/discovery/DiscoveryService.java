package com.canoo.platform.client.discovery;

import java.net.URL;
import java.util.concurrent.CompletableFuture;

public interface DiscoveryService {

    CompletableFuture<URL> getAddress(String name);

    CompletableFuture<URL> getAddress(String namespace, String name);

    void clearCache();
}
