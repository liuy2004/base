package com.github.liaomengge.base_common.mq.activemq;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;

import com.google.common.collect.Lists;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

/**
 * Created by liaomengge on 2019/5/5.
 */
@Data
@Validated
@ConfigurationProperties(prefix = "base.mq.activemq")
public class ActiveMQProperties {

    @NotNull
    private String brokerUrl;
    private final Pool pool = new Pool();
    private List<QueueProperties> queues = Lists.newArrayList();

    @Data
    public static class Pool {
        private boolean blockIfFull = true;
        private long blockIfFullTimeout = -1;
        private boolean createConnectionOnStartup = true;
        private long expiryTimeout = 0L;
        private int idleTimeout = 30000;
        private int maxConnections = 1;
        private int maximumActiveSessionPerConnection = 500;
        private boolean reconnectOnException = true;
        private long timeBetweenExpirationCheck = -1L;
        private boolean useAnonymousProducers = true;
    }

    @Data
    @Validated
    public static class QueueProperties {
        private String beanName;
        @NotNull
        private String baseQueueName;
        private int queueCount = 1;

        public String buildBeanName() {
            if (StringUtils.isBlank(this.getBeanName())) {
                return LOWER_UNDERSCORE.to(LOWER_CAMEL, this.getBaseQueueName()) + "QueueConfig";
            }
            return this.getBeanName();
        }
    }
}
