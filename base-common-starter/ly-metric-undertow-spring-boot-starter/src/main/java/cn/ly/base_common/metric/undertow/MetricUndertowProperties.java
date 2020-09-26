package cn.ly.base_common.metric.undertow;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Created by liaomengge on 2019/7/30.
 */
@Data
@ConfigurationProperties("ly.metric-undertow")
public class MetricUndertowProperties {

    private boolean enabled;
    private long initialDelay = 120L;//单位：秒
    private long statsInterval = 20L;//单位：秒
}
