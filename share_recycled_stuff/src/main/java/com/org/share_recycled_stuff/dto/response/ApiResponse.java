package com.org.share_recycled_stuff.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response wrapper with status code, message, and result data")
public class ApiResponse<T> {
    @Schema(description = "HTTP status code", example = "200")
    private int code;

    @Schema(description = "Response message (Vietnamese)", example = "Thao tác thành công")
    private String message;

    @Schema(description = "API endpoint path", example = "/api/posts")
    private String path;

    @Schema(description = "Response timestamp (ISO 8601)", example = "2024-01-01T10:00:00Z")
    private String timestamp;

    @Schema(description = "Result data (type varies by endpoint)")
    private T result;

    public static <T> ApiResponse<T> success(String message, T result) {
        return ApiResponse.<T>builder()
                .code(200)
                .message(message)
                .timestamp(Instant.now().toString())
                .result(result)
                .build();
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .timestamp(Instant.now().toString())
                .build();
    }

    public static <T> ApiResponse<T> error(int code, String message, String path) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .path(path)
                .timestamp(Instant.now().toString())
                .build();
    }

    public static <T> ApiResponse<T> noti(int code, String message, String path) {
        return ApiResponse.<T>builder()
                .code(code)
                .path(path)
                .message(message)
                .timestamp(Instant.now().toString())
                .build();
    }
}
//Ở đây có thể custom api trả ra tùy ý
