package com.github.liaomengge.service.base_framework.listener;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import static com.github.liaomengge.base_common.support.misc.consts.ToolConst.JOINER;

/**
 * Created by liaomengge on 2019/11/29.
 */
@Component
public class BaseWebServerInitializedListener {

    private static final String HTTP_PREFIX = "http://";

    @Order
    @EventListener(WebServerInitializedEvent.class)
    public void afterWebInitialize(WebServerInitializedEvent event) {
        WebServerApplicationContext context = event.getApplicationContext();
        if (isDevOrTestEnv(context)) {
            ServerProperties serverProperties = context.getBean(ServerProperties.class);
            WebEndpointProperties webEndpointProperties = context.getBean(WebEndpointProperties.class);
            String serverContextPath = serverProperties.getServlet().getContextPath();
            String endpointBasePath = webEndpointProperties.getBasePath();
            String applicationName = context.getEnvironment().getProperty("spring.application.name");
            String[] activeProfiles = context.getEnvironment().getActiveProfiles();

            String infoUrl = HTTP_PREFIX + getIpAndPort(event) + serverContextPath + endpointBasePath + "/info";
            StringBuilder sBuilder = new StringBuilder(16);
            sBuilder.append("\n");
            sBuilder.append("---------------------------------------------------------------------------").append("\n");
            sBuilder.append("APPLICATION NAME: ").append(applicationName).append("\n");
            sBuilder.append("ACTIVE PROFILES:  ").append(JOINER.join(activeProfiles)).append("\n");
            sBuilder.append("INFO URL:         ").append(infoUrl).append("\n");
            if (ClassUtils.isPresent("springfox.documentation.spring.web.plugins.Docket", null)) {
                String swaggerUrl = HTTP_PREFIX + getIpAndPort(event) + serverContextPath + "/doc.html";
                sBuilder.append("SWAGGER URL:      ").append(swaggerUrl).append("\n");
            }
            sBuilder.append("---------------------------------------------------------------------------").append("\n");
            System.err.println(sBuilder.toString());
        }
    }

    private String getIpAndPort(WebServerInitializedEvent event) {
        int port = event.getWebServer().getPort();
        String hostAddress = null;
        try {
            InetAddress address = InetAddress.getLocalHost();
            hostAddress = address.getHostAddress();
        } catch (UnknownHostException e) {
        }
        hostAddress = StringUtils.defaultIfBlank(hostAddress, "localhost");
        return hostAddress + ":" + port;
    }

    private boolean isDevOrTestEnv(WebServerApplicationContext ctx) {
        String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();
        if (ArrayUtils.isNotEmpty(activeProfiles)) {
            return Arrays.stream(activeProfiles).anyMatch(val -> StringUtils.equalsIgnoreCase(val, "dev") || StringUtils.equalsIgnoreCase(val, "test"));
        }
        return true;
    }
}
