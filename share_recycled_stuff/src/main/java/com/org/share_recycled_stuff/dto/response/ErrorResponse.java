package com.org.share_recycled_stuff.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Error response with details and optional field validation errors")
public class ErrorResponse {
    @Schema(description = "Request path that caused the error", example = "/api/posts")
    private String path;

    @Schema(description = "Error type", example = "Bad Request")
    private String error;

    @Schema(description = "Error code", example = "VALIDATION_ERROR")
    private String errorCode;

    @Schema(description = "Error message", example = "Invalid input data")
    private String message;

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Error timestamp", example = "2024-01-01T10:00:00")
    private LocalDateTime timestamp;

    @Schema(description = "List of field validation errors (if applicable)")
    private List<FieldError> fieldErrors;

    @Data
    @Builder
    @Schema(description = "Field validation error details")
    public static class FieldError {
        @Schema(description = "Field name", example = "email")
        private String field;

        @Schema(description = "Validation error message", example = "Email is required")
        private String message;
    }
}
