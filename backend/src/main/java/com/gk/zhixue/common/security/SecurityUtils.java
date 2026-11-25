package com.gk.zhixue.common.security;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        return null;
    }

    public static boolean isAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() != null &&
               SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
               !"anonymousUser".equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}