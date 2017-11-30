package com.canoo.dp.impl.platform.client.session;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.core.domain.UrlToAppDomainConverter;
import org.apiguardian.api.API;

import java.net.URL;
import java.util.Optional;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class SimpleUrlToAppDomainConverter implements UrlToAppDomainConverter {

    @Override
    public Optional<String> getApplicationDomain(URL url) {
        Assert.requireNonNull(url, "url");
        return Optional.of(url.getHost() + ":" + url.getPort());
    }
}
