package cn.ly.base_common.metric.druid.task;

import cn.ly.base_common.metric.druid.MetricDruidProperties;
import cn.ly.base_common.utils.log4j2.LyLogger;
import cn.ly.base_common.utils.thread.LyThreadUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.timgroup.statsd.StatsDClient;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 2019/7/24.
 */
@AllArgsConstructor
public class MetricDruidScheduledTask {

    private static final Logger log = LyLogger.getInstance(MetricDruidScheduledTask.class);

    private static final String METRIC_DRUID_PREFIX = "metric-druid.";

    private StatsDClient statsDClient;
    private MetricDruidProperties metricDruidProperties;

    @PostConstruct
    private void init() {
        DruidStatsThread druidStatsThread = new DruidStatsThread("metric-druid-stats");
        druidStatsThread.start();
        Runtime.getRuntime().addShutdownHook(new Thread(druidStatsThread::interrupt));
    }

    private class DruidStatsThread extends Thread {

        public DruidStatsThread(String name) {
            super(name);
            this.setDaemon(true);
        }

        @Override
        public void run() {
            long initialDelay = metricDruidProperties.getInitialDelay() * 1000L;
            if (initialDelay > 0) {
                LyThreadUtil.sleep(initialDelay);
            }
            while (!this.isInterrupted()) {
                try {
                    try {
                        Set<DruidDataSource> druidDataSources =
                                DruidDataSourceStatManager.getDruidDataSourceInstances();
                        Optional.ofNullable(druidDataSources).ifPresent(val -> val.forEach(druidDataSource -> {
                            long maxWaitMillis = druidDataSource.getMaxWait();//最大等待时间
                            long waitThreadCount = druidDataSource.getWaitThreadCount();//当前等待获取连接的线程数
                            long notEmptyWaitMillis = druidDataSource.getNotEmptyWaitMillis();//获取连接时累计等待多长时间
                            long notEmptyWaitCount = druidDataSource.getNotEmptyWaitCount();//获取连接时累计等待多少次'

                            int maxActive = druidDataSource.getMaxActive();//最大活跃数
                            int poolingCount = druidDataSource.getPoolingCount();//当前连接池数
                            int poolingPeak = druidDataSource.getPoolingPeak();//连接池峰值
                            int activeCount = druidDataSource.getActiveCount();//当前活跃连接数
                            int activePeak = druidDataSource.getActivePeak();//活跃数峰值

                            if (Objects.nonNull(statsDClient)) {
                                URI jdbcUri = parseJdbcUrl(druidDataSource.getUrl());
                                Optional.ofNullable(jdbcUri).ifPresent(val2 -> {
                                    String host = StringUtils.replaceChars(val2.getHost(), '.', '_');
                                    String prefix = METRIC_DRUID_PREFIX + host + '.' + val2.getPort() + '.';
                                    statsDClient.recordExecutionTime(prefix + "maxWaitMillis", maxWaitMillis);
                                    statsDClient.recordExecutionTime(prefix + "waitThreadCount", waitThreadCount);
                                    statsDClient.recordExecutionTime(prefix + "notEmptyWaitMillis", notEmptyWaitMillis);
                                    statsDClient.recordExecutionTime(prefix + "notEmptyWaitCount", notEmptyWaitCount);
                                    statsDClient.recordExecutionTime(prefix + "maxActive", maxActive);
                                    statsDClient.recordExecutionTime(prefix + "poolingCount", poolingCount);
                                    statsDClient.recordExecutionTime(prefix + "poolingPeak", poolingPeak);
                                    statsDClient.recordExecutionTime(prefix + "activeCount", activeCount);
                                    statsDClient.recordExecutionTime(prefix + "activePeak", activePeak);
                                });
                            } else {
                                druidDataSource.logStats();
                            }
                        }));
                    } catch (Exception e) {
                        log.error("druid stats exception", e);
                    }
                    TimeUnit.SECONDS.sleep(metricDruidProperties.getStatsInterval());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.info("metric druid interrupt exit...");
                } catch (Exception e) {
                    log.error("metric druid exception...", e);
                }
            }
        }
    }

    private URI parseJdbcUrl(String url) {
        if (StringUtils.isBlank(url) || !StringUtils.startsWith(url, "jdbc:")) {
            return null;
        }
        String cleanURI = url.substring(5);
        return URI.create(cleanURI);
    }
}
