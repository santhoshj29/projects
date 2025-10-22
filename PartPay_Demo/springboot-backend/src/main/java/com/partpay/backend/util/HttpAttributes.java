package com.partpay.backend.util;

import jakarta.servlet.http.HttpServletRequest;

public final class HttpAttributes {
    private HttpAttributes() {}

    public static Integer getUserId(HttpServletRequest request) {
        return (Integer) request.getAttribute("user_id");
    }

    public static Integer getOrgId(HttpServletRequest request) {
        return (Integer) request.getAttribute("org_id");
    }

    public static String getOrgName(HttpServletRequest request) {
        return (String) request.getAttribute("org_name");
    }

    public static String getRole(HttpServletRequest request) {
        return (String) request.getAttribute("role");
    }
}
