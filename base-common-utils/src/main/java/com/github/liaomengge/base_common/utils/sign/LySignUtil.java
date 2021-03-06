package com.github.liaomengge.base_common.utils.sign;


import com.github.liaomengge.base_common.utils.codec.LyDigestUtil;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by liaomengge on 17/1/10.
 */
@UtilityClass
public class LySignUtil {

    public String sign(String sign, Map<String, Object> params) {
        return sign(sign, params, "appKey");
    }

    /**
     * 过滤
     * 1. key is blank
     * 2. value=null
     * 加上补充的指定签名字段
     *
     * @param sign    签名的key
     * @param params  签名参数
     * @param signKey 签名后补充的签名key
     * @return
     */
    public String sign(String sign, Map<String, Object> params, String signKey) {
        if (params == null) {
            return "";
        }

        Map<String, Object> treeMap = new TreeMap<>();

        String key;
        Object value;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            key = entry.getKey();
            value = entry.getValue();
            if (StringUtils.isBlank(key) || value == null) {
                continue;
            }

            treeMap.put(key, value);
        }

        String params2Str = StringUtils.join(treeMap.entrySet(), "&") + "&" + signKey + "=" + sign;

        return LyDigestUtil.md5(params2Str);
    }
}
