package cn.ly.service.base_framework.common.filter.chain;

import cn.ly.base_common.utils.number.LyNumberUtil;

import com.google.common.collect.Lists;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.core.annotation.OrderUtils;

import lombok.Getter;

/**
 * Created by liaomengge on 2018/11/21.
 */
@Getter
public class FilterChain implements ServiceFilter {

    public List<ServiceFilter> filters = Lists.newArrayList();
    public int pos = 0;

    public FilterChain cloneChain() {
        return new FilterChain().addFilter(getFilters());
    }

    public FilterChain addFilter(ServiceFilter filter) {
        if (Objects.isNull(filters)) {
            filters = Lists.newArrayList();
        }
        filters.add(filter);
        return this;
    }

    public FilterChain addFilter(List<ServiceFilter> filterList) {
        if (Objects.isNull(filters)) {
            filters = Lists.newArrayList();
        }
        filters.addAll(filterList);
        return this;
    }

    public boolean hasNextFilter() {
        return pos < filters.size();
    }

    public void sortFilters() {
        filters = filters.stream()
                .sorted(Comparator.comparingInt(value -> LyNumberUtil.getIntValue(OrderUtils.getOrder(value.getClass(), value.getOrder()))))
                .collect(Collectors.toList());
    }

    public String printFilters() {
        return filters.parallelStream()
                .map(val -> {
                    String filterName = val.getClass().getSimpleName();
                    int order = LyNumberUtil.getIntValue(OrderUtils.getOrder(val.getClass(), val.getOrder()));
                    return filterName + "(" + order + ")";
                }).reduce((val, val2) -> val + ',' + val2).orElse("null");
    }

    public void reset() {
        pos = 0;
        filters = null;
    }

    @Override
    public Object doFilter(ProceedingJoinPoint joinPoint, FilterChain chain) throws Throwable {
        if (hasNextFilter()) {
            return getFilters().get(pos++).doFilter(joinPoint, chain);
        }
        return joinPoint.proceed();
    }
}
