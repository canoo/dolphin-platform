package com.canoo.dp.impl.platform.client.http.url;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.client.http.url.spi.URLStreamHandlerProvider;
import com.canoo.platform.core.DolphinRuntimeException;
import com.canoo.platform.core.PlatformConfiguration;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

public class ExtendableURLStreamHandlerFactory implements URLStreamHandlerFactory {

    private final PlatformConfiguration configuration;

    private final List<URLStreamHandlerProvider> providers;

    public ExtendableURLStreamHandlerFactory(final PlatformConfiguration configuration) {
        this.configuration = Assert.requireNonNull(configuration, "configuration");

        providers = new ArrayList<>();
        final List<String> supportedProtocols = new ArrayList<>();
        final ServiceLoader<URLStreamHandlerProvider> serviceLoader = ServiceLoader.load(URLStreamHandlerProvider.class);
        final Iterator<URLStreamHandlerProvider> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            final URLStreamHandlerProvider provider = iterator.next();
            final List<String> currentProtocols = provider.getSupportedProtocols();
            currentProtocols.stream()
                    .filter(p -> supportedProtocols.contains(p))
                    .findAny()
                    .ifPresent(p -> {throw new DolphinRuntimeException("URL protocol already registered: " + p);});
            provider.init(configuration);
            supportedProtocols.addAll(currentProtocols);
            providers.add(provider);
        }

    }

    @Override
    public URLStreamHandler createURLStreamHandler(final String protocol) {
        return providers.stream()
                .filter(p -> p.getSupportedProtocols().contains(protocol))
                .map(p -> p.createURLStreamHandler(protocol))
                .findAny()
                .orElse(null);
    }
}
