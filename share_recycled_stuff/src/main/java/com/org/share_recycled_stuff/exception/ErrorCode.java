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
    // User related
    USER_NOT_FOUND("USER_001", "User not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("USER_002", "User already exists", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS("USER_003", "Email already exists", HttpStatus.CONFLICT),
    USERNAME_ALREADY_EXISTS("USER_004", "Username already exists", HttpStatus.CONFLICT),

    // Item/Product related
    ITEM_NOT_FOUND("ITEM_001", "Item not found", HttpStatus.NOT_FOUND),
    ITEM_NOT_AVAILABLE("ITEM_002", "Item is not available", HttpStatus.BAD_REQUEST),
    ITEM_ALREADY_SHARED("ITEM_003", "Item has already been shared", HttpStatus.CONFLICT),

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
    SYSTEM_RESOURCE_NOT_FOUND("SYS_005", "The requested resource was not found", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
