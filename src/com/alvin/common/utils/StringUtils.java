package com.alvin.common.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StringUtils {
    public static boolean isBlank(String org) {
        return org == null || "".equals(org);
    }

    public static String formatDate2String(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }
    
    public static List<String> String2List(String source) {
        List<String> list = new ArrayList<String>();
        String[] urlStrings = new String[] {};
        if (source.contains(",")) {
            urlStrings = source.split(",");
            for (int i = 0; i < urlStrings.length; i++) {
                list.add(urlStrings[i]);
            }
        } else {
            list.add(source);
        }

        return list;
    }

    public static String List2String(List<String> sourceList) {
        String dest = null;
        for (int i = 0; i < sourceList.size(); i++) {
            if (i == sourceList.size() - 1) {
                dest += sourceList.get(i);
            } else {
                dest += sourceList.get(i) + ",";
            }
        }
        return dest;
    }
}
