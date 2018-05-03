package com.canoo.dp.impl.platform.client.metrics;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.client.ClientConfiguration;
import com.canoo.platform.client.PlatformClient;
import com.canoo.platform.core.DolphinRuntimeException;
import com.canoo.platform.core.http.HttpClient;
import com.canoo.platform.core.http.HttpResponse;
import com.canoo.platform.core.http.RequestMethod;
import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;

import static com.canoo.dp.impl.platform.client.metrics.ClientMetricConstants.PUSH_GATEWAY_DEFAULT;
import static com.canoo.dp.impl.platform.client.metrics.ClientMetricConstants.PUSH_GATEWAY_PROPERTY;
import static com.canoo.dp.impl.platform.core.http.HttpHeaderConstants.CHARSET;

public class MetricsPush {

    private final String gatewayBaseURL;

    public MetricsPush(final ClientConfiguration configuration) {
        Assert.requireNonNull(configuration, "configuration");
        this.gatewayBaseURL = configuration.getProperty(PUSH_GATEWAY_PROPERTY, PUSH_GATEWAY_DEFAULT);
    }

    public void push(CollectorRegistry registry, String job) throws IOException {
        doRequest(registry, job, Collections.emptyMap(), RequestMethod.PUT);
    }

    public void push(Collector collector, String job) throws IOException {
        CollectorRegistry registry = new CollectorRegistry();
        collector.register(registry);
        push(registry, job);
    }

    public void push(CollectorRegistry registry, String job, Map<String, String> groupingKey) throws IOException {
        doRequest(registry, job, groupingKey, RequestMethod.PUT);
    }

    public void push(Collector collector, String job, Map<String, String> groupingKey) throws IOException {
        CollectorRegistry registry = new CollectorRegistry();
        collector.register(registry);
        push(registry, job, groupingKey);
    }

    public void pushAdd(CollectorRegistry registry, String job) throws IOException {
        doRequest(registry, job, Collections.emptyMap(), RequestMethod.POST);
    }

    public void pushAdd(Collector collector, String job) throws IOException {
        CollectorRegistry registry = new CollectorRegistry();
        collector.register(registry);
        pushAdd(registry, job);
    }

    public void pushAdd(CollectorRegistry registry, String job, Map<String, String> groupingKey) throws IOException {
        doRequest(registry, job, groupingKey, RequestMethod.POST);
    }

    public void pushAdd(Collector collector, String job, Map<String, String> groupingKey) throws IOException {
        CollectorRegistry registry = new CollectorRegistry();
        collector.register(registry);
        pushAdd(registry, job, groupingKey);
    }

    public void delete(String job) throws IOException {
        doRequest(null, job, Collections.emptyMap(), RequestMethod.DELETE);
    }

    public void delete(String job, Map<String, String> groupingKey) throws IOException {
        doRequest(null, job, groupingKey, RequestMethod.DELETE);
    }

    private void doRequest(CollectorRegistry registry, String job, Map<String, String> groupingKey, RequestMethod method) {
        Assert.requireNonNull(method, "method");
        Assert.requireNonNull(groupingKey, "groupingKey");
        try {
            String url = gatewayBaseURL + URLEncoder.encode(job, CHARSET);
            for (Map.Entry<String, String> entry : groupingKey.entrySet()) {
                url += "/" + entry.getKey() + "/" + URLEncoder.encode(entry.getValue(), CHARSET);
            }
            final HttpClient client = PlatformClient.getService(HttpClient.class);
            if (!method.equals(RequestMethod.DELETE)) {
                final String content = as004String(registry);
                final HttpResponse<Void> response = client.request(url, method).withContent(content, TextFormat.CONTENT_TYPE_004).withoutResult().execute().get();
                final int status = response.getStatusCode();
                if (status != HttpURLConnection.HTTP_ACCEPTED) {
                    throw new IOException("Response code from " + url + " was " + response);
                }
            } else {
                final HttpResponse<Void> response = client.request(url, method).withoutContent().withoutResult().execute().get();
                final int status = response.getStatusCode();
                if (status != HttpURLConnection.HTTP_ACCEPTED) {
                    throw new IOException("Response code from " + url + " was " + response);
                }
            }
        } catch (Exception e) {
            throw new DolphinRuntimeException("Can not push metrics", e);
        }
    }

    private String as004String(final CollectorRegistry registry) throws IOException {
        Writer writer = new StringWriter();
        try {
            TextFormat.write004(writer, registry.metricFamilySamples());
        } catch (IOException e) {
            throw new DolphinRuntimeException("Can not export metrics", e);
        }
        return writer.toString();
    }
}
