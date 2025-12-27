package com.hedon.utils;

import java.net.InetSocketAddress;

import org.springframework.http.server.reactive.ServerHttpRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * IP 工具类
 */
public class IPUtil {

    /**
     * 获取请求 IP：
     * 用户的真实 IP 不能使用 request.getRemoteAddr()，
     * 这是因为可能会使用一些代理软件，这样 IP 获取就不准确了。
     * 此时我们如果使用了多级（LVS/Nginx）反向代理的话，
     * IP需要从 X-FORWARDED-FOR 中获取第一个非 unknown 的 IP 地址。
     *
     * @param request
     * @return
     */
    public static String getRequestIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // X-Forwarded-For 可能包含多个 IP，用逗号分隔，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    /**
     * 获取 IP 地址
     * 
     * @param request
     * @return
     */
    public static String getIP(ServerHttpRequest request) {
        String ip = request.getHeaders().getFirst("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            if (request.getRemoteAddress() != null) {
                InetSocketAddress remoteAddress = request.getRemoteAddress();
                if (remoteAddress != null) {
                    ip = remoteAddress.getAddress().getHostAddress();
                }
            }
        }

        // X-Forwarded-For 可能包含多个 IP，用逗号分隔，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }
}
