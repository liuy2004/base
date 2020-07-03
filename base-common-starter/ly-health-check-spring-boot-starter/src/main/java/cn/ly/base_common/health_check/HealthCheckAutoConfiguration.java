package cn.ly.base_common.health_check;

import cn.ly.base_common.health_check.health.ActiveMQHealthCheck;
import cn.ly.base_common.health_check.health.DataSourceHealthCheck;
import cn.ly.base_common.health_check.health.HealthCheck;
import cn.ly.base_common.health_check.health.RabbitMQHealthCheck;
import cn.ly.base_common.health_check.health.domain.HealthInfo;
import cn.ly.base_common.utils.json.LyJsonUtil;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Created by liaomengge on 2019/7/11.
 */
@Configuration
public class HealthCheckAutoConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    @ConditionalOnClass(javax.jms.ConnectionFactory.class)
    @ConditionalOnMissingBean
    public ActiveMQHealthCheck activeMQHealthCheck() {
        return new ActiveMQHealthCheck();
    }

    @Bean
    @ConditionalOnClass(ConnectionFactory.class)
    @ConditionalOnMissingBean
    public RabbitMQHealthCheck rabbitMQHealthCheck() {
        return new RabbitMQHealthCheck();
    }

    @Bean
    @ConditionalOnClass(AbstractRoutingDataSource.class)
    @ConditionalOnMissingBean
    public DataSourceHealthCheck dataSourceHealthCheck() {
        return new DataSourceHealthCheck();
    }

    @PostConstruct
    private void init() {
        Map<String, HealthCheck> healthCheckMap = this.applicationContext.getBeansOfType(HealthCheck.class);
        healthCheckMap.values().forEach(healthCheck -> {
            HealthInfo healthInfo = healthCheck.health();
            if (HealthInfo.Status.DOWN == healthInfo.getStatus()) {
                throw new RuntimeException(LyJsonUtil.toJson(healthInfo.getDetails()));
            }
        });
    }
}