package fr.osallek.mapsparser.common;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {

    private StringUtil() {
    }

    public static String clean(String s) {
        return StringUtils.normalizeSpace(StringUtils.trimToNull(s));
    }

    public static String capitalize(String s) {
        return StringUtils.capitalize(capitalize(s));
    }
}
