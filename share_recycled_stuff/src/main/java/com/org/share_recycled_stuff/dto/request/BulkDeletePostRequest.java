package com.org.share_recycled_stuff.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to bulk delete multiple posts")
public class BulkDeletePostRequest {
    @Schema(
            description = "List of post IDs to delete",
            example = "[1, 2, 3, 4, 5]",
            required = true
    )
    @NotEmpty(message = "Danh sách post IDs không được để trống")
    private List<Long> postIds;

    @Schema(
            description = "Reason for deletion",
            example = "Vi phạm chính sách cộng đồng"
    )
    private String reason;
}

