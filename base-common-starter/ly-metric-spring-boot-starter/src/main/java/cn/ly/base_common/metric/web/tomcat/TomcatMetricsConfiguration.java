package cn.ly.base_common.metric.web.tomcat;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.tomcat.TomcatMetrics;
import org.apache.catalina.startup.Tomcat;
import org.springframework.boot.actuate.autoconfigure.metrics.web.tomcat.TomcatMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Servlet;

/**
 * Created by liaomengge on 2020/9/16.
 */
@Configuration
@ConditionalOnClass({Servlet.class, Tomcat.class})
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "ly.metric.web.tomcat", name = "enabled", matchIfMissing = true)
@AutoConfigureBefore(TomcatMetricsAutoConfiguration.class)
public class TomcatMetricsConfiguration {

    @Bean
    @ConditionalOnBean(MeterRegistry.class)
    @ConditionalOnMissingBean({TomcatMetrics.class, TomcatMetricsBinder.class})
    public TomcatMetricsBinder tomcatMetricsBinder(MeterRegistry meterRegistry) {
        return new TomcatMetricsBinder(meterRegistry);
    }
}
