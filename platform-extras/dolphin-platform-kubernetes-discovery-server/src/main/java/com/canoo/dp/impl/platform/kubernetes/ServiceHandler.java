package com.canoo.dp.impl.platform.kubernetes;

import com.canoo.dp.impl.platform.core.Assert;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;

@ApplicationScope
@Component
public class ServiceHandler {

    private final static int MIN_PORT_NUMBER = 1;

    private final KubernetesClient client;

    @Autowired
    public ServiceHandler(final KubernetesClient client) {
        this.client = Assert.requireNonNull(client, "client");
    }

    private Service getService(final String namespace, final String name) {
        Assert.requireNonBlank(namespace, "namespace");
        Assert.requireNonBlank(name, "name");
        return client.services().inNamespace(namespace).withName(name).get();
    }

    private Service getService(final String name) {
        Assert.requireNonBlank(name, "name");
        return client.services().withName(name).get();
    }

    public String getAddress(final String namespace, final String name) {
        return getAddress(getService(namespace, name));
    }

    public String getAddress(final String name) {
        return getAddress(getService(name));
    }

    private String getAddress(final Service service) {
        Assert.requireNonNull(service, "service");

        final ServiceSpec spec = service.getSpec();
        Assert.requireNonNull(spec, "spec");

        final List<ServicePort> ports = service.getSpec().getPorts();
        Assert.requireNonNull(ports, "ports");
        if(ports.isEmpty()) {
            throw new IllegalStateException("No port found");
        }

        final ServicePort servicePort = ports.get(0);
        Assert.requireNonNull(servicePort, "servicePort");

        final Integer port = servicePort.getNodePort();
        Assert.requireNonNull(port, "port");
        if(port < MIN_PORT_NUMBER) {
            throw new IllegalStateException("No port found");
        }

        final List<String> externalIps = spec.getExternalIPs();
        Assert.requireNonNull(externalIps, "externalIps");
        if(externalIps.isEmpty()) {
            throw new IllegalStateException("No ip found");
        }

        final String ip = externalIps.get(0);
        Assert.requireNonBlank(ip, "ip");

        return ip + ":" + port;
    }

}
