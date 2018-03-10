package com.canoo.platform.samples.reconnect;

import com.canoo.platform.remoting.server.spring.DolphinPlatformRemotingApplication;
import org.springframework.boot.SpringApplication;

@DolphinPlatformRemotingApplication
public class Server {

    public static void main(String[] args) {
        SpringApplication.run(Server.class);
    }

}
