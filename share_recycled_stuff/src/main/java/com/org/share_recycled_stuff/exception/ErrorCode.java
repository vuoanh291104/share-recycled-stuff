package com.org.share_recycled_stuff.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Authentication & Authorization
    UNAUTHORIZED("AUTH_001", "Unauthorized access", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("AUTH_002", "Access denied", HttpStatus.FORBIDDEN),
    INVALID_TOKEN("AUTH_003", "Invalid token", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("AUTH_004", "Token has expired", HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS("AUTH_005", "Invalid username or password", HttpStatus.UNAUTHORIZED),
    ACCOUNT_DISABLED("AUTH_006", "Account has been disabled", HttpStatus.UNAUTHORIZED),
    ACCOUNT_LOCKED("AUTH_007", "Account has been locked", HttpStatus.UNAUTHORIZED),
    ACCOUNT_ALREADY_VERIFIED("AUTH_008", "Account has already been verified", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH("AUTH_009", "Password does not match", HttpStatus.BAD_REQUEST),
    INVALID_RESET_TOKEN("AUTH_010", "Invalid or expired reset token", HttpStatus.BAD_REQUEST),
    RESET_TOKEN_EXPIRED("AUTH_011", "Reset token has expired", HttpStatus.BAD_REQUEST),
    PASSWORD_CANNOT_BE_SAME("AUTH_012", "New password cannot be the same as current password", HttpStatus.BAD_REQUEST),
    INVALID_CURRENT_PASSWORD("AUTH_013", "Current password is incorrect", HttpStatus.BAD_REQUEST),
    // User related
    USER_NOT_FOUND("USER_001", "User not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("USER_002", "User already exists", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS("USER_003", "Email already exists", HttpStatus.CONFLICT),
    USERNAME_ALREADY_EXISTS("USER_004", "Username already exists", HttpStatus.CONFLICT),
    ACCOUNT_NOT_FOUND("USER_005", "Account not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_HAS_ROLE("USER_006", "User already has this role", HttpStatus.CONFLICT),
    USER_DOES_NOT_HAVE_ROLE("USER_007", "User does not have this role", HttpStatus.BAD_REQUEST),
    CANNOT_REMOVE_LAST_ADMIN("USER_008", "Cannot remove the last admin role", HttpStatus.BAD_REQUEST),
    USER_MUST_HAVE_AT_LEAST_ONE_ROLE("USER_009", "User must have at least one role", HttpStatus.BAD_REQUEST),

    // Item/Product related
    ITEM_NOT_FOUND("ITEM_001", "Item not found", HttpStatus.NOT_FOUND),
    ITEM_NOT_AVAILABLE("ITEM_002", "Item is not available", HttpStatus.BAD_REQUEST),
    ITEM_ALREADY_SHARED("ITEM_003", "Item has already been shared", HttpStatus.CONFLICT),

    //Category
    CATEGORY_NOT_FOUND("CATEGORY_001", "Category not found", HttpStatus.NOT_FOUND),

    //Post
    POST_NOT_FOUND("POST_001", "Post not found", HttpStatus.NOT_FOUND),
    POST_ALREADY_DELETED("POST_002", "Post is deleted", HttpStatus.GONE),
    POST_NOT_DELETED("POST_003", "Post is not deleted", HttpStatus.BAD_REQUEST),
    INVALID_STATUS_TRANSITION("POST_004", "Invalid status transition", HttpStatus.BAD_REQUEST),

    //Image
    IMAGE_NOT_FOUND("IMAGE_001", "Image not found", HttpStatus.NOT_FOUND),
    
    //Report
    REPORT_NOT_FOUND("REPORT_001", "Report not found", HttpStatus.NOT_FOUND),
    ALREADY_REPORTED_POST("REPORT_002", "You have already reported this post", HttpStatus.CONFLICT),
    ALREADY_REPORTED_USER("REPORT_003", "You have already reported this user", HttpStatus.CONFLICT),
    INVALID_REPORT_TYPE("REPORT_004", "Invalid report type", HttpStatus.BAD_REQUEST),
    CANNOT_REPORT_OWN_POST("REPORT_005", "Cannot report your own post", HttpStatus.BAD_REQUEST),
    CANNOT_REPORT_YOURSELF("REPORT_006", "Cannot report yourself", HttpStatus.BAD_REQUEST),
    INVALID_REPORT_TARGET("REPORT_007", "Invalid report target - must specify either post or account", HttpStatus.BAD_REQUEST),
    // Validation
    INVALID_INPUT("VAL_001", "Invalid input: %s", HttpStatus.BAD_REQUEST),
    MISSING_REQUIRED_FIELD("VAL_002", "Missing required field: %s", HttpStatus.BAD_REQUEST),
    INVALID_FILE_FORMAT("VAL_003", "Invalid file format", HttpStatus.BAD_REQUEST),
    FILE_SIZE_EXCEEDED("VAL_004", "File size exceeds maximum limit", HttpStatus.BAD_REQUEST),
    TYPE_MISMATCH("VAL_005", "Type mismatch", HttpStatus.BAD_REQUEST),

    // Business logic
    RESOURCE_NOT_FOUND("BUS_001", "Resource not found: %s", HttpStatus.NOT_FOUND),
    OPERATION_NOT_ALLOWED("BUS_002", "Operation not allowed: %s", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST("BUS_003", "Invalid request", HttpStatus.BAD_REQUEST),

    // System
    INTERNAL_ERROR("SYS_001", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE("SYS_002", "Service temporarily unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    DATABASE_ERROR("SYS_003", "Database error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    METHOD_NOT_SUPPORTED("SYS_004", "HTTP method not supported", HttpStatus.METHOD_NOT_ALLOWED),
    SYSTEM_RESOURCE_NOT_FOUND("SYS_005", "The requested resource was not found", HttpStatus.NOT_FOUND),
    EMAIL_SENDING_FAILED("SYS_006", "Sending email failed", HttpStatus.INTERNAL_SERVER_ERROR),
    DATA_INTEGRITY_ERROR("SYS_007", "Data integrity error", HttpStatus.INTERNAL_SERVER_ERROR);
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
