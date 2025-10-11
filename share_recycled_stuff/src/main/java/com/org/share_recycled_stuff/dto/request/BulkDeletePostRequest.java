package com.org.share_recycled_stuff.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkDeletePostRequest {
    @NotEmpty(message = "Danh sách post IDs không được để trống")
    private List<Long> postIds;

    private String reason;
}

