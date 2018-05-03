package com.canoo.dp.impl.platform.kubernetes;

import com.canoo.platform.server.spring.EnableDolphinPlatform;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDolphinPlatform
public class KubernetesDiscoveryServer {

    public static void main(final String[] args) {
        SpringApplication.run(KubernetesDiscoveryServer.class);
    }

}
