package com.canoo.dp.impl;

import com.canoo.dp.impl.platform.client.AbstractServiceProvider;
import com.canoo.platform.client.ClientConfiguration;
import com.canoo.platform.client.discovery.DiscoveryService;

public class DiscoveryServiceProvider extends AbstractServiceProvider<DiscoveryService> {

    private final static String DISCOVERY_ENDPOINT = "kubernetes.discovery.endpoint";

    public DiscoveryServiceProvider() {
        super(DiscoveryService.class);
    }

    @Override
    protected DiscoveryService createService(final ClientConfiguration configuration) {
        return new DiscoveryServiceImpl(configuration.getProperty(DISCOVERY_ENDPOINT));
    }
}
