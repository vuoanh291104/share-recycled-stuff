package com.org.share_recycled_stuff.constants;

/**
 * Constants for authentication and security
 */
public final class AuthConstants {
    
    // Private constructor to prevent instantiation
    private AuthConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    // Login Configuration
    public static final int DEFAULT_MAX_LOGIN_ATTEMPTS = 5;
    public static final int DEFAULT_ACCOUNT_LOCK_DURATION_MINUTES = 30;
    
    // Default Messages
    public static final String LOGIN_SUCCESS_MESSAGE = "Login successful";
    
    // Lock Reasons
    public static final String LOCK_REASON_TOO_MANY_ATTEMPTS = "Too many failed login attempts";
    public static final String LOCK_REASON_ADMIN_ACTION = "Locked by administrator";
    public static final String LOCK_REASON_SUSPICIOUS_ACTIVITY = "Suspicious activity detected";
}
