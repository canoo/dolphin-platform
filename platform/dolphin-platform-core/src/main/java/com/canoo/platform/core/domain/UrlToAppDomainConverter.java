package com.canoo.platform.core.domain;

import org.apiguardian.api.API;

import java.net.URL;
import java.util.Optional;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(since = "0.x", status = EXPERIMENTAL)
public interface UrlToAppDomainConverter {

    Optional<String> getApplicationDomain(URL url);
}
