package cn.ly.base_common.helper.cache;

import cn.ly.base_common.helper.redis.IRedisHelper;
import cn.ly.base_common.utils.json.LyJacksonUtil;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Objects;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;

import lombok.Setter;

/**
 * Created by liaomengge on 2019/7/9.
 */
public class RedisDbCacheHelper {

    private static final String SYNCHRONIZED_PREFIX = "synchronized:";

    @Setter
    private IRedisHelper iRedisHelper;

    /**
     * Redis缓存(L2) + synchronized
     *
     * @param key
     * @param supplier
     * @return
     */
    public String invoke(String key, Supplier<String> supplier) {
        String value = iRedisHelper.get(key);
        if (StringUtils.isBlank(value)) {
            synchronized (getSynchronizedKey(key)) {
                value = iRedisHelper.get(key);
                if (StringUtils.isBlank(value)) {
                    value = supplier.get();
                    if (StringUtils.isNotBlank(value)) {
                        iRedisHelper.set(key, value);
                    }
                    return value;
                }
            }
        }

        return value;
    }

    /**
     * Redis缓存(L2) + synchronized + expireTime
     *
     * @param key
     * @param redisExpiresInSeconds
     * @param supplier
     * @return
     */
    public String invoke(String key, int redisExpiresInSeconds, Supplier<String> supplier) {
        String value = iRedisHelper.get(key);
        if (StringUtils.isBlank(value)) {
            synchronized (getSynchronizedKey(key)) {
                value = iRedisHelper.get(key);
                if (StringUtils.isBlank(value)) {
                    value = supplier.get();
                    if (StringUtils.isNotBlank(value) && redisExpiresInSeconds > 0) {
                        iRedisHelper.set(key, value, redisExpiresInSeconds);
                    }
                    return value;
                }
            }
        }

        return value;
    }

    /**
     * Redis缓存(L2) + synchronized + expireTime
     * 如果value=null时, 指定缓存默认值
     *
     * @param key
     * @param redisExpiresInSeconds
     * @param defaultNullValue
     * @param supplier
     * @return
     */
    public String invoke(String key, int redisExpiresInSeconds, String defaultNullValue, Supplier<String> supplier) {
        String value = iRedisHelper.get(key);
        if (StringUtils.isBlank(value)) {
            synchronized (getSynchronizedKey(key)) {
                value = iRedisHelper.get(key);
                if (StringUtils.isBlank(value)) {
                    value = supplier.get();
                    value = StringUtils.defaultIfBlank(value, defaultNullValue);
                    if (StringUtils.isNotBlank(value) && redisExpiresInSeconds > 0) {
                        iRedisHelper.set(key, value, redisExpiresInSeconds);
                    }
                    return value;
                }
            }
        }

        return value;
    }

    /**
     * Redis缓存(L2) + 泛型化(Generic) + synchronized
     *
     * @param key
     * @param typeReference
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric(String key, TypeReference<T> typeReference, Supplier<T> supplier) {
        String value = iRedisHelper.get(key);
        if (StringUtils.isBlank(value)) {
            synchronized (getSynchronizedKey(key)) {
                value = iRedisHelper.get(key);
                if (StringUtils.isBlank(value)) {
                    T t = supplier.get();
                    if (Objects.nonNull(t)) {
                        iRedisHelper.set(key, LyJacksonUtil.toJson(t));
                    }
                    return t;
                }
            }
        }

        return LyJacksonUtil.fromJson(value, typeReference);
    }

    /**
     * Redis缓存(L2) + 泛型化(Generic) + synchronized
     *
     * @param key
     * @param clz
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGenericSync2(String key, Class<T> clz, Supplier<T> supplier) {
        String value = iRedisHelper.get(key);
        if (StringUtils.isBlank(value)) {
            synchronized (getSynchronizedKey(key)) {
                value = iRedisHelper.get(key);
                if (StringUtils.isBlank(value)) {
                    T t = supplier.get();
                    if (Objects.nonNull(t)) {
                        iRedisHelper.set(key, LyJacksonUtil.toJson(t));
                    }
                    return t;
                }
            }
        }

        return LyJacksonUtil.fromJson(value, clz);
    }

    /**
     * Redis缓存(L2) + 泛型化(Generic) + synchronized + expireTime
     *
     * @param key
     * @param redisExpiresInSeconds
     * @param typeReference
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric2(String key, int redisExpiresInSeconds, TypeReference<T> typeReference,
                                Supplier<T> supplier) {
        String value = iRedisHelper.get(key);
        if (StringUtils.isBlank(value)) {
            synchronized (getSynchronizedKey(key)) {
                value = iRedisHelper.get(key);
                if (StringUtils.isBlank(value)) {
                    T t = supplier.get();
                    if (Objects.nonNull(t) && redisExpiresInSeconds > 0) {
                        iRedisHelper.set(key, LyJacksonUtil.toJson(t), redisExpiresInSeconds);
                    }
                    return t;
                }
            }
        }

        return LyJacksonUtil.fromJson(value, typeReference);
    }

    /**
     * Redis缓存(L2) + 泛型化(Generic) + synchronized + expireTime
     * 如果value=null时, 指定缓存默认值
     *
     * @param key
     * @param redisExpiresInSeconds
     * @param typeReference
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric2(String key, int redisExpiresInSeconds, T defaultNullValue,
                                TypeReference<T> typeReference, Supplier<T> supplier) {
        String value = iRedisHelper.get(key);
        if (StringUtils.isBlank(value)) {
            synchronized (getSynchronizedKey(key)) {
                value = iRedisHelper.get(key);
                if (StringUtils.isBlank(value)) {
                    T t = supplier.get();
                    if (Objects.isNull(t)) {
                        t = defaultNullValue;
                    }
                    if (Objects.nonNull(t) && redisExpiresInSeconds > 0) {
                        iRedisHelper.set(key, LyJacksonUtil.toJson(t), redisExpiresInSeconds);
                    }
                    return t;
                }
            }
        }

        return LyJacksonUtil.fromJson(value, typeReference);
    }

    /**
     * Redis缓存(L2) + 泛型化(Generic) + synchronized + expireTime
     *
     * @param key
     * @param redisExpiresInSeconds
     * @param clz
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric2(String key, int redisExpiresInSeconds, Class<T> clz, Supplier<T> supplier) {
        String value = iRedisHelper.get(key);
        if (StringUtils.isBlank(value)) {
            synchronized (getSynchronizedKey(key)) {
                value = iRedisHelper.get(key);
                if (StringUtils.isBlank(value)) {
                    T t = supplier.get();
                    if (Objects.nonNull(t) && redisExpiresInSeconds > 0) {
                        iRedisHelper.set(key, LyJacksonUtil.toJson(t), redisExpiresInSeconds);
                    }
                    return t;
                }
            }
        }

        return LyJacksonUtil.fromJson(value, clz);
    }

    /**
     * Redis缓存(L2) + 泛型化(Generic) + synchronized + expireTime
     * 如果value=null时, 指定缓存默认值
     *
     * @param key
     * @param redisExpiresInSeconds
     * @param clz
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric2(String key, int redisExpiresInSeconds, T defaultNullValue, Class<T> clz,
                                Supplier<T> supplier) {
        String value = iRedisHelper.get(key);
        if (StringUtils.isBlank(value)) {
            synchronized (getSynchronizedKey(key)) {
                value = iRedisHelper.get(key);
                if (StringUtils.isBlank(value)) {
                    T t = supplier.get();
                    if (Objects.isNull(t)) {
                        t = defaultNullValue;
                    }
                    if (Objects.nonNull(t) && redisExpiresInSeconds > 0) {
                        iRedisHelper.set(key, LyJacksonUtil.toJson(t), redisExpiresInSeconds);
                    }
                    return t;
                }
            }
        }

        return LyJacksonUtil.fromJson(value, clz);
    }

    private String getSynchronizedKey(String key) {
        return SYNCHRONIZED_PREFIX + key;
    }
}
