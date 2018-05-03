package com.canoo.dp.impl;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.client.PlatformClient;
import com.canoo.platform.client.discovery.DiscoveryService;
import com.canoo.platform.client.http.url.spi.URLStreamHandlerProvider;
import com.canoo.platform.core.PlatformConfiguration;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Collections;
import java.util.List;

public class KubernetesURLStreamHandlerFactory implements URLStreamHandlerProvider {

    private final String KUBERNETES_PROTOCOL = "service";

    private final DiscoveryService discoveryService;

    public KubernetesURLStreamHandlerFactory() {
        discoveryService = PlatformClient.getService(DiscoveryService.class);
        Assert.requireNonNull(discoveryService, "discoveryService");
    }

    @Override
    public void init(final PlatformConfiguration configuration) {

    }

    @Override
    public List<String> getSupportedProtocols() {
        return Collections.singletonList(KUBERNETES_PROTOCOL);
    }

    @Override
    public URLStreamHandler createURLStreamHandler(final String protocol) {
        if(KUBERNETES_PROTOCOL.equals(protocol)) {
            new URLStreamHandler(){

                @Override
                protected URLConnection openConnection(final URL url) throws IOException {
                    Assert.requireNonNull(url, "url");
                    final String serviceName = url.getHost();
                    if(serviceName.contains(":")) {
                        final String namespace = serviceName.split(":")[0];
                        final String name = serviceName.split(":")[1];
                        try {
                            return discoveryService.getAddress(namespace, name).get().openConnection();
                        } catch (final Exception e) {
                            throw new IOException("Can not convert service URL", e);
                        }
                    } else {
                        final String name = serviceName;
                        try {
                            return discoveryService.getAddress(name).get().openConnection();
                        } catch (final Exception e) {
                            throw new IOException("Can not convert service URL", e);
                        }
                    }
                }
            };
        }
        return null;
    }


}
