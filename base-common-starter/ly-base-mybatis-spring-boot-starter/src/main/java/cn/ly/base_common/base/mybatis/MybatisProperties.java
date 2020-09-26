package cn.ly.base_common.base.mybatis;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

/**
 * Created by liaomengge on 2018/10/23.
 */
@Data
@Validated
@ConfigurationProperties(prefix = "ly.mybatis")
public class MybatisProperties {

    private Boolean isEnableSqlLog = Boolean.FALSE;
    @NotNull
    private String configLocation;
    @NotNull
    private String[] mapperLocations;
    private TxProperties tx = new TxProperties();
    private List<MappingProperties> mapping = Lists.newArrayList();

    @Data
    public static class MappingProperties {
        private String dsKeys;
        private boolean defaultMaster = false;
        private String mapperPackage;
    }

    public Resource resolveConfigLocation() {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        return resourceResolver.getResource(this.configLocation);
    }

    public Resource[] resolveMapperLocations() {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        List<Resource> resources = new ArrayList<>();
        if (this.mapperLocations != null) {
            for (String mapperLocation : this.mapperLocations) {
                try {
                    Resource[] mappers = resourceResolver.getResources(mapperLocation);
                    resources.addAll(Arrays.asList(mappers));
                } catch (IOException e) {
                }
            }
        }
        return resources.toArray(new Resource[resources.size()]);
    }

    @Data
    public static class TxProperties {
        private int timeout = TransactionDefinition.TIMEOUT_DEFAULT;
    }
}
