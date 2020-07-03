package cn.ly.base_common.utils.codec;

import cn.ly.base_common.support.misc.Encodings;
import lombok.experimental.UtilityClass;

import java.io.UnsupportedEncodingException;

/**
 * Created by liaomengge on 16/10/17.
 */
@UtilityClass
public class LyCharsetUtil {

    public String UTF2GBK(String value) {
        try {
            return new String(value.getBytes(), Encodings.GBK);
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    public String GBK2UTF(String value) {
        try {
            return new String(value.getBytes(), Encodings.UTF_8);
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }
}