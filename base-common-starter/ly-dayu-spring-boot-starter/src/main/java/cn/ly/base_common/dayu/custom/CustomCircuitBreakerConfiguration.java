package cn.ly.base_common.dayu.custom;

import cn.ly.base_common.dayu.custom.CustomCircuitBreakerProperties.RuleProperties;
import cn.ly.base_common.dayu.custom.aspect.CircuitBreakerResourceAspect;
import cn.ly.base_common.dayu.custom.circuit.CircuitBreakerHandler;
import cn.ly.base_common.dayu.custom.config.CircuitBreakerConfig;
import cn.ly.base_common.dayu.custom.helper.CircuitBreakerRedisHelper;
import cn.ly.base_common.helper.redis.IRedisHelper;
import cn.ly.base_common.redis.RedisAutoConfiguration;
import cn.ly.base_common.utils.number.LyNumberUtil;
import com.timgroup.statsd.StatsDClient;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static cn.ly.base_common.dayu.custom.consts.CustomCircuitBreakerConst.CUSTOM_CIRCUIT_BREAKER_PREFIX;

/**
 * Created by liaomengge on 2019/6/26.
 */
@AllArgsConstructor
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ConditionalOnProperty(prefix = CUSTOM_CIRCUIT_BREAKER_PREFIX, name = "enabled")
@EnableConfigurationProperties(CustomCircuitBreakerProperties.class)
public class CustomCircuitBreakerConfiguration {

    private final CustomCircuitBreakerProperties customCircuitBreakerProperties;

    @RefreshScope
    @Bean
    @ConditionalOnBean(IRedisHelper.class)
    @ConditionalOnMissingBean(CircuitBreakerRedisHelper.class)
    public CircuitBreakerRedisHelper circuitBreakerRedisHelper(IRedisHelper iRedisHelper) {
        RuleProperties ruleProperties = customCircuitBreakerProperties.getRule();
        CircuitBreakerConfig circuitBreakerConfig = new CircuitBreakerConfig();
        circuitBreakerConfig.setFailureIntervalSeconds(LyNumberUtil.getIntValue(ruleProperties.getFailureIntervalSeconds()));
        circuitBreakerConfig.setFailureThreshold(LyNumberUtil.getIntValue(ruleProperties.getFailureThreshold()));
        circuitBreakerConfig.setResetMilliSeconds(LyNumberUtil.getIntValue(ruleProperties.getResetMilliSeconds()));
        return new CircuitBreakerRedisHelper(iRedisHelper, circuitBreakerConfig);
    }

    @Bean
    @ConditionalOnClass(StatsDClient.class)
    @ConditionalOnBean(CircuitBreakerRedisHelper.class)
    @ConditionalOnMissingBean(CircuitBreakerHandler.class)
    public CircuitBreakerHandler circuitHandler(StatsDClient statsDClient,
                                                CircuitBreakerRedisHelper circuitBreakerRedisHelper) {
        return new CircuitBreakerHandler(statsDClient, circuitBreakerRedisHelper);
    }

    @Bean
    @ConditionalOnClass(StatsDClient.class)
    @ConditionalOnBean(CircuitBreakerRedisHelper.class)
    @ConditionalOnMissingBean(CircuitBreakerResourceAspect.class)
    public CircuitBreakerResourceAspect circuitBreakerResourceAspect(StatsDClient statsDClient,
                                                                     CircuitBreakerRedisHelper circuitBreakerRedisHelper) {
        return new CircuitBreakerResourceAspect(statsDClient, circuitBreakerRedisHelper);
    }
}
