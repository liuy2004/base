package cn.ly.base_common.nacos.endpoint;

import cn.ly.base_common.nacos.consts.NacosConst;
import cn.ly.base_common.utils.error.LyThrowableUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.registry.NacosRegistration;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.common.collect.Maps;
import org.springframework.beans.BeansException;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * Created by liaomengge on 2020/8/15.
 */
@Endpoint(id = NacosConst.PULL_IN_ENDPOINT)
public class NacosPullInEndpoint extends AbstractPullEndpoint implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @ReadOperation
    public Map<String, Object> pullIn() {
        Map<String, Object> retMap = Maps.newHashMap();
        try {
            NacosDiscoveryProperties nacosDiscoveryProperties =
                    applicationContext.getBean(NacosDiscoveryProperties.class);
            NacosRegistration nacosRegistration = applicationContext.getBean(NacosRegistration.class);
            Instance instance = getNacosInstance(nacosRegistration, true);
            NacosServiceManager nacosServiceManager = applicationContext.getBean(NacosServiceManager.class);
            NamingMaintainService namingMaintainService =
                    nacosServiceManager.getNamingMaintainService(nacosDiscoveryProperties.getNacosProperties());
            namingMaintainService.updateInstance(nacosDiscoveryProperties.getService(), instance);

            log.info("set service => {}, instance => {}, status => Enabled", nacosDiscoveryProperties.getService(),
                    nacosDiscoveryProperties.getIp());
            retMap.put("status", "Enabled");
            retMap.put("success", true);
        } catch (Exception e) {
            retMap.put("exception", LyThrowableUtil.getStackTrace(e));
            retMap.put("success", false);
        }
        return retMap;
    }
}
