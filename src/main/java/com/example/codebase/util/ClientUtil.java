package com.example.codebase.util;

import javax.servlet.http.HttpServletRequest;

public class ClientUtil {

    public static String getRemoteIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        // Proxy
        if (ip == null || ip.length() == 0) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        // Weblogic
        if (ip == null || ip.length() == 0) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }

        if (ip == null || ip.length() == 0) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        if (ip == null || ip.length() == 0) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }
}
