package cn.ly.base_common.utils.log4j2;

import cn.ly.base_common.utils.error.LyThrowableUtil;
import cn.ly.base_common.utils.json.LyJsonUtil;
import cn.ly.base_common.utils.text.LyEscapeJsonUtil;
import cn.ly.base_common.utils.trace.LyTraceLogUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.util.IllegalFormatException;


/**
 * Created by liaomengge on 8/11/17.
 */
public class LyLogger implements Logger {

    private final Logger logger;

    private final static String WRAPPER = "";

    private final Class<?> clazz;

    public LyLogger(Class clazz) {
        this.clazz = clazz;
        logger = LoggerFactory.getLogger(clazz);
    }

    public static LyLogger getInstance(Class clazz) {
        return new LyLogger(clazz);
    }

    @Override
    public String getName() {
        return clazz.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public void trace(String format) {
        logger.trace(WRAPPER + addTraceAndEscapeJson(format) + WRAPPER);
    }

    @Override
    public void trace(String format, Object arg) {
        logger.trace(WRAPPER + replaceFormat(format, arg) + WRAPPER);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        logger.trace(WRAPPER + replaceFormat(format, arg1, arg2) + WRAPPER);
    }

    @Override
    public void trace(String format, Object... arguments) {
        logger.trace(WRAPPER + replaceFormat(format, arguments) + WRAPPER);
    }

    @Override
    public void trace(String format, Throwable t) {
        logger.trace(WRAPPER + addTraceAndEscapeJson(format) + " " +
                LyEscapeJsonUtil.escapeJson(LyThrowableUtil.getStackTrace(t, true)) + WRAPPER);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return true;
    }

    @Override
    public void trace(Marker marker, String format) {
        logger.trace(marker, WRAPPER + addTraceAndEscapeJson(format) + WRAPPER);
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        logger.trace(marker, WRAPPER + replaceFormat(format, arg) + WRAPPER);
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        logger.trace(marker, WRAPPER + replaceFormat(format, arg1, arg2) + WRAPPER);
    }

    @Override
    public void trace(Marker marker, String format, Object... arguments) {
        logger.trace(marker, WRAPPER + replaceFormat(format, arguments) + WRAPPER);
    }

    @Override
    public void trace(Marker marker, String format, Throwable t) {
        logger.trace(marker, WRAPPER + addTraceAndEscapeJson(format) + " " +
                LyEscapeJsonUtil.escapeJson(LyThrowableUtil.getStackTrace(t, true)) + WRAPPER);
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public void debug(String format) {
        logger.debug(WRAPPER + addTraceAndEscapeJson(format) + WRAPPER);
    }

    @Override
    public void debug(String format, Object arg) {
        logger.debug(WRAPPER + replaceFormat(format, arg) + WRAPPER);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        logger.debug(WRAPPER + replaceFormat(format, arg1, arg2) + WRAPPER);
    }

    @Override
    public void debug(String format, Object... arguments) {
        logger.debug(WRAPPER + replaceFormat(format, arguments) + WRAPPER);
    }

    @Override
    public void debug(String format, Throwable t) {
        logger.debug(WRAPPER + addTraceAndEscapeJson(format) + " " +
                LyEscapeJsonUtil.escapeJson(LyThrowableUtil.getStackTrace(t, true)) + WRAPPER);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return true;
    }

    public void debug(LyLogData logData) {
        logger.debug(formatLogData(logData));
    }

    @Override
    public void debug(Marker marker, String format) {
        logger.debug(marker, WRAPPER + addTraceAndEscapeJson(format) + WRAPPER);
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        logger.debug(marker, WRAPPER + replaceFormat(format, arg) + WRAPPER);
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        logger.debug(marker, WRAPPER + replaceFormat(format, arg1, arg2) + WRAPPER);
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        logger.debug(marker, WRAPPER + replaceFormat(format, arguments) + WRAPPER);
    }

    @Override
    public void debug(Marker marker, String format, Throwable t) {
        logger.debug(marker, WRAPPER + addTraceAndEscapeJson(format) + " " +
                LyEscapeJsonUtil.escapeJson(LyThrowableUtil.getStackTrace(t, true)) + WRAPPER);
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public void info(String format) {
        logger.info(WRAPPER + addTraceAndEscapeJson(format) + WRAPPER);
    }

    @Override
    public void info(String format, Object arg) {
        logger.info(WRAPPER + replaceFormat(format, arg) + WRAPPER);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        logger.info(WRAPPER + replaceFormat(format, arg1, arg2) + WRAPPER);
    }

    @Override
    public void info(String format, Object... arguments) {
        logger.info(WRAPPER + replaceFormat(format, arguments) + WRAPPER);
    }

    @Override
    public void info(String format, Throwable t) {
        logger.info(WRAPPER + addTraceAndEscapeJson(format) + " " +
                LyEscapeJsonUtil.escapeJson(LyThrowableUtil.getStackTrace(t, true)) + WRAPPER);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return true;
    }

    public void info(LyLogData logData) {
        logger.info(formatLogData(logData));
    }

    @Override
    public void info(Marker marker, String format) {
        logger.info(marker, WRAPPER + addTraceAndEscapeJson(format) + WRAPPER);
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        logger.info(marker, WRAPPER + replaceFormat(format, arg) + WRAPPER);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        logger.info(marker, WRAPPER + replaceFormat(format, arg1, arg2) + WRAPPER);
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        logger.info(marker, WRAPPER + replaceFormat(format, arguments) + WRAPPER);
    }

    @Override
    public void info(Marker marker, String format, Throwable t) {
        logger.info(marker, WRAPPER + addTraceAndEscapeJson(format) + " " +
                LyEscapeJsonUtil.escapeJson(LyThrowableUtil.getStackTrace(t, true)) + WRAPPER);
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void warn(String format) {
        logger.warn(WRAPPER + addTraceAndEscapeJson(format) + WRAPPER);
    }

    @Override
    public void warn(String format, Object arg) {
        logger.warn(WRAPPER + replaceFormat(format, arg) + WRAPPER);
    }

    @Override
    public void warn(String format, Object... arguments) {
        logger.warn(WRAPPER + replaceFormat(format, arguments) + WRAPPER);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        logger.warn(WRAPPER + replaceFormat(format, arg1, arg2) + WRAPPER);
    }

    @Override
    public void warn(String format, Throwable t) {
        logger.warn(WRAPPER + addTraceAndEscapeJson(format) + " " +
                LyEscapeJsonUtil.escapeJson(LyThrowableUtil.getStackTrace(t, true)) + WRAPPER);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return true;
    }

    public void warn(LyLogData logData) {
        logger.warn(formatLogData(logData));
    }

    @Override
    public void warn(Marker marker, String format) {
        logger.warn(marker, WRAPPER + addTraceAndEscapeJson(format) + WRAPPER);
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        logger.warn(marker, WRAPPER + replaceFormat(format, arg) + WRAPPER);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        logger.warn(marker, WRAPPER + replaceFormat(format, arg1, arg2) + WRAPPER);
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        logger.warn(marker, WRAPPER + replaceFormat(format, arguments) + WRAPPER);
    }

    @Override
    public void warn(Marker marker, String format, Throwable t) {
        logger.warn(marker, WRAPPER + addTraceAndEscapeJson(format) + " " +
                LyEscapeJsonUtil.escapeJson(LyThrowableUtil.getStackTrace(t, true)) + WRAPPER);
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public void error(String format) {
        logger.error(WRAPPER + addTraceAndEscapeJson(format) + WRAPPER);
    }

    @Override
    public void error(String format, Object arg) {
        logger.error(WRAPPER + replaceFormat(format, arg) + WRAPPER);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        logger.error(WRAPPER + replaceFormat(format, arg1, arg2) + WRAPPER);
    }

    @Override
    public void error(String format, Object... arguments) {
        logger.error(WRAPPER + replaceFormat(format, arguments) + WRAPPER);
    }

    @Override
    public void error(String format, Throwable t) {
        logger.error(WRAPPER + addTraceAndEscapeJson(format) + " " +
                LyEscapeJsonUtil.escapeJson(LyThrowableUtil.getStackTrace(t, true)) + WRAPPER);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return true;
    }

    public void error(LyLogData logData) {
        logger.error(formatLogData(logData));
    }

    @Override
    public void error(Marker marker, String format) {
        logger.error(marker, WRAPPER + addTraceAndEscapeJson(format) + WRAPPER);
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        logger.error(marker, WRAPPER + replaceFormat(format, arg) + WRAPPER);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        logger.error(marker, WRAPPER + replaceFormat(format, arg1, arg2) + WRAPPER);
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        logger.error(marker, WRAPPER + replaceFormat(format, arguments) + WRAPPER);
    }

    @Override
    public void error(Marker marker, String format, Throwable t) {
        logger.error(marker, WRAPPER + addTraceAndEscapeJson(format) + " " +
                LyEscapeJsonUtil.escapeJson(LyThrowableUtil.getStackTrace(t, true)) + WRAPPER);
    }

    public String formatLogData(LyLogData data) {
        if (data == null) {
            return "";
        }

        data.setTraceId(StringUtils.defaultString(LyTraceLogUtil.get(), "---"));
        return LyEscapeJsonUtil.escapeJson(LyJsonUtil.toJson4Log(data));
    }

    private String addTraceAndEscapeJson(String src) {
        if (StringUtils.isBlank(src)) {
            return "";
        }

        String traceId = LyTraceLogUtil.get();
        if (StringUtils.isBlank(traceId)) {
            return LyEscapeJsonUtil.escapeJson(src);
        }
        return "TraceID[" + traceId + "], " + LyEscapeJsonUtil.escapeJson(src);
    }

    private String replaceFormat(String format, Object... values) {
        format = StringUtils.replace(format, "{}", "%s");
        try {
            format = String.format(format, values);
        } catch (IllegalFormatException e) {
        }
        return addTraceAndEscapeJson(format);
    }
}