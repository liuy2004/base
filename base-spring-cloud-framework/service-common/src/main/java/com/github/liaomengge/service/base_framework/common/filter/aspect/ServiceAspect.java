package com.github.liaomengge.service.base_framework.common.filter.aspect;

import com.github.liaomengge.base_common.support.datasource.DBContext;
import com.github.liaomengge.base_common.utils.error.LyThrowableUtil;
import com.github.liaomengge.base_common.utils.json.LyJsonUtil;
import com.github.liaomengge.base_common.utils.log.LyMDCUtil;
import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import com.github.liaomengge.base_common.utils.net.LyNetworkUtil;
import com.github.liaomengge.base_common.utils.trace.LyTraceLogUtil;
import com.github.liaomengge.base_common.utils.web.LyWebUtil;
import com.github.liaomengge.service.base_framework.base.DataResult;
import com.github.liaomengge.service.base_framework.common.config.FilterConfig;
import com.github.liaomengge.service.base_framework.common.filter.*;
import com.github.liaomengge.service.base_framework.common.filter.chain.FilterChain;
import com.github.liaomengge.service.base_framework.common.util.TimeThreadLocalUtil;
import com.google.common.collect.Iterables;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.InputStreamSource;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static com.github.liaomengge.base_common.support.misc.consts.ToolConst.SPLITTER;

/**
 * Created by liaomengge on 2018/10/23.
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ServiceAspect {

    private static final Logger log = LyLogger.getInstance(ServiceAspect.class);

    @Getter
    private FilterChain defaultFilterChain;

    @Setter
    private FilterChain filterChain;

    @Setter
    private FilterConfig filterConfig = new FilterConfig();

    @Autowired(required = false)
    private MeterRegistry meterRegistry;

    @Around("target(com.github.liaomengge.service.base_framework.api.BaseFrameworkService) " +
            "&& execution(public * *(..)) " +
            "&& !@annotation(com.github.liaomengge.service.base_framework.common.annotation.IgnoreServiceAop)")
    public Object proceed(ProceedingJoinPoint joinPoint) throws Throwable {
        TimeThreadLocalUtil.set(System.nanoTime());
        StringBuilder reqArgsBuilder = buildArgs(joinPoint);

        FilterChain filterChain = null;
        try {
            filterChain = defaultFilterChain.cloneChain();
            Object retObj = filterChain.doFilter(joinPoint, filterChain);
            buildResultLog(joinPoint, retObj, reqArgsBuilder);
            return retObj;
        } catch (Exception e) {
            buildExceptionResultLog(e, reqArgsBuilder);
            throw e;
        } finally {
            Optional.ofNullable(filterChain).ifPresent(FilterChain::reset);

            TimeThreadLocalUtil.remove();

            DBContext.clearDBKey();

            LyTraceLogUtil.clearTrace();

            LyMDCUtil.remove(LyMDCUtil.MDC_WEB_REMOTE_IP);
            LyMDCUtil.remove(LyMDCUtil.MDC_WEB_URI);
            LyMDCUtil.remove(LyMDCUtil.MDC_WEB_ELAPSED_NANO_TIME);
        }
    }

    private String getMethodName(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod().getName();
    }

    private boolean isIgnoreLogArgsMethod(ProceedingJoinPoint joinPoint) {
        String ignoreArgsMethodName = filterConfig.getLog().getIgnoreArgsMethodName();
        if (StringUtils.isNotBlank(ignoreArgsMethodName)) {
            String methodName = getMethodName(joinPoint);
            Iterable<String> iterable = SPLITTER.split(ignoreArgsMethodName);
            return Iterables.contains(iterable, methodName);
        }
        return false;
    }

    private boolean isIgnoreLogResultMethod(ProceedingJoinPoint joinPoint) {
        String ignoreResultMethodName = filterConfig.getLog().getIgnoreResultMethodName();
        if (StringUtils.isNotBlank(ignoreResultMethodName)) {
            String methodName = getMethodName(joinPoint);
            Iterable<String> iterable = SPLITTER.split(ignoreResultMethodName);
            return Iterables.contains(iterable, methodName);
        }
        return false;
    }

    private StringBuilder buildArgs(ProceedingJoinPoint joinPoint) {
        StringBuilder sBuilder = new StringBuilder();
        if (isIgnoreLogArgsMethod(joinPoint)) {
            return sBuilder;
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        sBuilder.append("method => " + method.getName());
        sBuilder.append(", args => ");
        buildArgsLog(args, sBuilder);
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Optional.ofNullable(servletRequestAttributes).map(ServletRequestAttributes::getRequest).ifPresent(val -> {
            LyMDCUtil.put(LyMDCUtil.MDC_WEB_REMOTE_IP, LyNetworkUtil.getIpAddress(val));
            LyMDCUtil.put(LyMDCUtil.MDC_WEB_URI, val.getRequestURI());
        });
        return sBuilder;
    }

    private void buildArgsLog(Object[] args, StringBuilder sBuilder) {
        if (Objects.isNull(args) || args.length <= 0) {
            sBuilder.append("null,");
            return;
        }
        Arrays.stream(args)
                .filter(Objects::nonNull)
                .filter(val -> !(val instanceof HttpServletResponse || val instanceof MultipartFile
                        || val instanceof InputStream || val instanceof InputStreamSource || val instanceof BindingResult))
                .forEach(val -> {
                    if (val instanceof HttpServletRequest) {
                        sBuilder.append(LyJsonUtil.toJson4Log(LyWebUtil.getRequestParams((HttpServletRequest) val))).append(',');
                    } else if (val instanceof WebRequest) {
                        sBuilder.append(LyJsonUtil.toJson4Log(((WebRequest) val).getParameterMap())).append(',');
                    } else {
                        sBuilder.append(LyJsonUtil.toJson4Log(val)).append(',');
                    }
                });
    }

    private void buildResultLog(ProceedingJoinPoint joinPoint, Object retObj, StringBuilder sBuilder) {
        long elapsedNanoTime = System.nanoTime() - TimeThreadLocalUtil.get();
        if (!isIgnoreLogResultMethod(joinPoint)) {
            if (retObj instanceof DataResult) {
                DataResult dataResult = (DataResult) retObj;
                dataResult.setElapsedNanoSeconds(elapsedNanoTime);
                sBuilder.append(" result => " + LyJsonUtil.toJson4Log(dataResult));
            } else if (retObj instanceof String) {
                sBuilder.append(" result => " + retObj);
            } else {
                sBuilder.append(" result => " + LyJsonUtil.toJson4Log(retObj));
            }
        }
        LyMDCUtil.put(LyMDCUtil.MDC_WEB_ELAPSED_NANO_TIME, String.valueOf(elapsedNanoTime));
        log.info("请求响应日志: {}", sBuilder.toString());
    }

    private void buildExceptionResultLog(Exception e, StringBuilder sBuilder) {
        long elapsedNanoTime = System.nanoTime() - TimeThreadLocalUtil.get();
        sBuilder.append(" exception result => " + LyThrowableUtil.getStackTrace(e));
        LyMDCUtil.put(LyMDCUtil.MDC_WEB_ELAPSED_NANO_TIME, String.valueOf(elapsedNanoTime));
        log.error("请求响应日志: {}", sBuilder.toString());
    }

    @PostConstruct
    private void init() {
        defaultFilterChain = new FilterChain();
        boolean enabledDefaultFilter = filterConfig.isEnabledDefaultFilter();
        if (enabledDefaultFilter) {
            defaultFilterChain.addFilter(new FailFastFilter(filterConfig))
                    .addFilter(new TraceFilter())
                    .addFilter(new SignFilter(filterConfig))
                    .addFilter(new ParamValidateFilter())
                    .addFilter(new MetricsFilter(meterRegistry));
        }
        if (Objects.nonNull(filterChain)) {
            defaultFilterChain.addFilter(filterChain.getFilters());
        }
        defaultFilterChain.sortFilters();
        LyMDCUtil.put(LyMDCUtil.MDC_WEB_ELAPSED_NANO_TIME, NumberUtils.INTEGER_ZERO.toString());
        log.info("sort filter chain ===> {}", defaultFilterChain.printFilters());
    }
}