package com.canoo.dp.impl.platform.client.session;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.client.session.ClientSessionStore;
import com.canoo.platform.core.domain.UrlToAppDomainConverter;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ClientSessionStoreImpl implements ClientSessionStore {

    private static final Logger LOG = LoggerFactory.getLogger(ClientSessionStoreImpl.class);

    private final Lock mapLock = new ReentrantLock();

    private final Map<String, String> domainToId = new HashMap<>();

    private UrlToAppDomainConverter converter = new SimpleUrlToAppDomainConverter();

    @Override
    public Optional<String> getClientIdentifierForUrl(final URL url) {
        Assert.requireNonNull(url, "url");
        return converter.getApplicationDomain(url).map(applicationDomain -> {
            LOG.debug("searching for client id application domain: {}", applicationDomain);
            mapLock.lock();
            try {
                final String clientId = domainToId.get(applicationDomain);
                LOG.debug("found client id '{}' for application domain {}", clientId, applicationDomain);
                return clientId;
            } finally {
                mapLock.unlock();
            }
        });
    }

    public void setClientIdentifierForUrl(final URL url, final String clientId) {
        Assert.requireNonNull(url, "url");
        final String applicationDomain = converter.getApplicationDomain(url).
                orElseThrow(() -> new IllegalStateException("Can not define application domain for url " + url));

        LOG.debug("updating client id for application domain: {}", applicationDomain);

        mapLock.lock();
        try {
            if (domainToId.containsKey(applicationDomain)) {
                final String storedId = domainToId.get(applicationDomain);
                if (clientId != null && !storedId.equals(clientId)) {
                    throw new IllegalStateException("PlatformClient Id for application domain " + applicationDomain + " already specified.");
                }
            } else {
                LOG.debug("Defining client id '{}' for application domain {}", clientId, applicationDomain);
                if (clientId == null) {
                    LOG.debug("Since client id for application domain {} is defined as 'null' it will be removed", applicationDomain);
                    domainToId.remove(applicationDomain);
                } else {
                    domainToId.put(applicationDomain, clientId);
                }
            }
        } finally {
            mapLock.unlock();
        }
    }

    @Override
    public void resetSession(final URL url) {
        LOG.debug("Resetting client id for url {}", url);
        setClientIdentifierForUrl(url, null);
    }

    public UrlToAppDomainConverter getConverter() {
        return converter;
    }

    public void setConverter(final UrlToAppDomainConverter converter) {
        this.converter = Assert.requireNonNull(converter, "converter");
    }
}
