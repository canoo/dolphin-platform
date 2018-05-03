package com.canoo.dp.impl.platform.client.metrics;

import com.canoo.dp.impl.platform.client.AbstractServiceProvider;
import com.canoo.dp.impl.platform.core.context.ContextManagerImpl;
import com.canoo.dp.impl.platform.metrics.MetricsImpl;
import com.canoo.dp.impl.platform.metrics.TagUtil;
import com.canoo.platform.client.ClientConfiguration;
import com.canoo.platform.metrics.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.prometheus.PrometheusPushMeterRegistry;

import java.util.List;
import java.util.concurrent.ExecutorService;

import static com.canoo.dp.impl.platform.core.PlatformConstants.APPLICATION_NAME_DEFAULT;
import static com.canoo.dp.impl.platform.core.PlatformConstants.APPLICATION_NAME_PROPERTY;

public class MetricsServiceProvider extends AbstractServiceProvider<Metrics> {

    public MetricsServiceProvider() {
        super(Metrics.class);
    }

    @Override
    protected Metrics createService(final ClientConfiguration configuration) {
        final MetricsPush push = new MetricsPush(configuration);
        final String job = configuration.getProperty(APPLICATION_NAME_PROPERTY, APPLICATION_NAME_DEFAULT);
        final PrometheusPushMeterRegistry registry = new PrometheusPushMeterRegistry(job, push);
        final ExecutorService executorService = configuration.getBackgroundExecutor();
        executorService.submit(() -> {
            while (true) {
                try {
                    Thread.sleep(1_000);
                    registry.push();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        MetricsImpl.getInstance().init(registry);

        final List<Tag> tagList = TagUtil.convertTags(ContextManagerImpl.getInstance().getGlobalContexts());

        new ClassLoaderMetrics(tagList).bindTo(registry);
        new JvmMemoryMetrics(tagList).bindTo(registry);
        new JvmGcMetrics(tagList).bindTo(registry);
        new ProcessorMetrics(tagList).bindTo(registry);
        new JvmThreadMetrics(tagList).bindTo(registry);

        return MetricsImpl.getInstance();
    }
}
