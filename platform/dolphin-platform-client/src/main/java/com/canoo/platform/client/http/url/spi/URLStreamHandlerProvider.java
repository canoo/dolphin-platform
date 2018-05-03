package com.canoo.platform.client.http.url.spi;

import com.canoo.platform.core.PlatformConfiguration;

import java.net.URLStreamHandler;
import java.util.List;

public interface URLStreamHandlerProvider {

    void init(PlatformConfiguration configuration);

    List<String> getSupportedProtocols();

    URLStreamHandler createURLStreamHandler(String protocol);
}
