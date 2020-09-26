package cn.ly.service.base_framework.base;

import cn.ly.base_common.utils.date.LyJdk8DateUtil;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by liaomengge on 16/9/29.
 */
@Data
public abstract class BaseRequest implements Serializable {
    private static final long serialVersionUID = -8516599114094145340L;

    private String appId;
    private String sign;
    private String language = "zh-CN";
    private String timeZone = "GMT+8";
    private Long timestamp = LyJdk8DateUtil.getSecondTime();
}
