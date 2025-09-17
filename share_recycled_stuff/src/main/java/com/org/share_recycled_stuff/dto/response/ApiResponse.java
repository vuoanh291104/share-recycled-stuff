package com.org.share_recycled_stuff.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class ApiResponse<T> {
    private int code;
    private String message;
    private String path;
    private String timestamp;
    private T result;

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
