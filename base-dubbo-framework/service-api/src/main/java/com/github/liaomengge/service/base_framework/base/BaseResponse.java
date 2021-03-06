package com.github.liaomengge.service.base_framework.base;

import com.github.liaomengge.base_common.utils.string.LyToStringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by liaomengge on 16/4/12.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseResponse implements Serializable {

    private static final long serialVersionUID = -8750943144463247125L;

    /**
     * 状态 000000表示成功
     */
    private String code;

    /**
     * 提示消息
     */
    private String msg;

    @Override
    public String toString() {
        return LyToStringUtil.toString(this);
    }
}
