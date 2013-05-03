package com.alvin.common.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * URL工具
 */
public class URLUtils {

    public static String encodeURL(String url) throws UnsupportedEncodingException {
        return URLEncoder.encode(url, "utf-8");
    }

    public static void main(String[] args) {
        try {
            System.out.println("URL = " + encodeURL("http://v22.pconline.com.cn:8080/intf/tags/dress.jsp"));
            System.out.println("URL = " + encodeURL("http://v22.pconline.com.cn:8080/intf/tags/brand.jsp"));
            System.out.println("URL = " + encodeURL("http://v22.pconline.com.cn:8080/intf/tags/style.jsp"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
