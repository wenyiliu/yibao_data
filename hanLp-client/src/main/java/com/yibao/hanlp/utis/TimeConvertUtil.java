package com.yibao.hanlp.utis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 时间转换工具
 *
 * @author liuwenyi
 * @date 2019/11/25
 */
public final class TimeConvertUtil {

    private static final String YEAR = "([0-9]{2,4})[-|年|/|.]";
    private static final String MONTH = "([0-9]{1,2})[-|月|/|.]";
    private static final String DAY = "([0-9]{1,2})[日|号|\\s]";
    private static final String HOUR = "([0-9]{1,2})[时|点|:|：]";
    private static final String MINUTE = "([0-9]{1,2})[分|:|：]";
    private static final String SECOND = "([0-9]{1,2})秒";
    private static final String SUFFIX = "?";
    private static final String SIZE = "^([0-9]{1,4})[*|x|X]([0-9]{1,4})";

    private static final String ZERO = "0";

    private TimeConvertUtil() {
    }

    public static String convert(String text) {
        Pattern p = Pattern.compile(SIZE);
        Matcher m = p.matcher(text);
        if (m.find()) {
            return text;
        }
        text = text.replaceAll(YEAR + MONTH + DAY + HOUR + MINUTE + SECOND + SUFFIX, "$1年$2月$3日$4时$5分$6秒");
        text = text.replaceAll(YEAR + MONTH + DAY + HOUR + MINUTE + SUFFIX, "$1年$2月$3日$4时$5分");
        text = text.replaceAll(YEAR + MONTH + DAY + HOUR + SUFFIX, "$1年$2月$3日$4时");
        text = text.replaceAll(YEAR + MONTH + DAY + SUFFIX, "$1年$2月$3日");
        text = text.replaceAll(YEAR + MONTH + SUFFIX, "$1年$2月");
        text = trimFirstChar(text, MONTH);
        text = trimFirstChar(text, DAY);
        text = trimFirstChar(text, HOUR);
        text = trimFirstChar(text, MINUTE);
        text = trimFirstChar(text, SECOND);
        return text;
    }

    private static String trimFirstChar(String text, String compile) {
        Pattern month = Pattern.compile(compile);
        Matcher matcher = month.matcher(text);
        if (!matcher.find()) {
            return text;
        }
        String group = matcher.group();
        if (!group.startsWith(ZERO)) {
            return text;
        }
        String newChar = group.substring(1);
        return text.replace(group, newChar);
    }
}
