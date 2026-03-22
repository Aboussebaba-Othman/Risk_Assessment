package com.riskassessment.scoring.security;

public class UserContextHolder {

    private static final ThreadLocal<Long> userContext = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        userContext.set(userId);
    }

    public static Long getUserId() {
        return userContext.get();
    }

    public static void clear() {
        userContext.remove();
    }
}
