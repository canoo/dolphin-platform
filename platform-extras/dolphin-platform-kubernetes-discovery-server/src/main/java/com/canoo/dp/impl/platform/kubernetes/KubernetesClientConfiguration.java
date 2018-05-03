package com.canoo.dp.impl.platform.kubernetes;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.ApplicationScope;

@Configuration
public class KubernetesClientConfiguration {

    @Value("${dolphinPlatform.kubernetes.masterUrl}")
    private String masterUrl;

    @Value("${dolphinPlatform.kubernetes.user}")
    private String user;

    @Value("${dolphinPlatform.kubernetes.password}")
    private String password;

    @Bean("kubernetesClient")
    @ApplicationScope
    public KubernetesClient createKubernetesClient() {
        final Config config = new ConfigBuilder()
                .withMasterUrl(masterUrl)
                .withUsername(user)
                .withPassword(password)
                .build();
        return new DefaultKubernetesClient(config);
    }
}
