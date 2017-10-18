package com.canoo.platform.client;

import com.canoo.dp.impl.platform.client.DefaultClientConfiguration;
import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.client.spi.ServiceProvider;
import com.canoo.platform.core.DolphinRuntimeException;
import org.apiguardian.api.API;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(since = "0.19.0", status = EXPERIMENTAL)
public class PlatformClient {

    private static PlatformClient INSTANCE;

    private final Map<Class, ServiceProvider> providers = new HashMap<>();

    private final Map<Class, Object> services = new HashMap<>();

    private final ClientConfiguration clientConfiguration;

    private PlatformClient() {
        this.clientConfiguration = new DefaultClientConfiguration();

        final ServiceLoader<ServiceProvider> loader = ServiceLoader.load(ServiceProvider.class);
        final Iterator<ServiceProvider> iterator = loader.iterator();
        while (iterator.hasNext()) {
            ServiceProvider provider = iterator.next();
            Class serviceClass = provider.getServiceType();
            Assert.requireNonNull(serviceClass, "serviceClass");
            if (providers.containsKey(serviceClass)) {
                throw new DolphinRuntimeException("Can not register more than 1 implementation for service type " + serviceClass);
            }
            providers.put(serviceClass, provider);
        }
        initImpl(new HeadlessToolkit());
    }

    private void initImpl(Toolkit toolkit) {
        services.clear();
        Assert.requireNonNull(toolkit, "toolkit");
        clientConfiguration.setUiExecutor(toolkit.getUiExecutor());
    }

    private <S> boolean hasServiceImpl(final Class<S> serviceClass) {
        Assert.requireNonNull(serviceClass, "serviceClass");
        return providers.containsKey(serviceClass);
    }

    private <S> S getServiceImpl(final Class<S> serviceClass) {
        Assert.requireNonNull(serviceClass, "serviceClass");
        if(services.containsKey(serviceClass)) {
            final S service = (S) services.get(serviceClass);
            return service;
        }
        final ServiceProvider<S> serviceProvider = providers.get(serviceClass);
        Assert.requireNonNull(serviceProvider, "serviceProvider");
        final S service = serviceProvider.getService(clientConfiguration);
        Assert.requireNonNull(service, "service");
        services.put(serviceClass, service);
        return service;
    }

    public static void init(Toolkit toolkit) {
        getInstance().initImpl(toolkit);
    }

    public static ClientConfiguration getClientConfiguration() {
        return getInstance().clientConfiguration;
    }

    public static <S> boolean hasService(final Class<S> serviceClass) {
        return getInstance().hasServiceImpl(serviceClass);
    }

    public static <S> S getService(final Class<S> serviceClass) {
        return getInstance().getServiceImpl(serviceClass);
    }

    private static synchronized PlatformClient getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new PlatformClient();
        }
        return INSTANCE;
    }
}
