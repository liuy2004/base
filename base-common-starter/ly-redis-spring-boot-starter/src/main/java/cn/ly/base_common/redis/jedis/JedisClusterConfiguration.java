package cn.ly.base_common.redis.jedis;

import cn.ly.base_common.helper.redis.JedisClusterHelper;
import cn.ly.base_common.redis.JedisClusterProperties;
import cn.ly.base_common.utils.number.LyMoreNumberUtil;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

/**
 * Created by liaomengge on 2018/11/16.
 */
@Configuration
@EnableConfigurationProperties(JedisClusterProperties.class)
@ConditionalOnProperty(prefix = "ly.redis.jedis-cluster", name = "enabled")
@ConditionalOnClass(JedisCluster.class)
public class JedisClusterConfiguration {

    @Autowired
    private JedisClusterProperties jedisClusterProperties;

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public JedisClusterHelper jedisClusterHelper(JedisCluster jedisCluster) {
        return new JedisClusterHelper(jedisCluster);
    }

    @Bean
    @ConditionalOnMissingBean
    public JedisCluster jedisCluster() {
        List<String> nodes = jedisClusterProperties.getNodes();
        Set<HostAndPort> hostAndPorts = createHostAndPorts(nodes);
        return new JedisCluster(hostAndPorts, jedisClusterProperties.getTimeout(),
                jedisClusterProperties.getMaxAttempts(), jedisClusterProperties.getPool());
    }

    private Set<HostAndPort> createHostAndPorts(List<String> nodes) {
        return nodes.stream().map(val -> {
            String[] hostAndPort = StringUtils.split(val, ':');
            return new HostAndPort(hostAndPort[0], LyMoreNumberUtil.toInt(hostAndPort[1]));
        }).collect(Collectors.toSet());
    }
}
